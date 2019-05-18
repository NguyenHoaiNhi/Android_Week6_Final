package com.example.week64.ROOM

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context

@Database(entities = arrayOf(Task::class, User::class), version = 3)
abstract class AppDatabase : RoomDatabase(){
    abstract fun taskDAO(): TaskDAO
    abstract fun userDAO(): UserDAO
    companion object {
        @Volatile
        private var instance: AppDatabase? = null
        private val LOCK = Any()

        // Single pattern
        operator fun invoke(context: Context) = instance ?: synchronized(
            LOCK
        ){
            instance
                ?: buildDatabase(context).also { instance = it }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context,
            AppDatabase::class.java, "DATABASE_NAME"
        ).allowMainThreadQueries().build()
    }
}