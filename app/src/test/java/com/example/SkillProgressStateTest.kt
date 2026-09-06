package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.db.AppDatabase
import com.example.data.model.SkillStatus
import com.example.data.seed.SkillTreeSeed
import com.example.data.state.SkillProgressManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class SkillProgressStateTest {

    private lateinit var database: AppDatabase
    private lateinit var progressManager: SkillProgressManager

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        progressManager = SkillProgressManager(
            skillDao = database.skillDao(),
            initialSkills = SkillTreeSeed.getInitialSkills()
        )
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun testSkillStatusParsing() {
        assertEquals(SkillStatus.NOT_STARTED, SkillStatus.fromString("NOT_STARTED"))
        assertEquals(SkillStatus.NOT_STARTED, SkillStatus.fromString("Not Started"))
        assertEquals(SkillStatus.IN_PROGRESS, SkillStatus.fromString("IN_PROGRESS"))
        assertEquals(SkillStatus.IN_PROGRESS, SkillStatus.fromString("In Progress"))
        assertEquals(SkillStatus.IN_PROGRESS, SkillStatus.fromString("LEARNING"))
        assertEquals(SkillStatus.COMPLETED, SkillStatus.fromString("COMPLETED"))
        assertEquals(SkillStatus.COMPLETED, SkillStatus.fromString("Completed"))
        assertEquals(SkillStatus.STRONG, SkillStatus.fromString("STRONG"))
        assertEquals(SkillStatus.STRONG, SkillStatus.fromString("Mastered"))
    }

    @Test
    fun testSetSkillStatusAndPersistToRoom() = runBlocking {
        val testSkillId = SkillTreeSeed.getInitialSkills().first().id
        
        // Update to IN_PROGRESS
        progressManager.setSkillStatus(testSkillId, SkillStatus.IN_PROGRESS)

        val updatedEntity = database.skillDao().getSkillProgressById(testSkillId)
        assertNotNull("Progress entity should be saved in Room", updatedEntity)
        assertEquals("IN_PROGRESS", updatedEntity?.status)

        // Verify skillsFlow reflects the updated progress
        val skills = progressManager.skillsFlow.first()
        val node = skills.find { it.id == testSkillId }
        assertNotNull(node)
        assertEquals(SkillStatus.IN_PROGRESS, node?.status)

        // Update to COMPLETED
        progressManager.setSkillStatus(testSkillId, SkillStatus.COMPLETED)
        val completedEntity = database.skillDao().getSkillProgressById(testSkillId)
        assertEquals("COMPLETED", completedEntity?.status)
    }

    @Test
    fun testCycleSkillStatusProgression() = runBlocking {
        val testSkillId = SkillTreeSeed.getInitialSkills().first().id

        // Set baseline to NOT_STARTED
        progressManager.setSkillStatus(testSkillId, SkillStatus.NOT_STARTED)

        // Cycle 1: Not Started -> In Progress
        val s1 = progressManager.cycleSkillStatus(testSkillId)
        assertEquals(SkillStatus.IN_PROGRESS, s1)

        // Cycle 2: In Progress -> Completed
        val s2 = progressManager.cycleSkillStatus(testSkillId)
        assertEquals(SkillStatus.COMPLETED, s2)

        // Cycle 3: Completed -> Mastered (Strong)
        val s3 = progressManager.cycleSkillStatus(testSkillId)
        assertEquals(SkillStatus.STRONG, s3)

        // Cycle 4: Mastered -> Not Started
        val s4 = progressManager.cycleSkillStatus(testSkillId)
        assertEquals(SkillStatus.NOT_STARTED, s4)
    }

    @Test
    fun testProgressSummaryMetrics() = runBlocking {
        val seedSkills = SkillTreeSeed.getInitialSkills()
        val testSkill1 = seedSkills[0].id
        val testSkill2 = seedSkills[1].id

        progressManager.setSkillStatus(testSkill1, SkillStatus.COMPLETED)
        progressManager.setSkillStatus(testSkill2, SkillStatus.IN_PROGRESS)

        val summary = progressManager.progressSummaryFlow.first()
        assertTrue("Total skills should be greater than 0", summary.totalSkills > 0)
        assertTrue("Completed count should be at least 1", summary.completedCount >= 1)
        assertTrue("In Progress count should be at least 1", summary.inProgressCount >= 1)
    }

    @Test
    fun testCompletingSkillAwardsXpAndUnlocksBadge() = runBlocking {
        val testSkill = SkillTreeSeed.getInitialSkills().first()
        
        // Complete the first skill
        progressManager.setSkillStatus(testSkill.id, SkillStatus.COMPLETED)

        // Verify Room persistence of UserXpEntity
        val userXpEntity = database.skillDao().getUserXp()
        assertNotNull("User XP entity should be created in Room", userXpEntity)
        assertTrue("Total XP should be > 0 after completing skill", (userXpEntity?.totalXp ?: 0) > 0)
        assertTrue("Completed skill IDs should track this skill", userXpEntity?.completedSkillIdsCsv?.contains(testSkill.id) == true)

        // Verify first commit badge was awarded
        assertTrue("First skill badge should be unlocked", userXpEntity?.unlockedBadgeIdsCsv?.contains("badge_first_skill") == true)

        // Verify userXpFlow
        val profile = progressManager.userXpFlow.first()
        assertTrue(profile.totalXp >= 150)
        assertTrue(profile.unlockedBadges.any { it.id == "badge_first_skill" })
        assertTrue("Completed skill count should be at least 1", profile.completedSkillCount >= 1)
    }

    @Test
    fun testDuplicateCompletionDoesNotDuplicateXp() = runBlocking {
        val testSkill = SkillTreeSeed.getInitialSkills().first()

        // Complete the skill once
        progressManager.setSkillStatus(testSkill.id, SkillStatus.COMPLETED)
        val initialXp = database.skillDao().getUserXp()?.totalXp ?: 0

        // Set to Strong
        progressManager.setSkillStatus(testSkill.id, SkillStatus.STRONG)
        val secondXp = database.skillDao().getUserXp()?.totalXp ?: 0

        // XP should not duplicate
        assertEquals("XP should not be duplicated when status changes within completion", initialXp, secondXp)
    }

    @Test
    fun testDailyFocusSkillSelectionAndCycling() {
        val initialSkills = SkillTreeSeed.getInitialSkills().mapIndexed { index, skill ->
            if (index < 2) skill.copy(status = SkillStatus.IN_PROGRESS) else skill
        }
        val inProgressList = initialSkills.filter { it.status == SkillStatus.IN_PROGRESS }
        
        assertTrue("Seed data should have in-progress skills", inProgressList.isNotEmpty())

        val uiState = com.example.ui.SkillTreeUiState(
            allSkills = initialSkills,
            dailyFocusSkillIndex = 0
        )

        assertEquals(inProgressList.size, uiState.inProgressSkills.size)
        assertEquals(inProgressList.first().id, uiState.currentDailyFocusSkill?.id)

        // Test cycling index
        val cycledUiState = uiState.copy(dailyFocusSkillIndex = 1)
        if (inProgressList.size > 1) {
            assertEquals(inProgressList[1].id, cycledUiState.currentDailyFocusSkill?.id)
        }
    }

    @Test
    fun testDailyBannerDismissState() {
        var uiState = com.example.ui.SkillTreeUiState(isDailyBannerDismissed = false)
        assertFalse(uiState.isDailyBannerDismissed)

        uiState = uiState.copy(isDailyBannerDismissed = true)
        assertTrue(uiState.isDailyBannerDismissed)
    }

    @Test
    fun testAttachPersonalNotesToSkillNode() = runBlocking {
        val testSkill = SkillTreeSeed.getInitialSkills().first { it.id == "net_cs_oop" }
        val testNote = "💡 Key Takeaway: Prefer composition over inheritance.\n📚 Resource: https://refactoring.guru/design-patterns"

        // Attach personal note
        progressManager.saveSkillNotes(testSkill.id, testNote)

        // Verify direct entity in Room database
        val savedEntity = database.skillDao().getSkillProgressById(testSkill.id)
        assertNotNull("Skill progress entity should exist in Room", savedEntity)
        assertEquals(testNote, savedEntity?.personalNotes)

        // Verify reactive skillsFlow emits the attached note
        val updatedSkills = progressManager.skillsFlow.first()
        val emittedSkill = updatedSkills.first { it.id == testSkill.id }
        assertEquals(testNote, emittedSkill.personalNotes)
        assertTrue("Skill should indicate note presence", emittedSkill.personalNotes.isNotBlank())
    }

    @Test
    fun testUpdateAndClearPersonalNotes() = runBlocking {
        val testSkill = SkillTreeSeed.getInitialSkills().first { it.id == "net_cs_generics" }
        val note1 = "💡 Key Takeaway: where T : class enforces reference type constraint."
        val note2 = "$note1\n⚠️ Gotcha: Cannot new() constraint with parameters."

        // First save
        progressManager.saveSkillNotes(testSkill.id, note1)
        assertEquals(note1, database.skillDao().getSkillProgressById(testSkill.id)?.personalNotes)

        // Update/expand note
        progressManager.saveSkillNotes(testSkill.id, note2)
        assertEquals(note2, database.skillDao().getSkillProgressById(testSkill.id)?.personalNotes)

        // Clear note
        progressManager.saveSkillNotes(testSkill.id, "")
        assertEquals("", database.skillDao().getSkillProgressById(testSkill.id)?.personalNotes)

        val updatedSkills = progressManager.skillsFlow.first()
        val emittedSkill = updatedSkills.first { it.id == testSkill.id }
        assertEquals("", emittedSkill.personalNotes)
    }
}

