package com.example.budgettrackerpoe

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ExpenseDao {
    @Insert
    fun insert(expense: Expense)

    @Query("SELECT * FROM expense_table WHERE date >= :startDate AND date <= :endDate")
    fun getExpensesByDateRange(startDate: String, endDate: String): List<Expense>
}

