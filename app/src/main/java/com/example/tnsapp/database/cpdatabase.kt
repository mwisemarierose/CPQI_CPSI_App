import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.tnsapp.database.Answers
import com.example.tnsapp.database.AnswersDao
import com.example.tnsapp.database.AuditList
import com.example.tnsapp.database.AuditListDao
import com.example.tnsapp.database.Categories
import com.example.tnsapp.database.CategoriesDao
import com.example.tnsapp.database.Questions
import com.example.tnsapp.database.QuestionsDao

@Database(entities = [AuditList::class, Categories::class, Questions::class, Answers::class], version = 1, exportSchema = false)
abstract class CPDatabase : RoomDatabase() {
    abstract fun auditListDao(): AuditListDao
    abstract fun categoriesDao(): CategoriesDao
    abstract fun questionsDao(): QuestionsDao
    abstract fun answersDao(): AnswersDao
}
