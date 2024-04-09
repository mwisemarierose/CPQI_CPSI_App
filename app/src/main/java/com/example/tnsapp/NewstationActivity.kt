package com.example.tnsapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.tnsapp.data.AppDatabase
import com.example.tnsapp.data.Cws
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NewstationActivity : AppCompatActivity() {
    private lateinit var db: AppDatabase
    @SuppressLint("MissingInflatedId")
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

            lifecycleScope.launch { // Use lifecycleScope for context safety
                val existingCws = db.cwsDao().getCwsByName(cwsName) // Check by name

                if (existingCws == null) {
                    val cws = Cws(cwsName = cwsName, cwsLeader = cwsLeader, location = location)
                    db.cwsDao().insert(cws)
                    withContext(Dispatchers.Main) { // Update UI on Main thread
                        Toast.makeText(this@NewstationActivity, "CWS added successfully", Toast.LENGTH_SHORT).show()
                    }
                    finish()
                } else {
                    withContext(Dispatchers.Main) { // Update UI on Main thread
                        Toast.makeText(this@NewstationActivity, "CWS already exists", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}