package com.example.tnsapp

import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tnsapp.adapters.AuditAdapter
import com.example.tnsapp.data.AuditCategories
import com.example.tnsapp.parsers.auditParser
import com.example.tnsapp.parsers.readJsonFromAssets
import android.widget.ArrayAdapter
import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import java.util.Locale

class AuditActivity : AppCompatActivity(), AuditAdapter.OnItemClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AuditAdapter
    private lateinit var jsonData: String
    private var isFirstSelection = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audit)
        supportActionBar?.hide()

        setupUI("")

        val languageSpinner: Spinner = findViewById(R.id.languageSpinner)
        setupLanguageSpinner(languageSpinner)
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
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                val selectedLanguage = parent?.getItemAtPosition(position).toString()

                if (!isFirstSelection) {
                    if (selectedLanguage == "English") {
                        changeLanguage("en")
                    } else {
                        changeLanguage("rw")
                    }
                    setupUI(selectedLanguage)
                }
                else {
                    setupUI(intent.getStringExtra("language") ?: "English")
                    isFirstSelection = false
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing if nothing is selected
            }
        }
    }

    private fun changeLanguage(languageCode: String) {
        println(languageCode)
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(languageCode))
        // Save the selected language in SharedPreferences or any other way you prefer
        saveLanguagePreference(languageCode)
    }

    private fun saveLanguagePreference(language: String) {
        val sharedPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        with (sharedPref.edit()) {
            putString("language", language)
            apply()
        }
    }
    private fun setupUI(selectedLanguage: String) {
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val languageSpinner: Spinner = findViewById(R.id.languageSpinner)

        // Use "en" as default language if language extra is missing
        val language = if (selectedLanguage == "") intent.getStringExtra("language") ?: "English" else selectedLanguage
        val result = if (language == "English" || language == "en") {
            "data_en.json"
        } else {
            "data_rw.json"
        }

        val selectedLang = when {
            selectedLanguage.isEmpty() && (language == "English" || language == "en") -> 0
            selectedLanguage == "English" -> 0
            else -> 1
        }
        languageSpinner.setSelection(selectedLang)

        jsonData = readJsonFromAssets(this, result)

        val items: List<AuditCategories> = auditParser(jsonData)

        // Handle case where JSON parsing fails or data is empty
        adapter = AuditAdapter(items, this)
        recyclerView.adapter = adapter
    }

    override fun onItemClick(position: Int) {
        startActivityAfterClick(position)
    }

    private fun startActivityAfterClick(position: Int) {
        val intent = Intent(this, AddNewActivity::class.java)
        intent.putExtra("auditId", position)
        intent.putExtra("audit", jsonData)
        startActivity(intent)
    }
}
