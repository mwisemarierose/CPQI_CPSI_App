package com.example.tnsapp.parsers

import com.example.tnsapp.data.Answers
import android.content.Context
import androidx.room.Room
import com.example.tnsapp.data.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AddNewListParser(context: Context){
    private val db = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java, "cpq_db"
    ).build()

    suspend fun fetchAnswers(): Array<Answers> {
        return withContext(Dispatchers.IO) {
            db.answerDao().getAll() // Assuming you have a DAO method to get all answers from the database
        }
    }


}
