//Group Members:
//Eliezer Zlotnick	        ST10312794
//Mmabalane Mothiba	        ST10393134
//Liam Max Brown	        ST10262451
//Kgomotso Mbulelo Nxumalo	ST10135860
//Muhammed Riyaad Kajee	    ST10395948

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

        // Initialize Room
        db = CategoryDatabase.getDatabase(this)
        categoryDao = db.categoryDao()
        categoryListView = findViewById(R.id.ctgList)
        categoriesList = mutableListOf()

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java)) // Navigate to Home
                    true
                }
                R.id.nav_add_expense -> {
                    startActivity(Intent(this, AddExpense::class.java)) // Navigate to Add Expense
                    true
                }
                R.id.nav_category -> {
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

        // Button to add a new category
        val btnAddCat = findViewById<Button>(R.id.btnAddCat)
        btnAddCat.setOnClickListener {
            val categoryName = findViewById<EditText>(R.id.ctgName).text.toString()
            if (categoryName.isNotEmpty()) {
                addCategory(Category(name = categoryName))
            }
        }
    }

    // Load categories from database
    private fun loadCategories() {
        val task = object : AsyncTask<Void, Void, List<Category>>() {
            override fun doInBackground(vararg params: Void?): List<Category> {
                return categoryDao.getAll()
            }

            override fun onPostExecute(result: List<Category>?) {
                super.onPostExecute(result)
                result?.let {
                    categoriesList.clear()
                    categoriesList.addAll(it)
                    val adapter = ArrayAdapter(this@CategoryActivity, android.R.layout.simple_list_item_1, categoriesList.map { it.name })
                    categoryListView.adapter = adapter
                }
            }
        }
        task.execute() // Execute AsyncTask
    }

    // Add a new category to the database
    private fun addCategory(category: Category) {
        val task = object : AsyncTask<Category, Void, Void>() {
            override fun doInBackground(vararg params: Category?): Void? {
                categoryDao.insert(params[0]!!)
                return null
            }

            override fun onPostExecute(result: Void?) {
                super.onPostExecute(result)
                loadCategories()
            }
        }
        task.execute(category) // Execute AsyncTask
    }
}
