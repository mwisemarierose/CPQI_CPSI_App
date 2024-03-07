package com.example.tnsapp.database
import androidx.room.*
import com.example.tnsapp.database.Answers
import com.example.tnsapp.database.Categories
import com.example.tnsapp.database.Questions


@Dao
interface AnswersDao {
    @Query("SELECT * FROM answers")
    suspend fun getAll(): List<Answers>

    @Query("SELECT * FROM answers WHERE q_id = :questionId")
    suspend fun getByQuestionId(questionId: Long): List<Answers>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(answer: Answers)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(answers: List<Answers>)

    @Delete
    suspend fun delete(answer: Answers)
}