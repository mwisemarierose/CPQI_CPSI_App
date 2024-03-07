package com.example.tnsapp


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CategoriesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories)
        val dateTextView = findViewById<TextView>(R.id.todaysDateTextView)

        val currentDate = Date()

        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(currentDate)

        dateTextView.text = "Date: $formattedDate"
    }
}