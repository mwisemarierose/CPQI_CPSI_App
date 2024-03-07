package com.example.tnsapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class AddnewCpsiActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.addnew_cpsi)
        setupUI()

    }
    private fun setupUI() {
        val addNewBtn = findViewById<Button>(R.id.addNewBtn)

        addNewBtn.setOnClickListener {
            openCategoryActivity()
        }
    }

    private fun openCategoryActivity() {

        startActivity(Intent(this, CategoriesCpqiActivity::class.java))
    }
}