package com.example.tnsapp.database

import android.content.Context
import androidx.room.RoomDatabase
import AppDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DatabaseCallback(private val context: Context) : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        val appDatabase = AppDatabase.getDatabase(context)
        populateDatabase(appDatabase)
    }

    private fun populateDatabase(db: AppDatabase) {
        GlobalScope.launch {
            val auditCategoriesDao = db.auditCategoryDao()
            val categoriesDao = db.categoryDao()
            val questionsDao = db.questionDao()

            // Define your static data
            val auditCategories = listOf(
                AuditCategories(name = "Audit 1", iconPath = ""),
                AuditCategories(name = "Audit 2", iconPath = "")
            )
            val categories = listOf(
                Categories(name = "Category 1", iconPath = "path/to/icon3.png", auditId = 1),
                Categories(name = "Category 2", iconPath = "path/to/icon4.png", auditId = 1),
                Categories(name = "Category 3", iconPath = "path/to/icon5.png", auditId = 2)
            )
            val questions = listOf(
                Questions(qName = "Question 1", catId = 1),
                Questions(qName = "Question 2", catId = 1),
                Questions(qName = "Question 3", catId = 2),
                Questions(qName = "Question 4", catId = 3)
            )
            // Insert data into respective tables
            auditCategoriesDao.insertAll(auditCategories)
            categoriesDao.insertAll(categories)
            questionsDao.insertAll(questions)
        }
    }
}
