package com.sirrlevi.whatsupp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [ContactEntity::class, MessageEntity::class],
    version = 2,
    exportSchema = false
)
abstract class WhatsuppDatabase : RoomDatabase() {
    abstract fun contactDao(): ContactDao
    abstract fun messageDao(): MessageDao

    companion object {
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE contacts ADD COLUMN isArchived INTEGER NOT NULL DEFAULT 0")
            }
        }

        @Volatile private var instance: WhatsuppDatabase? = null

        fun get(context: Context): WhatsuppDatabase = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext,
                WhatsuppDatabase::class.java,
                "whatsupp-local.db"
            ).addMigrations(MIGRATION_1_2).build().also { instance = it }
        }
    }
}
