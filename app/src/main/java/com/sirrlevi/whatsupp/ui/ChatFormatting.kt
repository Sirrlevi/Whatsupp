package com.sirrlevi.whatsupp.ui

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ChatFormatting {
    fun preview(body: String, kind: String): String = when (kind) {
        "IMAGE" -> if (body.isBlank()) "Photo" else "Photo · $body"
        "VIDEO" -> "Video"
        "AUDIO" -> "Voice message"
        "FILE" -> if (body.isBlank()) "Document" else body
        else -> body.replace('\n', ' ').trim().ifBlank { "No messages yet" }
    }.let { if (it.length > 48) it.take(45) + "…" else it }

    fun time(timestamp: Long): String = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(timestamp))

    fun dateLabel(timestamp: Long): String = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(Date(timestamp))

    fun dateTimeForEditor(timestamp: Long): String =
        SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).format(Date(timestamp))

    fun parseDateTime(value: String, fallback: Long = System.currentTimeMillis()): Long = try {
        SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).apply { isLenient = false }.parse(value.trim())?.time ?: fallback
    } catch (_: Exception) {
        fallback
    }

    fun reactions(raw: String): List<String> = raw.split('|').filter { it.isNotBlank() }.distinct()

    fun toggleReaction(raw: String, emoji: String): String {
        val current = reactions(raw).toMutableList()
        if (emoji in current) current.remove(emoji) else current.add(emoji)
        return current.joinToString("|")
    }
}
