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
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tnsapp.adapters.CategoryAdapter
import com.example.tnsapp.data.Answers
import com.example.tnsapp.data.AppDatabase
import com.example.tnsapp.data.Categories
import com.example.tnsapp.data.Cws
import com.example.tnsapp.data.Questions
import com.example.tnsapp.parsers.allAuditQuestionsParser
import com.example.tnsapp.parsers.categoryParser
import com.example.tnsapp.utils.formatDate
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import kotlin.properties.Delegates

class CategoriesActivity : AppCompatActivity(), CategoryAdapter.OnItemClickListener,
    PopupActivity.DialogDismissListener, View.OnClickListener {
    companion object {
        private const val REQUEST_CODE_ADD_CWS = 100
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CategoryAdapter
    private lateinit var audit: String
    private var auditId by Delegates.notNull<Int>()
    private lateinit var respondentContainer: LinearLayout
    private lateinit var respondent: TextView
    private lateinit var submitAll: Button
    private lateinit var dialog: PopupActivity
    private var answerDetails: Array<Answers> = emptyArray()
    private lateinit var cwsName: Spinner
    private var progress = 0
    private lateinit var progressBar: ProgressBar
    private lateinit var percentageText: TextView
    private lateinit var addStation: Button
    private var editMode = false
    private var viewMode = false
    private var selectedGroupedAnswerId = ""
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private val gson = Gson()
    private var json: String = ""
    private var existingAnswers: List<Answers> = emptyList()

    //    initialize room db
    private lateinit var db: AppDatabase
    private var items: List<Categories> = emptyList()
    private var allCatQuestions: List<Questions> = emptyList()

    @SuppressLint("SetTextI18n", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories)
        supportActionBar?.hide()
        db = AppDatabase.getDatabase(this)!!
        fetchCwsData()
        editMode = intent.getBooleanExtra("editMode", false)
        viewMode = intent.getBooleanExtra("viewMode", false)
        selectedGroupedAnswerId = intent.getStringExtra("selectedGroupedAnswerId").toString()
        val backIconBtn: ImageView = findViewById(R.id.backIcon)
        submitAll = findViewById(R.id.submitAllBtn)
        val toolBarTitle: TextView = findViewById(R.id.toolbarTitle)
        auditId = intent.getIntExtra("auditId", 0)
        audit = intent.getStringExtra("audit").toString()
        items = categoryParser(audit, auditId)
        allCatQuestions = allAuditQuestionsParser(audit, auditId)
        progressBar = findViewById(R.id.scoreProgressBar)
        percentageText = findViewById(R.id.percentageText)
        addStation = findViewById(R.id.addStation)
        onClickListener(addStation)
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

        if (viewMode) submitAll.visibility = View.GONE else submitAll.visibility = View.VISIBLE

        submitAll.isEnabled = false
        submitAll.backgroundTintList =
            ColorStateList.valueOf(resources.getColor(if (submitAll.isEnabled) R.color.maroon else R.color.maroonDisabled))

        db = AppDatabase.getDatabase(this)!!

        //handle submission on new answers and already existing answers in edit mode updating the existing answers in the db

        submitAll.setOnClickListener {
            val groupedAnswersId = UUID.randomUUID().toString()

            val answers = gson.fromJson(
                sharedPreferences.getString("answers", json),
                Array<Answers>::class.java
            )

            if (editMode) {
//                loop through answers and check if id is null, add new item in existingAnswers, otherwise update existing item
                answers.forEach {
                    if (it.id == null) {
                        existingAnswers = existingAnswers.plus(
                            Answers(
                                null,
                                it.responderName,
                                it.answer,
                                it.qId,
                                it.auditId,
                                cwsName = existingAnswers.last().cwsName,
                                groupedAnswersId = existingAnswers.last().groupedAnswersId,
                            )
                        )
                    } else {
                        val index = existingAnswers.indexOfFirst { answer -> answer.qId == it.qId }
                        existingAnswers = existingAnswers.toMutableList().apply {
                            this[index] = it
                        }
                    }
                }
            }

            // Update each answer with a unique id
            answers.forEach {
                it.groupedAnswersId = groupedAnswersId
            }

            // If respondent is not selected, show error message
            if (respondent.text.isEmpty()) {
                respondent.error = getString(R.string.missing_respondent_error)
                respondent.requestFocus()
                return@setOnClickListener
            }

            if (!editMode) {
                // Insert new answers into the database
                Thread {
                    db.answerDao().insertAll(answers)
                }.start()
            } else {
                // Update existing answers in the database
                Thread {
                    db.answerDao().updateAnswer(existingAnswers.toTypedArray())

//                    filter answers with null id and insert them into the db
                    val newAnswers = answers.filter { it.id == null }.map {
                        Answers(
                            null,
                            it.responderName,
                            it.answer,
                            it.qId,
                            it.auditId,
                            cwsName = existingAnswers.last().cwsName,
                            groupedAnswersId = existingAnswers.last().groupedAnswersId,
                        )
                    }
                    db.answerDao().insertAll(newAnswers.toTypedArray())
                }.start()
            }
//             Remove shared preferences after submitting answers
            editor.remove("answers")
            editor.apply()

            Toast.makeText(
                this,
                applicationContext.getText(R.string.success_alert_msg),
                Toast.LENGTH_SHORT
            ).show()
            Thread.sleep(2000)

            val intent = Intent(this, AddNewActivity::class.java)
            intent.putExtra("auditId", auditId)
            intent.putExtra("audit", audit)
            startActivity(intent)
        }

    }

    private fun onClickListener(addStation: Button) {
        addStation.setOnClickListener {
            openStationActivity(getSelectedLanguage())
        }
    }

    private fun openStationActivity(language: String) {
        val intent = Intent(this, NewstationActivity::class.java)
        intent.putExtra("auditId", auditId)
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
            val adapter = ArrayAdapter(
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

    private fun disableRecyclerView(recyclerView: RecyclerView) {
        // Disable all child views of the RecyclerView
        for (i in 0 until recyclerView.childCount) {
            val child = recyclerView.getChildAt(i)
            child.isClickable = false
            child.isFocusable = false
        }
        // Change background color of RecyclerView to grey
        recyclerView.setBackgroundColor(ContextCompat.getColor(this, R.color.lightGrey))
    }

    private fun enableRecyclerView(recyclerView: RecyclerView) {
        // Enable all child views of the RecyclerView
        for (i in 0 until recyclerView.childCount) {
            val child = recyclerView.getChildAt(i)
            child.isClickable = true
            child.isFocusable = true
        }
        // Change background color of RecyclerView to white
        recyclerView.setBackgroundColor(ContextCompat.getColor(this, R.color.LightPink1))
    }

    @SuppressLint("SetTextI18n")
    private fun setupUI(items: List<Categories>?) {
        respondentContainer = findViewById(R.id.textInputLayoutContainer)
        respondent = findViewById(R.id.nameEditText)
        cwsName = findViewById(R.id.cwsNameSpinner)

//        if editMode is true, load answers
        if (editMode) {
//            get today's answers corresponding with auditId
            existingAnswers = db.answerDao().getAll()
                .filter { it.groupedAnswersId == selectedGroupedAnswerId }

            respondent.text = existingAnswers.last().responderName
            respondent.isEnabled = false
            val cwsList = db.cwsDao().getAll()

            // Create an ArrayAdapter with CWS names (or relevant data)
            val adapter = ArrayAdapter(
                this@CategoriesActivity,
                android.R.layout.simple_spinner_dropdown_item,
                getCwsNames(cwsList)
            )
            cwsName.setSelection(adapter.getPosition(existingAnswers.last().cwsName))
            cwsName.isEnabled = false
            cwsName.adapter = adapter

            addStation.visibility = View.GONE

//            update progress bar
            progress =
                (existingAnswers.count { it.answer == Answers.YES } * 100) / allCatQuestions.size
            val score = progress

            progressBar.progress = score

            percentageText.text = "$score%"
        } else if (viewMode) {
            // Get today's answers corresponding with auditId
            existingAnswers = db.answerDao().getAll()
                .filter { it.groupedAnswersId == selectedGroupedAnswerId }

            respondent.text = existingAnswers.last().responderName
            respondent.isEnabled = false

            val cwsList = db.cwsDao().getAll()

            // Create an ArrayAdapter with CWS names (or relevant data)
            val adapter = ArrayAdapter(
                this@CategoriesActivity,
                android.R.layout.simple_spinner_dropdown_item,
                getCwsNames(cwsList)
            )

            cwsName.setSelection(adapter.getPosition(existingAnswers.last().cwsName))
            cwsName.isEnabled = false
            cwsName.adapter = adapter

            addStation.visibility = View.GONE

            // Update progress bar
            progress =
                (existingAnswers.count { it.answer == Answers.YES } * 100) / allCatQuestions.size
            val score = progress

            progressBar.progress = score

            // Update percentage text
            percentageText.text = "$score%"
        } else {
            progressBar.progress = 0
            percentageText.text = "0%"
        }

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
                            cwsName.selectedItem.toString(),
                            ""
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
                            cwsName.selectedItem.toString(),
                            ""
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
                val selectedCwsName = cwsName.selectedItem.toString()
                val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                val existingAnswers = db.answerDao().getAll()
                    .filter { it.cwsName == selectedCwsName && formatDate(it.date) == today && it.auditId.toInt() == auditId }

                if (existingAnswers.isNotEmpty()) {
                    // Display error message immediately
                    if (!editMode) {
                        (parent?.getChildAt(0) as? TextView)?.error =
                            getString(R.string.already_recorded_error_alert_msg)
                    }
                    // display alert-dialog with error message
                    Toast.makeText(
                        this@CategoriesActivity,
                        applicationContext.getText(R.string.already_recorded_error_alert_msg),
                        Toast.LENGTH_SHORT
                    ).show()
                    //disable the gridLayout below spinner
                    if (!editMode) disableRecyclerView(recyclerView)
                } else {
                    // No existing answers found, proceed with adding/updating answerDetails
                    if (answerDetails.isNotEmpty()) {
                        answerDetails = answerDetails.map {
                            it.copy(cwsName = selectedCwsName)
                        }.toTypedArray()
                    } else {
                        answerDetails = arrayOf(
                            Answers(
                                null,
                                respondent.text.toString(),
                                "",
                                items?.getOrNull(0)?.id ?: 0,
                                auditId.toLong(),
                                selectedCwsName,
                                ""
                            )
                        )
                    }
                    if (!editMode) enableRecyclerView(recyclerView)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing if nothing is selected
            }
        }

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = items?.let {
            CategoryAdapter(
                it,
                this,
                applicationContext,
                editMode,
                viewMode,
                existingAnswers,
                allCatQuestions
            )
        }!!
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
            if (cwsName.selectedItem != null) cwsName.selectedItem.toString() else "",
            editMode,
            viewMode,
            existingAnswers
        )
        dialog.setDismissListener(this)
        dialog.show()
    }
    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    override fun onDialogDismissed(updatedAnswers: Array<Answers>?, categoryId: Int) {
        adapter.updateColor(categoryId)
        adapter.notifyDataSetChanged()
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

        progress = (answerDetails.count { it.answer == Answers.YES } * 100) / allCatQuestions.size
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