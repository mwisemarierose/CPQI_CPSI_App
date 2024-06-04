package com.technoserve.cpqi


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
import com.technoserve.cpqi.data.AppDatabase
import com.technoserve.cpqi.parsers.readJsonFromAssets

class MainActivity : AppCompatActivity() {

    private lateinit var jsonData: String

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        val db = AppDatabase.getDatabase(this)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        val languageSpinner: Spinner = findViewById(R.id.languageSpinner)
        setupLanguageSpinner(languageSpinner)
        val initialLanguage = getInitialLanguage()
        setupUI(initialLanguage)
        onClickListener()
    }
    private fun getInitialLanguage(): String {
        val sharedPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val savedLanguage = sharedPref.getString("language", null)

        return savedLanguage ?: getDefaultLanguage()
    }

    private fun getDefaultLanguage(): String {
        // Determine the default language based on your requirements
        // For example, you can use the device's default language
        return "en" // Change this to the desired default language
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
                val selectedLanguage = when (position) {
                    0 -> "en"
                    1 -> "rw"
                    else -> getDefaultLanguage()
                }
                changeLanguage(selectedLanguage)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing if nothing is selected
            }
        }
    }

    private fun getDefaultJsonFileName(language: String): String {
        return when (language) {
            "en" -> "data_en.json"
            "rw" -> "data_rw.json"
            else -> "data_en.json" // Use English as the default
        }
    }

    private fun changeLanguage(languageCode: String) {
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(languageCode))
        saveLanguagePreference(languageCode)
        setupUI(languageCode)
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
        val selectedLang = when (selectedLanguage) {
            "en" -> 0
            "rw" -> 1
            else -> 0 // Set English as the default
        }
        languageSpinner.setSelection(selectedLang)

        jsonData = readJsonFromAssets(this, getDefaultJsonFileName(selectedLanguage))
    }

    private fun onClickListener() {
        val getStarted = findViewById<Button>(R.id.get_started)
        getStarted.setOnClickListener {
            val language = getInitialLanguage()
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