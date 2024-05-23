package com.technoserve.cpqi


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import com.androidplot.xy.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.technoserve.cpqi.parsers.readJsonFromAssets
import java.text.FieldPosition
import java.text.Format
import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var jsonData: String
    private lateinit var plot: XYPlot
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        val languageSpinner: Spinner = findViewById(R.id.languageSpinner)
        setupLanguageSpinner(languageSpinner)
        val initialLanguage = getInitialLanguage()
        setupUI(initialLanguage)
        onClickListener()
        plot = findViewById(R.id.plot)
        plot = findViewById(R.id.plot)

        // Adjust plot margins and padding
        plot.setPlotMargins(0F, 0F, 0F, 0F)
        plot.setPlotPadding(0F, 0F, 0F, 0F)
        plot.graph.setMargins(50F, 50F, 50F, 50F)
        plot.graph.setPadding(10F, 10F, 10F, 10F)

        // Sample data with dates and scores
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val dates = listOf(
            dateFormat.parse("2024-05-01"),
            dateFormat.parse("2024-05-02"),
            dateFormat.parse("2024-05-03"),
            dateFormat.parse("2024-05-04"),
            dateFormat.parse("2024-05-05")
        )
        val scores = listOf(10, 20, 15, 30, 25)

        // Convert dates to timestamps (or any numerical representation)
        val timestamps = dates.map { it.time.toDouble() }

        val series1: XYSeries = SimpleXYSeries(
            timestamps,
            scores,
            "Score vs Date"
        )

        val series1Format = LineAndPointFormatter(Color.BLUE, Color.BLACK, null, null)
        series1Format.interpolationParams = CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal)

        plot.addSeries(series1, series1Format)

        plot.graph.getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).format = object : Format() {
            override fun format(obj: Any?, toAppendTo: StringBuffer, pos: FieldPosition): StringBuffer {
                val date = Date((obj as Number).toLong())
                return toAppendTo.append(dateFormat.format(date))
            }

            override fun parseObject(source: String?, pos: ParsePosition): Any? {
                return null
            }
        }
        plot.graph.getLineLabelStyle(XYGraphWidget.Edge.LEFT).format = object : Format() {
            override fun format(obj: Any?, toAppendTo: StringBuffer, pos: FieldPosition): StringBuffer {
                val score = (obj as Number).toInt()
                return toAppendTo.append(score.toString())
            }

            override fun parseObject(source: String?, pos: ParsePosition): Any? {
                return null
            }
        }
        val maxDate = timestamps.maxOrNull() ?: 0.0
        val minDate = timestamps.minOrNull() ?: 0.0
        val maxScore = scores.maxOrNull() ?: 0
        val minScore = scores.minOrNull() ?: 0

// Add some padding to the boundaries for better visibility
        val dateRange = maxDate - minDate
        val scorePadding = 0.1 * (maxScore - minScore)

        plot.setDomainBoundaries(minDate - 0.1 * dateRange, maxDate + 0.1 * dateRange, BoundaryMode.AUTO)
        plot.setRangeBoundaries(minScore - scorePadding, maxScore + scorePadding, BoundaryMode.AUTO)
        plot.redraw()
        PanZoom.attach(plot)

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