package com.sirrlevi.whatsupp

import com.sirrlevi.whatsupp.update.Stage
import com.sirrlevi.whatsupp.update.UpdateRules
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UpdateRulesTest {
    @Test fun onlyHigherVersionCodesAreUpdates() {
        assertTrue(UpdateRules.isNewer(1033, 1034))
        assertFalse(UpdateRules.isNewer(1034, 1034))
        assertFalse(UpdateRules.isNewer(1035, 1034))
    }

    @Test fun sha256MatchesKnownVector() {
        assertEquals(
            "ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad",
            UpdateRules.sha256Hex("abc".toByteArray())
        )
    }

    @Test fun hashComparisonIgnoresCaseAndWhitespace() {
        assertTrue(UpdateRules.sameHash(" ABCDEF ", "abcdef"))
        assertFalse(UpdateRules.sameHash("abcdef", "abcdee"))
    }

    @Test fun badTokenMessageSaysToCreateANewOne() {
        assertTrue(UpdateRules.errorMessage(401, Stage.REPOSITORY).contains("new token"))
    }

    @Test fun missingRepositoryMessageNamesTheRepository() {
        val message = UpdateRules.errorMessage(404, Stage.REPOSITORY)
        assertTrue(message.contains("Sirrlevi/Whatsupp"))
    }

    @Test fun missingReleaseMessageNamesTheReleaseTag() {
        assertTrue(UpdateRules.errorMessage(404, Stage.RELEASE).contains(UpdateRules.RELEASE_TAG))
    }

    @Test fun rateLimitIsExplainedAndGithubTextIsKept() {
        val message = UpdateRules.errorMessage(403, Stage.RELEASE, "API rate limit exceeded")
        assertTrue(message.contains("request limit"))
        assertTrue(message.contains("API rate limit exceeded"))
    }

    @Test fun forbiddenWithoutRateLimitPointsAtContentsPermission() {
        assertTrue(UpdateRules.errorMessage(403, Stage.FILE).contains("Contents"))
    }

    @Test fun unknownStatusIsReportedWithItsNumber() {
        assertTrue(UpdateRules.errorMessage(500, Stage.FILE).contains("500"))
    }

    @Test fun sizesAreHumanReadable() {
        assertEquals("", UpdateRules.formatSize(0))
        assertEquals("512 KB", UpdateRules.formatSize(512L * 1024L))
        assertTrue(UpdateRules.formatSize(12L * 1024L * 1024L).endsWith("MB"))
    }
}
