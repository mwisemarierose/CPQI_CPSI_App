package com.example.tnsapp

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tnsapp.adapters.CategoryAdapter
import com.example.tnsapp.data.Answers
import com.example.tnsapp.data.AppDatabase
import com.example.tnsapp.data.Categories
import com.example.tnsapp.data.Cws
import com.example.tnsapp.parsers.categoryParser
import com.example.tnsapp.utils.formatDate
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.properties.Delegates

class CategoriesActivity : AppCompatActivity(), CategoryAdapter.OnItemClickListener,
    PopupActivity.DialogDismissListener, View.OnClickListener {
    companion object {
        private const val REQUEST_CODE_ADD_CWS = 100  // You can choose any unique value
    }
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CategoryAdapter
    private lateinit var audit: String
    private var auditId by Delegates.notNull<Int>()
    private lateinit var respondentContainer: LinearLayout
    private lateinit var respondent: TextView
    //    private lateinit var cwsName: TextView
    private lateinit var submitAll: Button
    private lateinit var dialog: PopupActivity
    private var answerDetails: Array<Answers> = emptyArray()
    private lateinit var cwsName: Spinner
    private var progress = 0
    private lateinit var progressBar: ProgressBar
    private lateinit var percentageText: TextView
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private val gson = Gson()
    private var json: String = ""

    //    initialize room db
    private lateinit var db: AppDatabase

    @SuppressLint("SetTextI18n", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories)
        supportActionBar?.hide()
        db = AppDatabase.getDatabase(this)!!
        fetchCwsData()
        onClickListener()
        val backIconBtn: ImageView = findViewById(R.id.backIcon)
        submitAll = findViewById(R.id.submitAllBtn)
        val toolBarTitle: TextView = findViewById(R.id.toolbarTitle)
        auditId = intent.getIntExtra("auditId", 0)
        audit = intent.getStringExtra("audit").toString()
        val items: List<Categories> = categoryParser(audit, auditId)
        progressBar = findViewById(R.id.scoreProgressBar)
        percentageText = findViewById(R.id.percentageText)
        val score = 0

        progressBar.progress = score

        // Update percentage text
        percentageText.text = "$score%"

        sharedPreferences = getSharedPreferences("AnswersPref", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()

        setupUI(items)

        val dateTextView = findViewById<TextView>(R.id.todaysDateTextView)

        val currentDate = Date()

        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(currentDate)

        dateTextView.text = "${applicationContext.getString(R.string.date_lbl)} $formattedDate"

        toolBarTitle.text = intent.getStringExtra("auditName")

        backIconBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        submitAll.isEnabled = false
        submitAll.backgroundTintList =
            ColorStateList.valueOf(resources.getColor(if (submitAll.isEnabled) R.color.maroon else R.color.maroonDisabled))

        db = AppDatabase.getDatabase(this)!!

        submitAll.setOnClickListener {
            val answers = gson.fromJson(
                sharedPreferences.getString("answers", json),
                Array<Answers>::class.java
            )

//            if respondent or cwsName is not selected, show error message
            if (respondent.text.isEmpty() || cwsName.selectedItem == null) {
                Toast.makeText(
                    this,
                    applicationContext.getText(R.string.missing_field_error_alert_msg),
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

//            check if in answers there is cwName equal to selected cwsName and it is recorded today
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val cwsName = cwsName.selectedItem.toString()
            val existingAnswers = db.answerDao().getAll()
                .filter { it.cwsName == cwsName && formatDate(it.date) == today }
            if (existingAnswers.isNotEmpty()) {
                Toast.makeText(
                    this,
                    applicationContext.getText(R.string.already_recorded_error_alert_msg),
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            Thread {
                db.answerDao().insertAll(answers)
            }.start()

//            remove shared preferences after submitting answers
            editor.remove("answers")
            editor.apply()

            Toast.makeText(
                this,
                applicationContext.getText(R.string.success_alert_msg),
                Toast.LENGTH_SHORT
            ).show()

//            add delay before going back to recorded audits activity
            Thread.sleep(2000)

            val intent = Intent(this, AddNewActivity::class.java)
            intent.putExtra("auditId", auditId)
            intent.putExtra("audit", audit)
            startActivity(intent)
        }
    }

    private fun onClickListener() {
        val addStation = findViewById<Button>(R.id.addStation)
        addStation.setOnClickListener {
            openStationActivity(getSelectedLanguage())
        }
    }

    private fun openStationActivity(language: String) {
        val intent = Intent(this, NewstationActivity::class.java)
        intent.putExtra("language", language)
        startActivityForResult(intent, REQUEST_CODE_ADD_CWS)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_ADD_CWS && resultCode == Activity.RESULT_OK) {
            fetchCwsData()
        }
    }

    private fun getSelectedLanguage(): String {
        val sharedPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return sharedPref.getString("language", "en") ?: "en"
    }

    private fun fetchCwsData() {
        CoroutineScope(Dispatchers.IO).launch {
            val cwsList = db.cwsDao().getAll()

            // Create an ArrayAdapter with CWS names (or relevant data)
            val adapter = ArrayAdapter<String>(
                this@CategoriesActivity,
                android.R.layout.simple_spinner_dropdown_item,
                getCwsNames(cwsList)
            )
            // Update UI on the main thread
            runOnUiThread {
                cwsName.adapter = adapter
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

    private fun setupUI(items: List<Categories>?) {
        respondentContainer = findViewById(R.id.textInputLayoutContainer)
        respondent = findViewById(R.id.nameEditText)
//        cwsName = findViewById(R.id.cwsNameEditText)
        cwsName = findViewById(R.id.cwsNameSpinner)

//        add respondent name and cws name to answerDetails when the user enters them
        respondent.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                if (answerDetails.isNotEmpty()) {
                    answerDetails = answerDetails.map {
                        Answers(
                            null,
                            respondent.text.toString(),
                            it.answer,
                            it.qId,
                            auditId.toLong(),
                            cwsName.selectedItem.toString()
                        )
                    }.toTypedArray()
                } else {
                    answerDetails = answerDetails.plus(
                        Answers(
                            null,
                            respondent.text.toString(),
                            "",
                            items!![0].id,
                            auditId.toLong(),
                            cwsName.selectedItem.toString()
                        )
                    )
                }
            }
        }

        cwsName.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (answerDetails.isNotEmpty()) {
                    answerDetails = answerDetails.map {
                        Answers(
                            null,
                            it.responderName,
                            it.answer,
                            it.qId,
                            auditId.toLong(),
                            cwsName.selectedItem.toString()
                        )
                    }.toTypedArray()
                } else {
                    answerDetails = answerDetails.plus(
                        Answers(
                            null,
                            respondent.text.toString(),
                            "",
                            items!![0].id,
                            auditId.toLong(),
                            cwsName.selectedItem.toString()
                        )
                    )
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing if nothing is selected
            }
        }
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = items?.let { CategoryAdapter(it, this) }!!
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = adapter
    }

    override fun onItemClick(position: Int) {
        startActivityAfterClick(position)
    }

    private fun startActivityAfterClick(position: Int) {
        val auditId = intent.getIntExtra("auditId", 0)

        dialog = PopupActivity(
            this,
            auditId,
            audit,
            position,
            adapter.items[position - 1].name,
            answerDetails,
            respondent.text.toString(),
            if (cwsName.selectedItem != null) cwsName.selectedItem.toString() else ""
        )
        dialog.setDismissListener(this)
        dialog.show()
    }

    @SuppressLint("SetTextI18n")
    override fun onDialogDismissed(updatedAnswers: Array<Answers>?) {
        updatedAnswers?.forEach { updatedAnswer ->
            val existingAnswer = answerDetails.find { it.qId == updatedAnswer.qId }
            if (existingAnswer != null) {
                // Update existing answer in answerDetails
                val index = answerDetails.indexOf(existingAnswer)
                answerDetails[index] = updatedAnswer
            } else {
                // Add new answer to answerDetails
                answerDetails = answerDetails.plus(updatedAnswer)
            }
        }

        if (answerDetails.isNotEmpty()) {
            submitAll.isEnabled = true
            submitAll.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(if (submitAll.isEnabled) R.color.maroon else R.color.maroonDisabled))
        } else {
            submitAll.isEnabled = false
            submitAll.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(if (submitAll.isEnabled) R.color.maroon else R.color.maroonDisabled))
        }

//        Update progress bar
        answerDetails.forEach {
            println(it.toString())
        }

        progress = (answerDetails.count { it.answer == Answers.YES } * 100) / answerDetails.size
        println(progress)
        val score = progress

        progressBar.progress = score

        // Update percentage text
        percentageText.text = "$score%"

//        add shared preferences to save answers
        json = gson.toJson(answerDetails)
        editor.putString("answers", json)
        editor.apply()
    }

    override fun onClick(v: View?) {
        TODO("Not yet implemented")
    }
}