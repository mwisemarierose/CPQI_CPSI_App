package com.technoserve.cpqi

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.androidplot.xy.*
import com.technoserve.cpqi.data.Answers
import com.technoserve.cpqi.data.AppDatabase
import com.technoserve.cpqi.data.Cws
import com.technoserve.cpqi.data.RecordedAudit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.FieldPosition
import java.text.Format
import java.text.ParsePosition

class Statistics : AppCompatActivity() {
    private lateinit var cwsName: Spinner
    private lateinit var plot: XYPlot
    private lateinit var monthSpinner: Spinner
    private lateinit var yearSpinner: Spinner
    private lateinit var db: AppDatabase

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)
        supportActionBar?.hide()
        db = AppDatabase.getDatabase(this)!!
        fetchCwsData()
        setupMonthSpinner()
        setupYearSpinner()

        val backIconBtn: ImageView = findViewById(R.id.backIcon)
        backIconBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        plot = findViewById(R.id.plot)
        PanZoom.attach(plot)
    }

    private fun setupMonthSpinner() {
        monthSpinner = findViewById(R.id.monthSpinner)
        ArrayAdapter.createFromResource(
            this,
            R.array.months,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            monthSpinner.adapter = adapter
        }

        monthSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                updateGraph()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }
    }

    private fun setupYearSpinner() {
        yearSpinner = findViewById(R.id.yearSpinner)
        ArrayAdapter.createFromResource(
            this,
            R.array.years,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            yearSpinner.adapter = adapter
        }

        yearSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                updateGraph()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }
    }

    private fun fetchCwsData() {
        cwsName = findViewById(R.id.cwsNameSpinner)
        lifecycleScope.launch(Dispatchers.IO) {
            val cwsList = db.cwsDao().getAll()

            val adapter = ArrayAdapter(
                this@Statistics,
                android.R.layout.simple_spinner_dropdown_item,
                getCwsNames(cwsList)
            )

            withContext(Dispatchers.Main) {
                cwsName.adapter = adapter
                adapter.notifyDataSetChanged()
                cwsName.setSelection(0)
            }
        }

        cwsName.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                updateGraph()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }
    }

    private fun getCwsNames(cwsList: Array<Cws>): List<String> {
        val names = mutableListOf<String>()
        for (cws in cwsList) {
            names.add(cws.cwsName)
        }
        return names
    }
    private fun getMonthNumber(monthName: String): Int {
        return when (monthName) {
            "January" -> 1
            "February" -> 2
            "March" -> 3
            "April" -> 4
            "May" -> 5
            "June" -> 6
            "July" -> 7
            "August" -> 8
            "September" -> 9
            "October" -> 10
            "November" -> 11
            "December" -> 12
            else -> 0 // Default case, should not happen
        }
    }


    private fun updateGraph() {
        val selectedCws = cwsName.selectedItem.toString()
        val selectedMonthName = monthSpinner.selectedItem.toString()
        val selectedMonth = getMonthNumber(selectedMonthName)
        val selectedYear = yearSpinner.selectedItem.toString()

        lifecycleScope.launch(Dispatchers.IO) {
            val data = getDataForGraph(selectedCws, selectedMonth, selectedYear)

            withContext(Dispatchers.Main) {
                plot.clear()

                val series: XYSeries = SimpleXYSeries(
                    data.map { it.score.toDouble() },
                    SimpleXYSeries.ArrayFormat.Y_VALS_ONLY,
                    "Score"
                )

                val format = LineAndPointFormatter(Color.GREEN, Color.BLACK, null, null)
                format.interpolationParams = CatmullRomInterpolator.Params(
                    10,
                    CatmullRomInterpolator.Type.Centripetal
                )

                plot.addSeries(series, format)

                plot.graph.getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).format = object : Format() {
                    override fun format(
                        obj: Any?,
                        toAppendTo: StringBuffer,
                        pos: FieldPosition
                    ): StringBuffer {
                        val i = Math.round((obj as Number).toFloat())
                        return toAppendTo.append(data[i.toInt()].date)
                    }

                    override fun parseObject(source: String?, pos: ParsePosition): Any? {
                        return null
                    }
                }

                plot.redraw()
            }
        }
    }


    private suspend fun getDataForGraph(cwsName: String, month: Int, year: String): List<RecordedAudit> {
        val monthString = if (month < 10) "0$month" else month.toString()
        val answers = db.answerDao().getAllByCwsAndDate(cwsName, monthString, year)
        val result = answers.map {
            RecordedAudit(
                null,
                it.auditId.toInt(),
                it.cwsName,
                it.responderName,
                if (it.answer == Answers.YES) 1 else 0,
                it.date,
                it.groupedAnswersId
            )
        }

        return result.groupBy { it.groupedAnswersId }.mapValues { (_, audits) ->
            audits.reduce { _, audit ->
                RecordedAudit(
                    null,
                    audit.auditId,
                    audit.cwsName,
                    audit.respondent,
                    audits.sumOf { it.score },
                    audit.groupedAnswersId,
                    audit.date
                )
            }
        }.values.toList()
    }
}
