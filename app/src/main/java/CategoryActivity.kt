package com.example.budgettrackerpoe

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class CategoryActivity : AppCompatActivity() {

    private lateinit var ctgList: ListView
    private lateinit var ctgName: EditText
    private lateinit var btnAddCat: Button
    private lateinit var categoryAdapter: ArrayAdapter<String>
    private val categoryList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        ctgList = findViewById(R.id.ctgList)
        ctgName = findViewById(R.id.ctgName)
        btnAddCat = findViewById(R.id.btnAddCat)

        categoryAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, categoryList)
        ctgList.adapter = categoryAdapter

        btnAddCat.setOnClickListener {
            val newCategory = ctgName.text.toString()
            if (newCategory.isNotBlank()) {
                categoryList.add(newCategory)
                categoryAdapter.notifyDataSetChanged()
                ctgName.text.clear()
            }
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                R.id.nav_add_expense -> {
                    startActivity(Intent(this, AddExpenseActivity::class.java))
                    true
                }
                R.id.nav_category -> true
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


        bottomNavigationView.selectedItemId = R.id.nav_category
    }
}
