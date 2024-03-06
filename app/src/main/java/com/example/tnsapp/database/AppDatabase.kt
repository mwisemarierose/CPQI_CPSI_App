package com.example.tnsapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@Database(entities = [Categories::class, Questions::class, Answers::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoriesDao
    abstract fun questionDao(): QuestionsDao
    abstract fun answerDao(): AnswersDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null
        private val LOCK = Any()

        fun getDatabase(context: Context): AppDatabase? {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            if (INSTANCE == null) {
                synchronized(this) {
                    // Pass the database to the INSTANCE
                    INSTANCE = buildDatabase(context)
                }
            }
            // Return database.
            return INSTANCE
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "cpq_db"
            )
            .addCallback(RoomDatabaseCallback(context.applicationContext, INSTANCE))
                .build()
        }
    }

    private class RoomDatabaseCallback(private val context: Context, instance: AppDatabase?) : RoomDatabase.Callback() {
        @OptIn(DelicateCoroutinesApi::class)
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                GlobalScope.launch(Dispatchers.IO) {
                    prepopulateDatabase(context, database.categoryDao())
                }
            }
        }

        private suspend fun prepopulateDatabase(context: Context, categoryDao: CategoriesDao) {
            val categories = listOf(
                Categories(name = "Cherry Reception", iconPath = "path_to_icon1", auditName = "CPQI"),
                Categories(name = "Pulping", iconPath = "path_to_icon2", auditName = "CPQS"),
            )
            categoryDao.insertAll(categories)
        }
    }
}