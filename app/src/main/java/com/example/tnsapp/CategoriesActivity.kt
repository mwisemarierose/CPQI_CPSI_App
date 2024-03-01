package com.example.tnsapp


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class CategoriesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories)

        val auditListId = intent.getLongExtra("auditListId", 0)
        println(auditListId)
    }
}