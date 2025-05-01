package com.example.budgettrackerpoe

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class ReportsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reports) // Set the layout for this activity

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
                    val intent = Intent(this, ExpenseHistoryActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_reports -> {
                    // Stay on the current Reports page
                    true
                }
                else -> false
            }
        }
    }
}
