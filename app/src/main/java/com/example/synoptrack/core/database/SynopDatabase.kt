package com.example.synoptrack.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.synoptrack.core.database.dao.ChatMessageDao
import com.example.synoptrack.core.database.entity.ChatMessageEntity

@Database(entities = [ChatMessageEntity::class], version = 1, exportSchema = false)
abstract class SynopDatabase : RoomDatabase() {
    abstract fun chatMessageDao(): ChatMessageDao
}
