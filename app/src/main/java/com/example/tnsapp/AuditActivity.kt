package com.example.tnsapp

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
import androidx.recyclerview.widget.RecyclerView
import com.example.tnsapp.adapters.AuditAdapter
import com.example.tnsapp.data.AuditCategories
import com.example.tnsapp.parsers.auditParser
import com.example.tnsapp.parsers.readJsonFromAssets

class AuditActivity : AppCompatActivity(), AuditAdapter.OnItemClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AuditAdapter
    private lateinit var jsonData: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audit)
        val language = getSelectedLanguage()
        supportActionBar?.hide()
        onClickListener()

        intent.getStringExtra("language")?.let { setupUI(it) }

        val languageSpinner: Spinner = findViewById(R.id.languageSpinner)
        setupLanguageSpinner(languageSpinner)
    }
    private fun getSelectedLanguage(): String {
        val sharedPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return sharedPref.getString("language", "en") ?: "en"
    }

    private fun onClickListener() {
        val addstation = findViewById<Button>(R.id.addStation)
        addstation.setOnClickListener {
            openStationActivity(getSelectedLanguage())
        }
    }
    private fun openStationActivity(language: String) {
        val intent = Intent(this, NewstationActivity::class.java)
        intent.putExtra("language", language)
        startActivity(intent)
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
    }

    private fun saveLanguagePreference(language: String) {
        val sharedPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("language", language)
            apply()
        }
    }

    private fun setupUI(selectedLanguage: String) {
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val languageSpinner: Spinner = findViewById(R.id.languageSpinner)

        // Use "en" as default language if language extra is missing
        val result = if (selectedLanguage == "English" || selectedLanguage == "en") {
            "data_en.json"
        } else {
            "data_rw.json"
        }

        val selectedLang = if (selectedLanguage == "English" || selectedLanguage == "en") 0 else 1

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
