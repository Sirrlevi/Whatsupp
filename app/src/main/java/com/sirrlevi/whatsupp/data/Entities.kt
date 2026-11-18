package com.sirrlevi.whatsupp.data

import androidx.room.Entity
import androidx.room.ColumnInfo
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "contacts")
data class ContactEntity(
    @PrimaryKey val id: String,
    val name: String,
    val phone: String = "",
    val avatarUri: String? = null,
    val about: String = "Available",
    val createdAt: Long = System.currentTimeMillis(),
    val isPinned: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val mediaVisibility: Boolean = true,
    val disappearingMessages: Boolean = false,
    val chatLocked: Boolean = false,
    @ColumnInfo(defaultValue = "0") val isArchived: Boolean = false
)

@Entity(
    tableName = "messages",
    foreignKeys = [
        ForeignKey(
            entity = ContactEntity::class,
            parentColumns = ["id"],
            childColumns = ["contactId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("contactId"), Index("timestamp")]
)
data class MessageEntity(
    @PrimaryKey val id: String,
    val contactId: String,
    val body: String = "",
    val kind: String = "TEXT",
    val attachmentUri: String? = null,
    val outgoing: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "READ",
    val replyToId: String? = null,
    val edited: Boolean = false,
    val reactions: String = "",
    val isDeleted: Boolean = false
)
