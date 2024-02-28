package com.example.tnsapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class AuditActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audit)
        setupUI()
    }

    private fun setupUI() {
        val button = findViewById<Button>(R.id.button)

        button.setOnClickListener {
            openAddnewActivity()
        }
    }

    private fun openAddnewActivity() {
        startActivity(Intent(this@AuditActivity, AddnewActivity::class.java))
    }

}