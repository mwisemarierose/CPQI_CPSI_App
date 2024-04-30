package com.example.tnsapp

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.view.menu.MenuPopupHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tnsapp.adapters.AddNewListAdapter
import com.example.tnsapp.adapters.CategoryAdapter
import com.example.tnsapp.data.Answers
import com.example.tnsapp.data.AppDatabase
import com.example.tnsapp.data.Categories
import com.example.tnsapp.data.Questions
import com.example.tnsapp.data.RecordedAudit
import com.example.tnsapp.parsers.allAuditQuestionsParser
import com.example.tnsapp.parsers.categoryParser
import com.example.tnsapp.utils.isTodayDate
import org.json.JSONObject
import java.io.PrintWriter
import kotlin.properties.Delegates

class AddNewActivity : AppCompatActivity(), AddNewListAdapter.OnItemClickListener,
    CategoryAdapter.OnItemClickListener {
    private var auditName = ""
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyView: TextView
    private lateinit var adapter: AddNewListAdapter
    private lateinit var db: AppDatabase
    private lateinit var items: List<Questions>
    private lateinit var categories: List<Categories>
    private lateinit var getAnswers: Array<Answers>
    private var auditId by Delegates.notNull<Int>()
    private lateinit var audit: String
    private lateinit var uniqueResult: Map<String, RecordedAudit>

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addnew)
        supportActionBar?.hide()
        val backIconBtn: ImageView = findViewById(R.id.backIcon)
        val toolBarTitle: TextView = findViewById(R.id.toolbarTitle)
        auditId = intent.getIntExtra("auditId", 0)
        audit = intent.getStringExtra("audit").toString()
        items = allAuditQuestionsParser(audit, auditId)

        categories = categoryParser(audit, auditId)

        val parsedAudit =
            JSONObject(JSONObject(audit).getJSONArray("audits")[auditId - 1].toString())

        toolBarTitle.text = if (parsedAudit.has("name")) parsedAudit["name"].toString() else "Audit"
        auditName = if (parsedAudit.has("name")) parsedAudit["name"].toString() else "Audit"
        sharedPreferences = getSharedPreferences("AnswersPref", MODE_PRIVATE)
        editor = sharedPreferences.edit()

        setupUI(audit, auditId)

        backIconBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun setupUI(audit: String, auditId: Int) {
//        val auditId = intent.getIntExtra("auditId", 0)
        val addNewBtn = findViewById<Button>(R.id.addNewBtn)

//        access answers from room db
        db = AppDatabase.getDatabase(this)!!

        getAnswers = db.answerDao().getAllByAuditId(auditId)

        addNewBtn.setOnClickListener {
            openCategoryActivity(auditId, audit)
        }

        // Remove date filtering logic here
        val result = getAnswers.map {
            RecordedAudit(
                null,
                it.auditId.toInt(),
                cwsName = it.cwsName,
                respondent = it.responderName,
                score = if (it.answer == Answers.YES) 1 else 0,
                date = it.date,
                groupedAnswersId = it.groupedAnswersId
            )
        }

        uniqueResult = result.groupBy { it.groupedAnswersId }.mapValues { (_, audits) ->
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
        }

        emptyView = findViewById(R.id.emptyTextView)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = AddNewListAdapter(uniqueResult, result.size, items.size, this)
        recyclerView.adapter = adapter

        if (result.isEmpty() || result[0].auditId != auditId) {
            emptyView.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            emptyView.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }

    @SuppressLint("RestrictedApi")
    fun showDropdownMenu(v: View?) {
        val menuBuilder = MenuBuilder(this)
        val inflater = MenuInflater(this)
        inflater.inflate(R.menu.dropdown_menu, menuBuilder)

        // Add an export menu item

        val optionsMenu = v?.let { MenuPopupHelper(this, menuBuilder, it) }
        optionsMenu?.setForceShowIcon(true)

        menuBuilder.setCallback(object : MenuBuilder.Callback {
            override fun onMenuItemSelected(menu: MenuBuilder, item: MenuItem): Boolean {
                return when (item.itemId) {
                    R.id.export_option -> {
                        // Export data to CSV
                        downloadCsv(this@AddNewActivity)

                        Toast.makeText(this@AddNewActivity, "Exporting data...", Toast.LENGTH_SHORT)
                            .show()
                        true
                    }

                    else -> false
                }
            }

            override fun onMenuModeChange(menu: MenuBuilder) {}
        })

        optionsMenu?.show()
    }

    // Assuming this function is called from an Activity or Fragment
    fun downloadCsv(activity: Activity) {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "text/csv"
            putExtra(
                Intent.EXTRA_TITLE, "cpqi_audits-${
                    System.currentTimeMillis()
                }.csv"
            )
        }

        activity.startActivityForResult(intent, requestCodeCreateDocument)
    }

    private val requestCodeCreateDocument = 1001

    // This function should be called from onActivityResult in the calling Activity or Fragment
    private fun handleActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        uniqueResult: Map<String, RecordedAudit>,
        activity: Activity
    ) {
        if (requestCode == requestCodeCreateDocument && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                activity.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    PrintWriter(outputStream.bufferedWriter()).use { writer ->
                        writer.println("Audit,CWS Name,Respondent,Total Score,Score Percentage,Date,Category,Question,Answer,Grouped Answers Id")
                        for (p in uniqueResult) {
                            var line: String
                            for ((index, answer) in getAnswers.toList().filter {
                                it.groupedAnswersId == p.value.groupedAnswersId
                            }.withIndex()) {
                                if (index == 0) {
                                    line =
                                        "${JSONObject(JSONObject(audit).getJSONArray("audits")[p.value.auditId - 1].toString())["name"]},${p.value.cwsName},${p.value.respondent},${p.value.score} / ${items.size},${
                                            p.value.score * 100 / items.size
                                        }%,${p.value.date},${
                                            categories.find {
                                                it.id == (items.find { i -> i.id == answer.qId }?.catId?.plus(
                                                    1
                                                ))
                                            }?.name
                                        },\"${
                                            items.find { it.id == answer.qId }?.qName
                                        }\",${answer.answer},${answer.groupedAnswersId}"
                                } else {
                                    line =
                                        if (items.find { i ->
                                                i.id == getAnswers.filter {
                                                    it.groupedAnswersId == p.value.groupedAnswersId
                                                }.toList()[index - 1].qId
                                            }?.catId != items.find { i ->
                                                i.id == getAnswers.filter {
                                                    it.groupedAnswersId == p.value.groupedAnswersId
                                                }.toList()[index].qId
                                            }?.catId) {
                                            ",,,,,,${
                                                categories.find {
                                                    it.id == (items.find { i -> i.id == answer.qId }?.catId?.plus(
                                                        1
                                                    ))
                                                }?.name
                                            },\"${
                                                items.find { it.id == answer.qId }?.qName
                                            }\",${answer.answer},"
                                        } else {
                                            ",,,,,,,\"${
                                                items.find { it.id == answer.qId }?.qName
                                            }\",${answer.answer},"
                                        }
                                }
                                writer.println(line)
                            }
                        }

                        writer.flush()
                    }
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == requestCodeCreateDocument) {
            handleActivityResult(requestCode, resultCode, data, uniqueResult, this)
        }
    }

    private fun openCategoryActivity(auditId: Int, audit: String?) {
        val intent = Intent(this, CategoriesActivity::class.java)
        intent.putExtra("auditId", auditId)
        intent.putExtra("audit", audit)
        intent.putExtra("auditName", auditName)
        startActivity(intent)
    }

    override fun onItemClick(position: Int) {
        val intent = Intent(this@AddNewActivity, CategoriesActivity::class.java)
        intent.putExtra("auditId", auditId)
        intent.putExtra("audit", audit)
        if (db.answerDao().getAllByAuditId(auditId).isNotEmpty()) {
            intent.putExtra(
                "selectedGroupedAnswerId",
                uniqueResult.entries.elementAt(position).value.groupedAnswersId
            )
            intent.putExtra(
                "editMode", isTodayDate(
                    uniqueResult.entries.elementAt(position).value.date
                )
            )
            intent.putExtra(
                "viewMode", !isTodayDate(
                    uniqueResult.entries.elementAt(position).value.date
                )
            )
        }
        startActivity(intent)
    }
}