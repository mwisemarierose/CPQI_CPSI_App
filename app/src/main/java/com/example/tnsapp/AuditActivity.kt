package com.example.tnsapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast

class AuditActivity : AppCompatActivity() {
    private var lastClickedButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audit)

        setupUI()
    }

    private fun setupUI() {
        val cpqiBtn: Button = findViewById(R.id.cpqiBtn)
        val cpsiBtn: Button = findViewById(R.id.cpsiBtn)
        val continueBtn = findViewById<Button>(R.id.continueBtn)
        continueBtn.isEnabled = false

        cpqiBtn.setOnClickListener {
            cpqiBtn.setBackgroundColor(resources.getColor(R.color.pressed_button_color))
            cpsiBtn.setBackgroundColor(resources.getColor(R.color.default_button_color))
            lastClickedButton = cpqiBtn
            continueBtn.isEnabled = true
        }

        cpsiBtn.setOnClickListener {
            cpsiBtn.setBackgroundColor(resources.getColor(R.color.pressed_button_color))
            cpqiBtn.setBackgroundColor(resources.getColor(R.color.default_button_color))
            lastClickedButton = cpsiBtn
            continueBtn.isEnabled = true
        }

        continueBtn.setOnClickListener {
            if (lastClickedButton != null) {
                when (lastClickedButton?.id) {
                    R.id.cpqiBtn -> openAddNewCPQIActivity()
                    R.id.cpsiBtn -> openAddNewCPSIActivity()
                }
            } else {
                Toast.makeText(this, "Please select an audit option first", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openAddNewCPQIActivity() {
        startActivity(Intent(this, AddNewActivity::class.java))
    }

    private fun openAddNewCPSIActivity() {
        startActivity(Intent(this, AddnewCpsiActivity::class.java))
    }
}
