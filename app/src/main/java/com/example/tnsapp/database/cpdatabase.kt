import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.tnsapp.database.Answers
import com.example.tnsapp.database.AuditCategories
import com.example.tnsapp.database.Categories
import com.example.tnsapp.database.Questions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@Database(entities = [AuditCategories::class, Categories::class, Questions::class, Answers::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun auditCategoryDao(): AuditCategoriesDao
    abstract fun categoryDao(): CategoriesDao
    abstract fun questionDao(): QuestionsDao
    abstract fun answerDao(): AnswersDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "cpq_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }

        fun populateDatabase(context: Context) {
            GlobalScope.launch(Dispatchers.IO) {
                val database = getDatabase(context)
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
                database.auditCategoryDao().insertAll(auditCategories)
                database.categoryDao().insertAll(categories)
                database.questionDao().insertAll(questions)
            }
        }
    }
}
