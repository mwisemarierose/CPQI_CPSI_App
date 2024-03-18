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

                    isFirstSelection = false

                    setupUI(selectedLanguage)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing if nothing is selected
            }
        }
    }

    private fun changeLanguage(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)
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
        println(selectedLanguage)
        println("......")
        val language = if (selectedLanguage == "") intent.getStringExtra("language") ?: "English" else selectedLanguage
        println(language)
        val result = if (language == "English" ) {
            "data_en.json"
        } else {
            "data_rw.json"
        }

        languageSpinner.setSelection(if (selectedLanguage == "" && language == "English") 0 else languageSpinner.selectedItemPosition)

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
