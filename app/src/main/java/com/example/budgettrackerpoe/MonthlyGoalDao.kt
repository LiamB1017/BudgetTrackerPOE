package com.example.budgettrackerpoe

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MonthlyGoalDao {

    @Insert
    fun insert(monthlyGoal: MonthlyGoal)

    @Query("SELECT * FROM monthly_goals")
    fun getAllGoals(): List<MonthlyGoal>

    @Query("SELECT * FROM monthly_goals WHERE month = :month LIMIT 1")
    fun getGoalByMonth(month: String): MonthlyGoal?
}

