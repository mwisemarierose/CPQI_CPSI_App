package com.example.tnsapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        val categoriesLayout = layoutInflater.inflate(R.layout.activity_categories, null)
//        val todaysDateTextView: TextView = categoriesLayout.findViewById(R.id.todaysDateTextView)
//        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
//        todaysDateTextView.text = "Today's Date: $currentDate"

        onClickListener()
    }

    private fun onClickListener() {
        val getstarted = findViewById<Button>(R.id.getstarted)
        getstarted.setOnClickListener {
            openAuditActivity()
        }
    }

    private fun openAuditActivity() {
        startActivity(Intent(this@MainActivity, AuditActivity::class.java))
    }
}
