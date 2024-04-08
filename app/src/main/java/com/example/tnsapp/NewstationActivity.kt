package com.example.tnsapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.tnsapp.data.AppDatabase
import com.example.tnsapp.data.Cws
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class NewstationActivity : AppCompatActivity() {
    private lateinit var db: AppDatabase
    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.newstation)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        db = AppDatabase.getDatabase(this)!!

        val addBtn = findViewById<Button>(R.id.Add)
        addBtn.setOnClickListener {
            val cwsName = findViewById<EditText>(R.id.cwsName).text.toString()
            val cwsLeader = findViewById<EditText>(R.id.cwsLeader).text.toString()
            val location = findViewById<EditText>(R.id.location).text.toString()

            val cws = Cws(cwsName = cwsName, cwsLeader = cwsLeader, location = location)

            GlobalScope.launch {
                db.cwsDao().insert(cws)
            }
            finish()
        }
    }

    }
