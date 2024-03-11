package com.example.tnsapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import java.util.Date;
import java.util.Locale;
import java.text.SimpleDateFormat
import android.view.View
import android.view.ViewGroup
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.PopupWindow
import android.widget.Button

class CategoriesCpqiActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.categories_cpqi)

        val dateTextView = findViewById<TextView>(R.id.todaysDateTextView)

        val currentDate = Date()

        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(currentDate)

        dateTextView.text = "Date: $formattedDate"

        val box1Layout = findViewById<LinearLayout>(R.id.box1Layout)
        val box2Layout = findViewById<LinearLayout>(R.id.box2Layout)

        box1Layout.setOnClickListener {
            showCustomPopupMenu(it)
        }

        box2Layout.setOnClickListener {
            showCustomPopupMenu(it)
        }

    }

    fun showCustomPopupMenu(view: View) {
        val popupView = LayoutInflater.from(this).inflate(R.layout.popup_layout, null)

        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )

        val saveButton = popupView.findViewById<Button>(R.id.saveButton)
        saveButton.setOnClickListener {
            // Handle save button click
            // Dismiss the popup window
            popupWindow.dismiss()
        }

        // Show the popup window at a specific location
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)
    }
}