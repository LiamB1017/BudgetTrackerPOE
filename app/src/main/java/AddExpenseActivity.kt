package com.example.budgettrackerpoe

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class AddExpenseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_expense)

        // Get reference to BottomNavigationView
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        // Set listener for BottomNavigationView
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    // Navigate to DashboardActivity
                    navigateTo(DashboardActivity::class.java)
                    true
                }
                R.id.nav_add_expense -> {
                    // Stay on the current AddExpenseActivity (no need to restart)
                    true
                }
                R.id.nav_category -> {
                    // Navigate to CategoryActivity
                    navigateTo(CategoryActivity::class.java)
                    true
                }
                R.id.nav_expense_history -> {
                    // Stay on the current Expense History page (no need to restart)
                    true
                }
                R.id.nav_reports -> {
                    // Navigate to ReportsActivity
                    navigateTo(ReportsActivity::class.java)
                    true
                }
                else -> false
            }
        }
    }

    // Helper function to start a new activity
    private fun navigateTo(activityClass: Class<*>) {
        val intent = Intent(this, activityClass)
        startActivity(intent)
    }
}
