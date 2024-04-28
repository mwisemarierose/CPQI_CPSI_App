package com.example.tnsapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tnsapp.adapters.AuditAdapter
import com.example.tnsapp.data.AuditCategories
import com.example.tnsapp.parsers.auditParser
import com.example.tnsapp.parsers.readJsonFromAssets

class MainActivity : AppCompatActivity() {

    private lateinit var jsonData: String
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        val languageSpinner: Spinner = findViewById(R.id.languageSpinner)
        setupLanguageSpinner(languageSpinner)
        val language = getSelectedLanguage()
        intent.getStringExtra("language")?.let { setupUI(it) }
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(language))
        onClickListener()
    }

    private fun getSelectedLanguage(): String {
        val sharedPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return sharedPref.getString("language", "en") ?: "en"
    }
    private fun setupLanguageSpinner(languageSpinner: Spinner) {
        ArrayAdapter.createFromResource(
            this,
            R.array.languages,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            languageSpinner.adapter = adapter
        }

        languageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: android.view.View?,
                position: Int,
                id: Long
            ) {
                val selectedLanguage = parent?.getItemAtPosition(position).toString()
                val intentLang = intent.getStringExtra("language")

                if (intentLang != selectedLanguage && selectedLanguage == "English") {
                    if (intentLang != null) {
                        changeLanguage(selectedLanguage)
                    }
                } else {
                    changeLanguage(if (selectedLanguage == "English" || selectedLanguage == "en") "en" else "rw")
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing if nothing is selected
            }
        }
    }
    private fun changeLanguage(languageCode: String) {
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(languageCode))
        // Save the selected language in SharedPreferences or any other way you prefer
        saveLanguagePreference(languageCode)
        setupUI(languageCode)
        println(languageCode)
    }
    private fun saveLanguagePreference(language: String) {
        val sharedPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("language", language)
            apply()
        }
    }
    private fun setupUI(selectedLanguage: String) {
        val languageSpinner: Spinner = findViewById(R.id.languageSpinner)
        // Use "en" as default language if language extra is missing
        val result = if (selectedLanguage == "English" || selectedLanguage == "en") {
            "data_en.json"
        } else {
            "data_rw.json"
        }
        println(result)

        val selectedLang = if (selectedLanguage == "English" || selectedLanguage == "en") 0 else 1
        languageSpinner.setSelection(selectedLang)

        jsonData = readJsonFromAssets(this, result)

    }
    private fun onClickListener() {
        val getStarted = findViewById<Button>(R.id.get_started)
        getStarted.setOnClickListener {
            val language = getSelectedLanguage()
            openAuditActivity(language)
        }
    }
    private fun openAuditActivity(language: String) {
        val intent = Intent(this, AddNewActivity::class.java)
        intent.putExtra("language", language)
        intent.putExtra("auditId", 1)
        intent.putExtra("audit", jsonData)
        startActivity(intent)
    }
}