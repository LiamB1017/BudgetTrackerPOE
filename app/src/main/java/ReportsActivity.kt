//Group Members:
//Eliezer Zlotnick	        ST10312794
//Mmabalane Mothiba	        ST10393134
//Liam Max Brown	        ST10262451
//Kgomotso Mbulelo Nxumalo	ST10135860
//Muhammed Riyaad Kajee	    ST10395948

package com.example.budgettrackerpoe

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import android.net.Uri
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.text.SimpleDateFormat
import java.util.*

class ReportsActivity : AppCompatActivity() {

    private lateinit var startDate: String
    private lateinit var endDate: String
    private lateinit var expenseListView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reports)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        // Nav Bar
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

        // Views
        val btnPickStartDate = findViewById<View>(R.id.btnPickStartDate)
        val btnPickEndDate = findViewById<View>(R.id.btnPickEndDate)
        val btnSearch = findViewById<View>(R.id.btnSearch)
        expenseListView = findViewById(R.id.listViewCategorySummary)

        // Set start date
        btnPickStartDate.setOnClickListener {
            showDatePickerDialog { selectedDate ->
                startDate = selectedDate
            }
        }

        // Set end date
        btnPickEndDate.setOnClickListener {
            showDatePickerDialog { selectedDate ->
                endDate = selectedDate
            }
        }

        btnSearch.setOnClickListener {
            fetchExpensesAndDisplay()
        }
    }

    private fun showDatePickerDialog(onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val formattedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
                onDateSelected(formattedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    // Fetch expenses based on date range
    private fun fetchExpensesAndDisplay() {
        Thread {
            // Get expenses from RoomDB
            val db = Room.databaseBuilder(applicationContext, CategoryDatabase::class.java, "category_db").build()
            val expenseDao = db.expenseDao()
            val expenses = expenseDao.getExpensesByDateRange(startDate, endDate)

            // Run UI update on the main thread
            runOnUiThread {
                val expenseDescriptions = expenses.map { it.description }
                val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, expenseDescriptions)
                expenseListView.adapter = adapter
                expenseListView.setOnItemClickListener { _, _, position, _ ->
                    // Get selected expense and display its receipt
                    val selectedExpense = expenses[position]
                    displayReceiptImage(selectedExpense)
                }
            }
        }.start()
    }


    // Function to display the receipt image when expense is clicked
    private fun displayReceiptImage(expense: Expense) {
        val imageView = findViewById<ImageView>(R.id.imageViewChart)

        if (expense.receiptUri != null) {
            val uri = Uri.parse(expense.receiptUri)
            // Display the image in the ImageView
            imageView.setImageURI(uri)
        } else {
            imageView.setImageDrawable(null)
        }
    }
}
