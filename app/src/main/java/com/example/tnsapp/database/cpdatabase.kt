import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.tnsapp.database.Answers
import com.example.tnsapp.database.AuditCategories
import com.example.tnsapp.database.Categories
import com.example.tnsapp.database.Questions

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
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            if (INSTANCE == null) {
                synchronized(this) {
                    // Pass the database to the INSTANCE
                    INSTANCE = buildDatabase(context)
                }
            }
            // Return database.
            return INSTANCE!!
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "cpq_db"
            )
                .build()
        }
    }
}
