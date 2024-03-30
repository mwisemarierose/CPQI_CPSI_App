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
import com.example.tnsapp.adapters.AddNewListAdapter
import com.example.tnsapp.data.Answers
import com.example.tnsapp.data.AppDatabase
import com.example.tnsapp.parsers.AddNewListParser
import kotlinx.coroutines.GlobalScope
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddNewActivity : AppCompatActivity() , AddNewListAdapter.OnItemClickListener{
    private var auditName = ""
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    //    initialize room db
    private lateinit var db: AppDatabase

    @OptIn(DelicateCoroutinesApi::class)
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
            JSONObject(JSONObject(audit!!).getJSONArray("audits")[auditId - 1].toString())

        toolBarTitle.text = parsedAudit["name"].toString()
        auditName = parsedAudit["name"].toString()
        sharedPreferences = getSharedPreferences("AnswersPref", MODE_PRIVATE)
        editor = sharedPreferences.edit()

        setupUI(audit.toString())

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        val items = mutableListOf<Answers>()
        val adapter = AddNewListAdapter(items, this)

        recyclerView.adapter = adapter // Set the adapter to the RecyclerView

        // Fetch data from Room Database using AddNewListParser
        val parser = AddNewListParser(this)

        GlobalScope.launch {
            val fetchedData = parser.fetchAnswers()
            withContext(Dispatchers.Main) {
                items.addAll(fetchedData) //
                println(fetchedData)//
                adapter.notifyDataSetChanged()
            }
        }

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

            formattedDate?.compareTo(today) == 0
        }

//        if (todaysAnswers.isNotEmpty()) {
//            addNewBtn.isEnabled = false
//            addNewBtn.backgroundTintList = ColorStateList.valueOf(R.color.maroonDisabled)
//        } else {
//            addNewBtn.isEnabled = true
//            addNewBtn.backgroundTintList = ColorStateList.valueOf(R.color.maroon)
//        }

        addNewBtn.setOnClickListener {
//            if (it.isEnabled) {
                openCategoryActivity(auditId, audit)
//            }
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