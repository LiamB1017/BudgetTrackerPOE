package com.example.budgettrackerpoe

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class CategoryActivity : AppCompatActivity() {

    private lateinit var db: CategoryDatabase
    private lateinit var categoryDao: CategoryDao
    private lateinit var categoryListView: ListView
    private lateinit var categoriesList: MutableList<Category>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        // Initialize Room Database
        db = CategoryDatabase.getDatabase(this)
        categoryDao = db.categoryDao()

        // Initialize ListView and list
        categoryListView = findViewById(R.id.ctgList)
        categoriesList = mutableListOf()

        // Bottom Navigation View Setup
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java)) // Navigate to Home
                    true
                }
                R.id.nav_add_expense -> {
                    startActivity(Intent(this, AddExpenseActivity::class.java)) // Navigate to Add Expense
                    true
                }
                R.id.nav_category -> {
                    // This is the current activity, no need to navigate
                    true
                }
                R.id.nav_expense_history -> {
                    startActivity(Intent(this, ExpenseHistoryActivity::class.java)) // Navigate to Expense History
                    true
                }
                R.id.nav_reports -> {
                    startActivity(Intent(this, ReportsActivity::class.java)) // Navigate to Reports
                    true
                }
                else -> false
            }
        }
        bottomNavigationView.selectedItemId = R.id.nav_category // Set default selected item

        // Fetch categories from database
        loadCategories()

        // Setup Button to add a new category
        val btnAddCat = findViewById<Button>(R.id.btnAddCat)
        btnAddCat.setOnClickListener {
            val categoryName = findViewById<EditText>(R.id.ctgName).text.toString()
            if (categoryName.isNotEmpty()) {
                addCategory(Category(name = categoryName))
            }
        }
    }

    // Function to load categories from the database
    private fun loadCategories() {
        val task = object : AsyncTask<Void, Void, List<Category>>() {
            override fun doInBackground(vararg params: Void?): List<Category> {
                return categoryDao.getAll() // Perform database operation on background thread
            }

            override fun onPostExecute(result: List<Category>?) {
                super.onPostExecute(result)
                result?.let {
                    categoriesList.clear()
                    categoriesList.addAll(it)
                    // Update ListView with fetched categories
                    val adapter = ArrayAdapter(this@CategoryActivity, android.R.layout.simple_list_item_1, categoriesList.map { it.name })
                    categoryListView.adapter = adapter
                }
            }
        }
        task.execute() // Execute AsyncTask
    }

    // Function to add a new category to the database
    private fun addCategory(category: Category) {
        val task = object : AsyncTask<Category, Void, Void>() {
            override fun doInBackground(vararg params: Category?): Void? {
                categoryDao.insert(params[0]!!) // Insert category into the database in the background
                return null
            }

            override fun onPostExecute(result: Void?) {
                super.onPostExecute(result)
                loadCategories() // Reload categories after adding a new one
            }
        }
        task.execute(category) // Execute AsyncTask
    }
}
