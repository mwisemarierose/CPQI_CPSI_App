package com.technoserve.cpqi


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.DashPathEffect
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.androidplot.util.PixelUtils
import com.androidplot.xy.*
import com.technoserve.cpqi.parsers.readJsonFromAssets
import java.text.FieldPosition
import java.text.Format
import java.text.ParsePosition
import kotlin.math.roundToInt

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
        val plot = findViewById<XYPlot>(R.id.plot)

        // create a couple arrays of y-values to plot:
        val domainLabels = arrayOf(1, 2, 3, 6, 7, 8, 9, 10, 13, 14)
        val series1Numbers = arrayOf(1, 4, 2, 8, 4, 16, 8, 32, 16, 64)
        val series2Numbers = arrayOf(5, 2, 10, 5, 20, 10, 40, 20, 80, 40)

        val series1 = SimpleXYSeries(
            series1Numbers.toList(), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Date"
        )
        val series2 = SimpleXYSeries(
            series2Numbers.toList(), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Scores"
        )

        val series1Format = LineAndPointFormatter(this, R.xml.line_point_formatter_with_labels)
        val series2Format = LineAndPointFormatter(this, R.xml.line_point_formatter_with_labels_2)

        series2Format.linePaint.pathEffect = DashPathEffect(floatArrayOf(
            PixelUtils.dpToPix(20f),
            PixelUtils.dpToPix(15f)
        ), 0f)


        series1Format.interpolationParams = CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal)
        series2Format.interpolationParams = CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal)


        plot.addSeries(series1, series1Format)
        plot.addSeries(series2, series2Format)

        plot.graph.getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).format = object : Format() {
            override fun format(obj: Any?, toAppendTo: StringBuffer, pos: FieldPosition): StringBuffer {
                val i = Math.round((obj as Number).toFloat())
                return toAppendTo.append(domainLabels[i])
            }

            override fun parseObject(source: String, pos: ParsePosition): Any? {
                return null
            }
        }
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