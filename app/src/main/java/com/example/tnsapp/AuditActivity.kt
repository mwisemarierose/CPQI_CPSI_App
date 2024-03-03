package com.example.tnsapp

import AppDatabase
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AuditActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audit)

        db = AppDatabase.getDatabase(this)

        GlobalScope.launch(Dispatchers.Main) {
            val auditCategories = withContext(Dispatchers.IO) {
                db.auditCategoryDao().getAll()
            }

            auditCategories.forEach { audit ->
                Log.d("AuditCategory", "Name: ${audit.name}, Icon Path: ${audit.iconPath}")
            }
        }

        setupUI()
    }

    private fun setupUI() {
        val cpqiBtn: Button = findViewById(R.id.cpqiBtn)
        val cpsiBtn: Button = findViewById(R.id.cpsiBtn)
        val continueBtn = findViewById<Button>(R.id.continueBtn)

        var clickedBtn = 0

        cpqiBtn.setOnClickListener {
            clickedBtn = 1
        }

        cpsiBtn.setOnClickListener {
            clickedBtn = 2
        }

        continueBtn.setOnClickListener {
            openAddNewActivity(clickedBtn.toLong())
        }
    }

    private fun openAddNewActivity(
        auditListId: Long
    ) {
        startActivity(Intent(this@AuditActivity, AddNewActivity::class.java).apply {
            putExtra("auditListId", auditListId)
        })
    }

}