package com.example.tnsapp

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.view.menu.MenuPopupHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tnsapp.adapters.AddNewListAdapter
import com.example.tnsapp.adapters.CategoryAdapter
import com.example.tnsapp.data.Answers
import com.example.tnsapp.data.AppDatabase
import com.example.tnsapp.data.Questions
import com.example.tnsapp.data.RecordedAudit
import com.example.tnsapp.parsers.allAuditQuestionsParser
import com.example.tnsapp.utils.formatDate
import com.example.tnsapp.utils.isTodayDate
import org.json.JSONObject
import java.io.OutputStream
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
    private var auditId by Delegates.notNull<Int>()
    private lateinit var audit: String

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

        val getAnswers = db.answerDao().getAllByAuditId(auditId)

        addNewBtn.setOnClickListener {
            openCategoryActivity(auditId, audit)
        }

        // Remove date filtering logic here
        val result = getAnswers.map {
            RecordedAudit(
                null, it.auditId.toInt(), cwsName = it.cwsName,
                score = if (it.answer == Answers.YES) 1 else 0,
                date = it.date,
            )

        }

        val uniqueResult = result
            .groupBy { it.cwsName to formatDate(it.date) }
            .mapValues { (_, audits) ->
                audits.sumBy { it.score }
            }

        // Print the result
        uniqueResult.forEach { (key, value) ->
            val (cwsName, date) = key
            println("cwsName: $cwsName, Date: $date, Total Score: $value")
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
                        // Handle export action

                        val people = listOf(
                            Person("Alice", 30),
                            Person("Bob", 25),
                            Person("Charlie", 35)
                        )
                        val fileName = "people.csv"
                        exportToCSV(people, fileName)

                        true
                    }

                    else -> false
                }
            }

            override fun onMenuModeChange(menu: MenuBuilder) {}
        })

        optionsMenu?.show()
    }
    //function to export data in csv format to external storage

    data class Person(val name: String, val age: Int)

    // Define an activity result contract for starting activities for result
    class MyStartActivityForResultContract : ActivityResultContract<Intent, ActivityResult>() {
        override fun createIntent(context: Context, input: Intent): Intent {
            return input
        }

        override fun parseResult(resultCode: Int, intent: Intent?): ActivityResult {
            return ActivityResult(resultCode, intent)
        }
    }

    // Define a data class to hold the activity result
    data class ActivityResult(val resultCode: Int, val data: Intent?)

    // Function to launch activity for result
    private fun startMyActivityForResult(
        intent: Intent,
        callback: (ActivityResult) -> Unit
    ) {
        val launcher = registerForActivityResult(MyStartActivityForResultContract()) { result ->
            callback(result)
        }
        launcher.launch(intent)
    }

    fun exportToCSV(people: List<Person>, fileName: String) {
        val createDocumentLauncher = startMyActivityForResult(intent) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                // Handle the result as needed
                if (data != null) {
                    val uri = data.data
                    uri?.let {
                        val contentResolver = applicationContext.contentResolver
                        val outputStream: OutputStream? = contentResolver.openOutputStream(uri)

                        if (outputStream != null) {
                            PrintWriter(outputStream.bufferedWriter()).use { writer ->
                                // Write the header row
                                writer.println("Name,Age")

                                // Write each farm's data
                                for (person in people) {
                                    val line =
                                        "${person.name},${person.age}"
                                    writer.println(line)
                                }
                            }
                        }
                    }
                }
            }
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
        intent.putExtra(
            "editMode",
            db.answerDao().getAllByAuditId(auditId).isNotEmpty() && isTodayDate(
                db.answerDao().getAllByAuditId(auditId)[0].date
            )
        )
        intent.putExtra(
            "viewMode", db.answerDao().getAllByAuditId(auditId).isNotEmpty() && !isTodayDate(
                db.answerDao().getAllByAuditId(auditId)[0].date
            )
        )
        startActivity(intent)
    }
}