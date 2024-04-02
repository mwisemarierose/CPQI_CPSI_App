package com.example.tnsapp

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.view.menu.MenuPopupHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tnsapp.adapters.AddNewListAdapter
import com.example.tnsapp.adapters.CategoryAdapter
import com.example.tnsapp.data.AppDatabase
import com.example.tnsapp.data.Categories
import com.example.tnsapp.data.RecordedAudit
import com.example.tnsapp.parsers.categoryParser
import com.example.tnsapp.parsers.readJsonFromAssets
import com.example.tnsapp.utils.getAuditId
import kotlinx.coroutines.DelicateCoroutinesApi
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddNewActivity : AppCompatActivity(), AddNewListAdapter.OnItemClickListener,
    CategoryAdapter.OnItemClickListener {
    private var auditName = ""
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AddNewListAdapter
    private lateinit var db: AppDatabase

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addnew)
        supportActionBar?.hide()
        val backIconBtn: ImageView = findViewById(R.id.backIcon)
        val toolBarTitle: TextView = findViewById(R.id.toolbarTitle)
        val auditId = intent.getIntExtra("auditId", 0)
        val audit = intent.getStringExtra("audit")

        val parsedAudit =
            if (audit != null) JSONObject(JSONObject(audit).getJSONArray("audits")[auditId - 1].toString()) else JSONObject()

        toolBarTitle.text = if (parsedAudit.has("name")) parsedAudit["name"].toString() else "Audit"
        auditName = if (parsedAudit.has("name")) parsedAudit["name"].toString() else "Audit"
        sharedPreferences = getSharedPreferences("AnswersPref", MODE_PRIVATE)
        editor = sharedPreferences.edit()

        setupUI(audit.toString())

        backIconBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun setupUI(audit: String) {
        val auditId = intent.getIntExtra("auditId", 0)
        val addNewBtn = findViewById<Button>(R.id.addNewBtn)

//        access answers from room db
        db = AppDatabase.getDatabase(this)!!

        val getAnswers = db.answerDao().getAll()
        // Date format for Room DB date
        val roomDateFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH)

        // Get today's date and format it to yyyy-MM-dd
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(Date())

        val todaysAnswers = getAnswers.filter {

            // Get the date from your Room DB
            val roomDbDate = roomDateFormat.parse(it.date)

            // Format the date to yyyy-MM-dd
            val formattedDate = roomDbDate?.let { it1 ->
                SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(
                    it1
                )
            }

            formattedDate?.compareTo(today) == 0 && it.auditId == auditId.toLong()
        }

        if (todaysAnswers.isNotEmpty()){
//            addNewBtn.isEnabled = false
            addNewBtn.backgroundTintList = ColorStateList.valueOf(R.color.maroonDisabled)
        } else {
            addNewBtn.isEnabled = true
            addNewBtn.backgroundTintList = ColorStateList.valueOf(R.color.maroon)
        }

        addNewBtn.setOnClickListener {
//            if (it.isEnabled) {
                openCategoryActivity(auditId, audit)
//            }
        }

//        get answers by unique date from datetime
        val uniqueDates = getAnswers
            .map { it.date.substring(0, 10) }
            .distinct()

        val result = uniqueDates.map { date ->
            val answer = getAnswers.find { it.date.substring(0, 10) == date && it.auditId == auditId.toLong()}
            RecordedAudit(
                cwsName = answer?.cwsName ?: "",
                score = 0,
                date = date
            )
        }

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = AddNewListAdapter(result, this)
        recyclerView.adapter = adapter
    }

    @SuppressLint("RestrictedApi")
    fun showDropdownMenu(v: View?) {
        val menuBuilder = MenuBuilder(this)
        val inflater = MenuInflater(this)
        inflater.inflate(R.menu.dropdown_menu, menuBuilder)
        val optionsMenu = v?.let { MenuPopupHelper(this, menuBuilder, it) }
        optionsMenu?.setForceShowIcon(true)

        menuBuilder.setCallback(object : MenuBuilder.Callback {
            override fun onMenuItemSelected(menu: MenuBuilder, item: MenuItem): Boolean {
                return false
            }

            override fun onMenuModeChange(menu: MenuBuilder) {}
        })

        optionsMenu?.show()
    }


    private fun openCategoryActivity(auditId: Int, audit: String?) {
        val intent = Intent(this, CategoriesActivity::class.java)
        intent.putExtra("auditId", auditId)
        intent.putExtra("audit", audit)
        intent.putExtra("auditName", auditName)
        startActivity(intent)
    }

    override fun onItemClick(position: Int) {
        TODO("Not yet implemented")
    }
}