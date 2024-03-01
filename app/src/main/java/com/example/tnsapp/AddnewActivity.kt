package com.example.tnsapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class AddNewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addnew)

        setupUI()
    }

    private fun setupUI() { val addNewBtn = findViewById<Button>(R.id.addNewBtn)

        addNewBtn.setOnClickListener {
            openCategoryActivity()
        }
    }

    private fun openCategoryActivity() {
        val auditListId = intent.getLongExtra("auditListId", 0)

        startActivity(Intent(this, CategoriesActivity::class.java).apply {
            putExtra("auditListId", auditListId)
        })
    }
}