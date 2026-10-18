package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.repository.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class PersonalTrackerRepositoryTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val prefs get() = context.getSharedPreferences(PersonalTrackerRepository.PREFS_NAME, Context.MODE_PRIVATE)

    @Before fun reset() { prefs.edit().clear().commit() }

    @Test fun clawssaryStartsPublishedAndDoesNotReappearAfterDeletion() {
        val app = PersonalTrackerRepository.getEntries(prefs, TrackerKind.PLAY_STORE).single()
        assertEquals("Clawssary", app.name)
        assertEquals("Yayında", TrackerKind.PLAY_STORE.states[app.status])
        PersonalTrackerRepository.delete(prefs, TrackerKind.PLAY_STORE, app.id)
        assertTrue(PersonalTrackerRepository.getEntries(prefs, TrackerKind.PLAY_STORE).isEmpty())
    }

    @Test fun editingTrickPreservesRequirementsAndNotesWithoutDuplicatingIt() {
        val trick = TrackerEntry(name = "Double lift", requirements = "Standart deste", notes = "İki kartı birlikte çevir.")
        PersonalTrackerRepository.save(prefs, TrackerKind.CARD_TRICKS, trick)
        PersonalTrackerRepository.save(prefs, TrackerKind.CARD_TRICKS, trick.copy(status = 2, notes = "Serçe parmakla boşluk tut."))
        val saved = PersonalTrackerRepository.getEntries(prefs, TrackerKind.CARD_TRICKS).single()
        assertEquals(trick.id, saved.id)
        assertEquals("Standart deste", saved.requirements)
        assertEquals("Serçe parmakla boşluk tut.", saved.notes)
        assertEquals(2, saved.status)
        assertTrue(PersonalTrackerRepository.getEntries(prefs, TrackerKind.GITHUB).isEmpty())
    }

    @Test fun projectTypeLinkAndNextStepSurviveReload() {
        val project = TrackerEntry(name = "API", discipline = "Backend", status = 1,
            url = "https://github.com/example/api", notes = "Kimlik doğrulamayı ekle.")
        PersonalTrackerRepository.save(prefs, TrackerKind.GITHUB, project)
        assertEquals(project, PersonalTrackerRepository.getEntries(prefs, TrackerKind.GITHUB).single())
    }

    @Test fun malformedStorageIsNotOverwrittenBySave() {
        prefs.edit().putString(TrackerKind.GITHUB.name, "broken-json").commit()
        assertTrue(runCatching { PersonalTrackerRepository.save(prefs, TrackerKind.GITHUB, TrackerEntry(name = "Test")) }.isFailure)
        assertEquals("broken-json", prefs.getString(TrackerKind.GITHUB.name, null))
    }
}
