package com.example.tnsapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        onClickListener()
    }

    private fun onClickListener() {
        val getStarted = findViewById<Button>(R.id.getstarted)
        getStarted.setOnClickListener {
            openAuditActivity()
        }
    }

    private fun openAuditActivity() {
        startActivity(Intent(this@MainActivity, AuditActivity::class.java))
    }
}