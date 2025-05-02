package com.example.budgettrackerpoe

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.content.Intent
import java.util.concurrent.Executors

class DashboardActivity : AppCompatActivity() {

    private lateinit var minGoalInput: EditText
    private lateinit var maxGoalInput: EditText
    private lateinit var saveGoalsButton: Button
    private lateinit var goalsInfoTextView: TextView
    private lateinit var monthSpinner: Spinner
    private lateinit var appDatabase: CategoryDatabase
    private lateinit var monthlyGoalDao: MonthlyGoalDao

    private val executor = Executors.newSingleThreadExecutor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        minGoalInput = findViewById(R.id.minGoalInput)
        maxGoalInput = findViewById(R.id.maxGoalInput)
        saveGoalsButton = findViewById(R.id.saveGoalsButton)
        goalsInfoTextView = findViewById(R.id.goalsInfoTextView)
        monthSpinner = findViewById(R.id.monthSpinner)

        appDatabase = CategoryDatabase.getDatabase(applicationContext)
        monthlyGoalDao = appDatabase.monthlyGoalDao()

        val months = listOf("January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, months)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        monthSpinner.adapter = adapter

        saveGoalsButton.setOnClickListener {
            val minGoal = minGoalInput.text.toString().toIntOrNull()
            val maxGoal = maxGoalInput.text.toString().toIntOrNull()

            if (minGoal == null || maxGoal == null || minGoal > maxGoal) {
                Toast.makeText(this, "Please enter valid integer values", Toast.LENGTH_SHORT).show()
            } else {
                val selectedMonth = monthSpinner.selectedItem.toString()
                val goal = MonthlyGoal(month = selectedMonth, minGoal = minGoal, maxGoal = maxGoal)

                executor.execute {
                    monthlyGoalDao.insert(goal)
                    runOnUiThread {
                        goalsInfoTextView.text = "Saved for $selectedMonth:\nMin: $minGoal\nMax: $maxGoal"
                    }
                }
            }
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_add_expense -> {
                    startActivity(Intent(this, AddExpense::class.java))
                    true
                }
                R.id.nav_category -> {
                    startActivity(Intent(this, CategoryActivity::class.java))
                    true
                }
                R.id.nav_expense_history -> {
                    startActivity(Intent(this, ExpenseHistoryActivity::class.java))
                    true
                }
                R.id.nav_reports -> {
                    startActivity(Intent(this, ReportsActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        executor.shutdown()
    }
}
