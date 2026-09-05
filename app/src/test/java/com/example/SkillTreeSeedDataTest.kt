package com.example

import com.example.data.model.BranchId
import com.example.data.model.SkillStatus
import com.example.data.seed.SkillTreeSeed
import org.junit.Assert.*
import org.junit.Test

class SkillTreeSeedDataTest {

    @Test
    fun verifySeedContainsAllBranches() {
        val skills = SkillTreeSeed.getInitialSkills()
        assertNotNull(skills)
        assertTrue("Seed skills should not be empty", skills.isNotEmpty())

        val presentBranches = skills.map { it.branchId }.distinct()
        for (branch in BranchId.values()) {
            assertTrue("Branch ${branch.title} should have skill nodes", presentBranches.contains(branch))
        }
    }

    @Test
    fun verifyDotNetBackendPriorityCoverage() {
        val skills = SkillTreeSeed.getInitialSkills()
        val backendSkills = skills.filter { it.branchId == BranchId.BACKEND_DOTNET }
        assertTrue("Backend .NET branch must have at least 15 comprehensive nodes", backendSkills.size >= 15)

        // Check essential .NET categories
        val categories = backendSkills.map { it.category }.distinct()
        assertTrue(categories.contains("C#"))
        assertTrue(categories.contains("ASP.NET Core"))
        assertTrue(categories.contains("Database"))
        assertTrue(categories.contains("Architecture"))
        assertTrue(categories.contains("Production Backend"))
    }

    @Test
    fun verifyCanonicalBooks() {
        val books = SkillTreeSeed.referenceBooks
        assertEquals(4, books.size)
        assertTrue(books.any { it.title.contains("Three Easy Pieces") })
        assertTrue(books.any { it.title.contains("Programmer's Perspective") })
        assertTrue(books.any { it.title.contains("Top-Down Approach") })
        assertTrue(books.any { it.title.contains("Data-Intensive Applications") })
    }

    @Test
    fun verifyKnowledgeLoopSteps() {
        val steps = SkillTreeSeed.knowledgeLoopSteps
        assertEquals(6, steps.size)
        assertEquals("LEARN", steps[0].name)
        assertEquals("APPLY", steps[1].name)
        assertEquals("WRITE NOTES", steps[2].name)
        assertEquals("BUILD", steps[3].name)
        assertEquals("WRITE MEDIUM ARTICLE", steps[4].name)
        assertEquals("REVIEW LATER", steps[5].name)
    }

    @Test
    fun verifyInitialProjects() {
        val projects = SkillTreeSeed.initialProjects
        assertTrue(projects.isNotEmpty())
        assertTrue(projects.any { it.title.contains("Pharmacy") })
        assertTrue(projects.any { it.title.contains("KV Cache") })
    }
}
