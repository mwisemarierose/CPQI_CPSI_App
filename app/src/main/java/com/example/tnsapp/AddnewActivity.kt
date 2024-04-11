package com.example.tnsapp

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
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
import com.example.tnsapp.data.Answers
import com.example.tnsapp.data.AppDatabase
import com.example.tnsapp.data.RecordedAudit
import org.json.JSONObject

class AddNewActivity : AppCompatActivity(), AddNewListAdapter.OnItemClickListener,
    CategoryAdapter.OnItemClickListener {
    private var auditName = ""
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyView: TextView
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

        // Remove date filtering logic here
        val result = getAnswers.map {
            RecordedAudit(null, it.auditId.toInt(), cwsName = it.cwsName,
                score = if(it.answer == Answers.YES) 1 else 0,
                date = it.date) }

        result.forEach {
            println(it.toString())
        }

        val lastItem = result.lastOrNull()

        emptyView = findViewById(R.id.emptyTextView)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = AddNewListAdapter(lastItem?.let { listOf(it) } ?: emptyList(),
            result.sumOf { it.score })
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