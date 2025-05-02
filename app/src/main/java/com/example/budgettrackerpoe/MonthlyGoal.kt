package com.example.budgettrackerpoe

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "monthly_goals")
data class MonthlyGoal(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val month: String,
    val minGoal: Int,
    val maxGoal: Int
)

