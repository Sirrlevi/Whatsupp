package com.sirrlevi.whatsupp

import com.sirrlevi.whatsupp.ui.ChatFormatting
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ChatFormattingTest {
    @Test fun imagePreviewUsesMediaLabel() {
        assertEquals("Photo", ChatFormatting.preview("", "IMAGE"))
    }

    @Test fun longPreviewIsBounded() {
        val result = ChatFormatting.preview("x".repeat(80), "TEXT")
        assertEquals(46, result.length)
        assertTrue(result.endsWith("…"))
    }

    @Test fun reactionsCanBeAddedAndRemoved() {
        val one = ChatFormatting.toggleReaction("", "❤️")
        assertEquals(listOf("❤️"), ChatFormatting.reactions(one))
        val two = ChatFormatting.toggleReaction(one, "😂")
        assertEquals(listOf("❤️", "😂"), ChatFormatting.reactions(two))
        val three = ChatFormatting.toggleReaction(two, "❤️")
        assertEquals(listOf("😂"), ChatFormatting.reactions(three))
    }

    @Test fun dateTimeEditorRoundTrips() {
        val original = ChatFormatting.parseDateTime("2026-11-18 10:53")
        assertEquals("2026-11-18 10:53", ChatFormatting.dateTimeForEditor(original))
    }

    @Test fun malformedDateFallsBackInsteadOfCrashing() {
        assertEquals(1234L, ChatFormatting.parseDateTime("not-a-date", 1234L))
    }
}
