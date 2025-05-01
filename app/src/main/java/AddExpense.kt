package com.example.budgettrackerpoe


import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
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

        // Spinner setup
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.expense_categories,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = adapter

        etDate.setOnClickListener { pickDate() }
        etStartTime.setOnClickListener { pickTime(etStartTime) }
        etEndTime.setOnClickListener { pickTime(etEndTime) }

        btnAttach.setOnClickListener { pickImage() }
        btnAddExpense.setOnClickListener { saveExpense() }

        // Bottom Navigation Setup
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

    private fun pickDate() {
        val c = Calendar.getInstance()
        val dpd = DatePickerDialog(
            this,
            { _, year, month, day ->
                etDate.setText("$day/${month + 1}/$year")
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
        val amount = etAmount.text.toString()
        val date = etDate.text.toString()
        val startTime = etStartTime.text.toString()
        val endTime = etEndTime.text.toString()
        val desc = etDescription.text.toString()
        val category = spinnerCategory.selectedItem.toString()

        if (amount.isEmpty() || date.isEmpty() || startTime.isEmpty() || endTime.isEmpty()) {
            Toast.makeText(this, "Please complete all fields", Toast.LENGTH_SHORT).show()
            return
        }

        Toast.makeText(this, "Expense Added!", Toast.LENGTH_SHORT).show()
    }
}
