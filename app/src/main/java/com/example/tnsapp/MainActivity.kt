package com.example.tnsapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        onClickListener()
    }
    private fun onClickListener(){
       val getstarted = findViewById<Button>(R.id.getstarted)

        getstarted.setOnClickListener {
            openAuditActivity()
        }

    }

    private fun openAuditActivity(){
        startActivity(Intent(this@MainActivity,AuditActivity::class.java))
    }


}