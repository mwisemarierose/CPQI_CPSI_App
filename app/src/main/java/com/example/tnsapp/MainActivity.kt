package com.example.tnsapp

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import java.util.Locale

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        val language = getSelectedLanguage()
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(language))
        onClickListener()
    }

    private fun onClickListener() {
        val getStarted = findViewById<Button>(R.id.get_started)
        getStarted.setOnClickListener {
            val language = getSelectedLanguage()
            openAuditActivity(language)
        }
    }
    private fun getSelectedLanguage(): String {
        val sharedPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return sharedPref.getString("language", "en") ?: "en"
    }

    private fun openAuditActivity(language: String) {
        val intent = Intent(this, AuditActivity::class.java)
        intent.putExtra("language", language)
        startActivity(intent)
    }

}