//Group Members:
//Eliezer Zlotnick	        ST10312794
//Mmabalane Mothiba	        ST10393134
//Liam Max Brown	        ST10262451
//Kgomotso Mbulelo Nxumalo	ST10135860
//Muhammed Riyaad Kajee	    ST10395948

package com.example.budgettrackerpoe

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.text.SimpleDateFormat
import java.util.*
import android.content.Intent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ExpenseHistoryActivity : AppCompatActivity() {

    private lateinit var expenseDao: ExpenseDao
    private lateinit var categoryDao: CategoryDao
    private lateinit var buttonFetchData: Button
    private lateinit var buttonStartDate: Button
    private lateinit var buttonEndDate: Button
    private lateinit var textViewData: TextView

    private var startDate: String = ""
    private var endDate: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expense_history)

        val database = CategoryDatabase.getDatabase(this)
        expenseDao = database.expenseDao()
        categoryDao = database.categoryDao()

        buttonFetchData = findViewById(R.id.buttonFetchData)
        buttonStartDate = findViewById(R.id.buttonStartDate)
        buttonEndDate = findViewById(R.id.buttonEndDate)
        textViewData = findViewById(R.id.textViewData)

        buttonStartDate.setOnClickListener {
            showDatePicker { date ->
                startDate = date
                buttonStartDate.text = "Start Date: $date"
            }
        }

        buttonEndDate.setOnClickListener {
            showDatePicker { date ->
                endDate = date
                buttonEndDate.text = "End Date: $date"
            }
        }

        buttonFetchData.setOnClickListener {
            if (startDate.isNotEmpty() && endDate.isNotEmpty()) {
                fetchExpenseData(startDate, endDate)
            } else {
                textViewData.text = "Please select both start and end dates."
            }
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                R.id.nav_add_expense -> true
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
        bottomNavigationView.selectedItemId = R.id.nav_add_expense
    }

    private fun showDatePicker(onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
                onDateSelected(selectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun fetchExpenseData(startDate: String, endDate: String) {
        // Launch the background task
        GlobalScope.launch(Dispatchers.Main) {
            try {
                // Fetch expenses using DAO on background thread
                val expenses = withContext(Dispatchers.IO) {
                    expenseDao.getExpensesByDateRange(startDate, endDate)
                }
                val stringBuilder = StringBuilder()
                var totalAmount = 0.0

                if (expenses.isEmpty()) {
                    textViewData.text = "No expenses found for the given date range."
                    return@launch
                }

                // Loop through each expense and build output
                for (expense in expenses) {
                    stringBuilder.append("---------------\n\n")
                    stringBuilder.append("Category: ${expense.category}\n")
                    stringBuilder.append("Amount: ${expense.amount}\n")
                    stringBuilder.append("Description: ${expense.description}\n\n")
                    totalAmount += expense.amount
                }

                stringBuilder.append("\nTotal Amount Spent: $totalAmount")
                textViewData.text = stringBuilder.toString()

            } catch (e: Exception) {
                textViewData.text = "Error fetching data: ${e.message}"
            }
        }
    }
}
