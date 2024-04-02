package com.example.tnsapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.migration.Migration
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Answers::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun answerDao(): AnswersDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

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
            ).allowMainThreadQueries()
                .addMigrations(Migration1To2())
                .build()
        }
    }

    class Migration1To2 : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE Answers ADD COLUMN audit_id INTEGER NOT NULL DEFAULT 0") // Modify this line if needed (e.g., change data type or add constraints)
        }
    }
}



