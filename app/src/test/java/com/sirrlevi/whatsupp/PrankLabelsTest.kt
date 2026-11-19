package com.sirrlevi.whatsupp

import com.sirrlevi.whatsupp.ui.PrankLabels
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PrankLabelsTest {
    @Test fun watermarkSaysPrank() {
        assertEquals("PRANK", PrankLabels.WATERMARK)
    }

    @Test fun headerAndFooterCarryTheRequestedText() {
        listOf(PrankLabels.HEADER, PrankLabels.FOOTER).forEach { label ->
            assertTrue(label, label.contains("PRANK APK"))
            assertTrue(label, label.contains("CHAT GENERATOR"))
            assertTrue(label, label.contains("PRANK FUN"))
        }
    }

    @Test fun watermarkIsLargeAndVisible() {
        assertTrue(PrankLabels.WATERMARK_SIZE_SP >= 72)
        assertTrue(PrankLabels.WATERMARK_FILL_ALPHA >= 0.2f)
        assertTrue(PrankLabels.WATERMARK_OUTLINE_ALPHA >= 0.2f)
        assertTrue(PrankLabels.WATERMARK_ROTATION_DEGREES != 0f)
    }

    @Test fun everyLabelIsNonBlank() {
        assertEquals(3, PrankLabels.all.size)
        PrankLabels.all.forEach { assertTrue(it.isNotBlank()) }
    }
}
