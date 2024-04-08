package com.example.tnsapp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AnswersDao {
    @Query("SELECT * FROM answers")
    fun getAll(): Array<Answers>

    @Query("SELECT * FROM answers WHERE q_id = :questionId")
    suspend fun getByQuestionId(questionId: Long): Array<Answers>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(answer: Answers)

    @Insert(onConflict = OnConflictStrategy.NONE)
    fun insertAll(answers: Array<Answers>)

    @Delete
    suspend fun delete(answer: Answers)
}
@Dao
interface CwsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(cws: Cws)

    @Query("SELECT * FROM cws")
    fun getAll(): Array<Cws>
    @Insert(onConflict = OnConflictStrategy.NONE)
    fun insertAll(cws: Array<Cws>)

    @Delete
    suspend fun delete(cws: Cws)
}