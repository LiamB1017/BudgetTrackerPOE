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
import android.content.pm.PackageManager


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
    private val REQUEST_CAMERA = 1001 // Request code for the camera

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

        // Request camera permission at runtime
        if (checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(android.Manifest.permission.CAMERA), REQUEST_CAMERA)
        } else {
            btnAttach.setOnClickListener { captureImage() }
        }

        // Date and Time pickers
        etDate.setOnClickListener { pickDate() }
        etStartTime.setOnClickListener { pickTime(etStartTime) }
        etEndTime.setOnClickListener { pickTime(etEndTime) }

        // Add expense button
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

    private fun captureImage() {
        try {
            val photoFile = createImageFile()  // Create the file for the image
            receiptUri = FileProvider.getUriForFile(
                this,
                "${applicationContext.packageName}.provider",
                photoFile
            )
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, receiptUri)
            startActivityForResult(cameraIntent, REQUEST_CAMERA)
        } catch (ex: IOException) {
            Toast.makeText(this, "Error creating image file.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir: File = getExternalFilesDir(null)!!
        return File.createTempFile(
            "IMG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {
            ivReceipt.setImageURI(receiptUri) // Set the captured image to ImageView
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

    // Handle permissions request result
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CAMERA) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                btnAttach.setOnClickListener { captureImage() }
            } else {
                Toast.makeText(this, "Camera permission is required to take photos.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
