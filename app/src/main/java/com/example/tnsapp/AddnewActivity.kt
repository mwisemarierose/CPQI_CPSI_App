package com.example.tnsapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class AddnewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addnew)
        setupUI()
    }

    private fun setupUI() {
        val bodyButton = findViewById<Button>(R.id.bodyButton)

        bodyButton.setOnClickListener {
            openCategoryActivity()
        }
    }

    private fun openCategoryActivity() {
        startActivity(Intent(this, CategoriesActivity::class.java))
    }
}