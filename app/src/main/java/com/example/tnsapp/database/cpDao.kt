import androidx.room.*
import com.example.tnsapp.database.Answers
import com.example.tnsapp.database.AuditCategories
import com.example.tnsapp.database.Categories
import com.example.tnsapp.database.Questions

@Dao
interface AuditCategoriesDao {
    @Query("SELECT * FROM audit_categories")
    suspend fun getAll(): List<AuditCategories>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(auditCategory: AuditCategories)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(auditCategories: List<AuditCategories>)

}

@Dao
interface CategoriesDao {
    @Query("SELECT * FROM categories")
    suspend fun getAll(): List<Categories>

    @Query("SELECT * FROM categories WHERE audit_id = :auditId")
    suspend fun getByAuditId(auditId: Long): List<Categories>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: Categories)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<Categories>)

    @Delete
    suspend fun delete(category: Categories)
}

@Dao
interface QuestionsDao {
    @Query("SELECT * FROM questions")
    suspend fun getAll(): List<Questions>

    @Query("SELECT * FROM questions WHERE cat_id = :catId")
    suspend fun getByCategoryId(catId: Long): List<Questions>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(question: Questions)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(questions: List<Questions>)

    @Delete
    suspend fun delete(question: Questions)
}

@Dao
interface AnswersDao {
    @Query("SELECT * FROM answers")
    suspend fun getAll(): List<Answers>

    @Query("SELECT * FROM answers WHERE q_id = :questionId")
    suspend fun getByQuestionId(questionId: Long): List<Answers>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(answer: Answers)

    @Delete
    suspend fun delete(answer: Answers)
}
