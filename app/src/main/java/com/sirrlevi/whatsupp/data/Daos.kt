package com.sirrlevi.whatsupp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {
    @Query("SELECT * FROM contacts ORDER BY isPinned DESC, name COLLATE NOCASE ASC")
    fun observeAll(): Flow<List<ContactEntity>>

    @Query("SELECT * FROM contacts WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): ContactEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(contact: ContactEntity)

    @Query("DELETE FROM contacts WHERE id = :id")
    suspend fun delete(id: String)

    @Query("DELETE FROM contacts")
    suspend fun deleteAll()
}

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages WHERE contactId = :contactId ORDER BY timestamp ASC")
    fun observeForContact(contactId: String): Flow<List<MessageEntity>>

    @Query("SELECT * FROM messages WHERE contactId = :contactId ORDER BY timestamp DESC LIMIT 1")
    suspend fun latestForContact(contactId: String): MessageEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(message: MessageEntity)

    @Update
    suspend fun update(message: MessageEntity)

    @Query("DELETE FROM messages WHERE id = :id")
    suspend fun delete(id: String)

    @Query("DELETE FROM messages")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM messages WHERE contactId = :contactId")
    suspend fun countForContact(contactId: String): Int
}
