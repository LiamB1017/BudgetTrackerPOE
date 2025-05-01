package com.example.budgettrackerpoe

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class ExpenseHistoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expense_history) // Set the layout for this activity

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        // Set listener for BottomNavigationView
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this, DashboardActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_add_expense -> {
                    val intent = Intent(this, AddExpense::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_category -> {
                    val intent = Intent(this, CategoryActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_expense_history -> {
                    // Stay on the current Expense History page
                    true
                }
                R.id.nav_reports -> {
                    val intent = Intent(this, ReportsActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }
}
