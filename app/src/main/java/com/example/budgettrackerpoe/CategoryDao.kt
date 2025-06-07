package com.example.budgettrackerpoe

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CategoryDao {
    @Insert
    fun insert(category: Category)

    @Query("SELECT * FROM category_table")
    fun getAll(): List<Category>
}
