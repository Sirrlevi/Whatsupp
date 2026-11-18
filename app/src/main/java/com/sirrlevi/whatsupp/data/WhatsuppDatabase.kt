package com.sirrlevi.whatsupp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [ContactEntity::class, MessageEntity::class],
    version = 1,
    exportSchema = false
)
abstract class WhatsuppDatabase : RoomDatabase() {
    abstract fun contactDao(): ContactDao
    abstract fun messageDao(): MessageDao

    companion object {
        @Volatile private var instance: WhatsuppDatabase? = null

        fun get(context: Context): WhatsuppDatabase = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext,
                WhatsuppDatabase::class.java,
                "whatsupp-local.db"
            ).build().also { instance = it }
        }
    }
}
