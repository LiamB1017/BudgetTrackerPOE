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

        // Initialize views
        val btnPickStartDate = findViewById<View>(R.id.btnPickStartDate)
        val btnPickEndDate = findViewById<View>(R.id.btnPickEndDate)
        val btnSearch = findViewById<View>(R.id.btnSearch)
        expenseListView = findViewById(R.id.listViewCategorySummary)

        // Set date picker for start date
        btnPickStartDate.setOnClickListener {
            showDatePickerDialog { selectedDate ->
                startDate = selectedDate
            }
        }

        // Set date picker for end date
        btnPickEndDate.setOnClickListener {
            showDatePickerDialog { selectedDate ->
                endDate = selectedDate
            }
        }

        // Set listener for search button
        btnSearch.setOnClickListener {
            fetchExpensesAndDisplay()
        }
    }

    // Function to show date picker dialog
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

    // Function to fetch expenses based on date range and display in ListView
    private fun fetchExpensesAndDisplay() {
        // Use a background thread to fetch expenses
        Thread {
            // Get expenses from RoomDB based on the selected date range
            val db = Room.databaseBuilder(applicationContext, CategoryDatabase::class.java, "category_db").build()
            val expenseDao = db.expenseDao()

            // Fetch expenses from the database using the date range
            val expenses = expenseDao.getExpensesByDateRange(startDate, endDate)

            // Run the UI update on the main thread after fetching the data
            runOnUiThread {
                // Set up the ListView to display expenses
                val expenseDescriptions = expenses.map { it.description }
                val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, expenseDescriptions)
                expenseListView.adapter = adapter

                // Handle item click in ListView
                expenseListView.setOnItemClickListener { _, _, position, _ ->
                    // Get the selected expense and display its receipt image
                    val selectedExpense = expenses[position]
                    displayReceiptImage(selectedExpense)
                }
            }
        }.start()  // Start the thread
    }


    // Function to display the receipt image when an expense is clicked
    private fun displayReceiptImage(expense: Expense) {
        val imageView = findViewById<ImageView>(R.id.imageViewChart)

        if (expense.receiptUri != null) {
            val uri = Uri.parse(expense.receiptUri)
            imageView.setImageURI(uri)  // Display the image in the ImageView
        } else {
            imageView.setImageDrawable(null)  // Clear the image view if no image is available
        }
    }
}
