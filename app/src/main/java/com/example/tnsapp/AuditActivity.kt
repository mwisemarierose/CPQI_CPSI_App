package com.example.tnsapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.tnsapp.database.AuditCategories
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AuditActivity : AppCompatActivity() {

    private lateinit var cpqiBtn: Button
    private lateinit var cpsiBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audit)
        setupUI()
        fetchAuditCategories()
    }

    private fun setupUI() {
        cpqiBtn = findViewById(R.id.cpqiBtn)
        cpsiBtn = findViewById(R.id.cpsiBtn)

        cpqiBtn.setOnClickListener {
            // Handle CPQI button click
        }

        cpsiBtn.setOnClickListener {
            // Handle CPSI button click
        }
    }

    private fun fetchAuditCategories() {
        GlobalScope.launch(Dispatchers.Main) {
            val auditCategories = AppDatabase.getDatabase(applicationContext).auditCategoryDao().getAll()
            updateButtonLabels(auditCategories)
        }
    }

    private fun updateButtonLabels(auditCategories: List<AuditCategories>) {
        if (auditCategories.size >= 2) {
            cpqiBtn.text = auditCategories[0].name
            cpsiBtn.text = auditCategories[1].name
        }
    }

    private fun openAddNewActivity() {
        startActivity(Intent(this@AuditActivity, AddNewActivity::class.java))
    }
}
