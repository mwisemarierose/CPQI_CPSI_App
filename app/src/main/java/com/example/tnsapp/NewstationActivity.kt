package com.example.tnsapp

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.tnsapp.adapters.ImageSliderAdapter
import com.example.tnsapp.data.AppDatabase
import com.example.tnsapp.data.Cws
import com.smarteist.autoimageslider.SliderView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class  NewstationActivity : AppCompatActivity() {
    private lateinit var db: AppDatabase
    private val imageResources = arrayOf(
        R.drawable.donna,
        R.drawable.reach,
        R.drawable.inside
    )
    @SuppressLint("MissingInflatedId", "CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.newstation)

        val sliderView: SliderView = findViewById(R.id.slider)
        val sliderAdapter = ImageSliderAdapter(imageResources)
        sliderView.setSliderAdapter(sliderAdapter)
        sliderView.scrollTimeInSec = 3
        sliderView.isAutoCycle = true
        sliderView.startAutoCycle()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        db = AppDatabase.getDatabase(this)!!

        val addBtn = findViewById<Button>(R.id.Add)
        val backIconBtn: ImageView = findViewById(R.id.backIcon)
        backIconBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        addBtn.setOnClickListener {
            val cwsName = findViewById<EditText>(R.id.cwsName).text.toString()
            val cwsLeader = findViewById<EditText>(R.id.cwsLeader).text.toString()
            val location = findViewById<EditText>(R.id.location).text.toString()

            lifecycleScope.launch {
                val existingCws = db.cwsDao().getCwsByName(cwsName)
                val successMessage = getString(R.string.toast_message)
                val duplicateMessage = getString(R.string.toast_message)

              //set error on empty fields if any
                if (cwsName.isEmpty()) {
                    findViewById<EditText>(R.id.cwsName).error = getString(R.string.cws_error)
                    findViewById<EditText>(R.id.cwsName).requestFocus()
                    return@launch
                }
//                if (cwsLeader.isEmpty()) {
//                    findViewById<EditText>(R.id.cwsLeader).error = getString(R.string.leader_error)
//                    findViewById<EditText>(R.id.cwsLeader).requestFocus()
//                    return@launch
//                }
//                if (location.isEmpty()) {
//                    findViewById<EditText>(R.id.location).error = getString(R.string.address_error)
//                    findViewById<EditText>(R.id.location).requestFocus()
//                    return@launch
//                }

                if (existingCws == null) {
                    val auditId = intent.getIntExtra("audit_id", 0)
                    val cws = Cws(cwsName = cwsName, cwsLeader = cwsLeader, location = location, auditId = auditId)
                    db.cwsDao().insert(cws)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@NewstationActivity, successMessage, Toast.LENGTH_SHORT).show()
                    }
                    delay(2000)
                    setResult(Activity.RESULT_OK)
                    finish()
                } else {
                    withContext(Dispatchers.Main) { // Update UI on Main thread
                        Toast.makeText(this@NewstationActivity, duplicateMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}