package com.example.budgettrackerpoe

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class AddExpense : AppCompatActivity() {

    private lateinit var etAmount: EditText
    private lateinit var etDate: EditText
    private lateinit var etStartTime: EditText
    private lateinit var etEndTime: EditText
    private lateinit var etDescription: EditText
    private lateinit var spinnerCategory: Spinner
    private lateinit var ivReceipt: ImageView
    private lateinit var btnAttach: Button
    private lateinit var btnAddExpense: Button

    private var receiptUri: Uri? = null
    private val PICK_IMAGE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_expense)

        etAmount = findViewById(R.id.etAmount)
        etDate = findViewById(R.id.etDate)
        etStartTime = findViewById(R.id.etStartTime)
        etEndTime = findViewById(R.id.etEndTime)
        etDescription = findViewById(R.id.etDescription)
        spinnerCategory = findViewById(R.id.spinnerCategory)
        ivReceipt = findViewById(R.id.ivReceipt)
        btnAttach = findViewById(R.id.btnAttach)
        btnAddExpense = findViewById(R.id.btnAddExpense)

        btnAttach.setOnClickListener { pickImage() }
        etDate.setOnClickListener { pickDate() }
        etStartTime.setOnClickListener { pickTime(etStartTime) }
        etEndTime.setOnClickListener { pickTime(etEndTime) }
        btnAddExpense.setOnClickListener { saveExpense() }

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

        loadCategories()
    }

    private fun loadCategories() {
        // Run database query on a background thread
        Thread {
            val categoryDao = CategoryDatabase.getDatabase(applicationContext).categoryDao()
            val categories = categoryDao.getAll() // Fetch categories from RoomDB
            val categoryNames = categories.map { it.name }

            // Run on the main thread to update UI
            runOnUiThread {
                if (categoryNames.isNotEmpty()) {
                    // Set the spinner adapter with the category names
                    val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categoryNames)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerCategory.adapter = adapter
                } else {
                    // In case no categories are found, display a default message
                    val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listOf("No categories available"))
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerCategory.adapter = adapter
                }
            }
        }.start()
    }

    private fun pickDate() {
        val c = Calendar.getInstance()
        val dpd = DatePickerDialog(
            this,
            { _, year, month, day ->
                // Format the date as yyyy-MM-dd before setting it in the EditText
                val formattedDate = String.format("%04d-%02d-%02d", year, month + 1, day)
                etDate.setText(formattedDate)
            },
            c.get(Calendar.YEAR),
            c.get(Calendar.MONTH),
            c.get(Calendar.DAY_OF_MONTH)
        )
        dpd.show()
    }


    private fun pickTime(target: EditText) {
        val c = Calendar.getInstance()
        val tpd = TimePickerDialog(
            this,
            { _, hour, minute ->
                target.setText(String.format("%02d:%02d", hour, minute))
            },
            c.get(Calendar.HOUR_OF_DAY),
            c.get(Calendar.MINUTE),
            true
        )
        tpd.show()
    }

    private fun pickImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            receiptUri = data.data
            ivReceipt.setImageURI(receiptUri)
        }
    }

    private fun saveExpense() {
        val amountText = etAmount.text.toString()
        val date = etDate.text.toString()
        val startTime = etStartTime.text.toString()
        val endTime = etEndTime.text.toString()
        val description = etDescription.text.toString()
        val category = spinnerCategory.selectedItem.toString()

        if (amountText.isEmpty() || date.isEmpty() || startTime.isEmpty() || endTime.isEmpty()) {
            Toast.makeText(this, "Please complete all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Convert amount to a double
        val amount = amountText.toDoubleOrNull() ?: 0.0

        // Get URI of receipt
        val uriString = receiptUri?.toString()

        // Create an Expense object and save it to the database (RoomDB)
        val expense = Expense(
            amount = amount,
            date = date,
            startTime = startTime,
            endTime = endTime,
            description = description,
            category = category,
            receiptUri = uriString
        )

        val db = CategoryDatabase.getDatabase(this)
        val expenseDao = db.expenseDao()

        // Insert the expense in a background thread
        Thread {
            expenseDao.insert(expense)
            runOnUiThread {
                Toast.makeText(this, "Expense Added!", Toast.LENGTH_SHORT).show()
                finish()  // Close the activity
            }
        }.start()
    }
}
