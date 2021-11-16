/*
package com.tc.hackernews.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [StoryEntity::class], version = 1)
abstract class StoryDatabase : RoomDatabase() {

    abstract fun storyDAO(): StoryDAO

    companion object {
        private var instance: StoryDatabase? = null

        @Synchronized
        fun getInstance(ctx: Context): StoryDatabase {
            if (instance == null)
                instance = Room.databaseBuilder(
                    ctx.applicationContext, StoryDatabase::class.java,
                    "story_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()

            return instance!!
        }

    }
}*/
