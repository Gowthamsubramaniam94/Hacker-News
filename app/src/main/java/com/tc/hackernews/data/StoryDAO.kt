/*
package com.tc.hackernews.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import java.util.*

@Dao
interface StoryDAO {

    @Query("SELECT * FROM story_table")
    fun getAllStories(): LinkedList<StoryEntity>

    @Insert
    fun insertAllStories(story: LinkedList<StoryEntity>)

    @Delete
    fun delete(story: StoryEntity)

}*/
