package com.example.tnsapp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface AuditListDao {
    @Insert
    suspend fun insert(checklist: AuditList)

    @Query("SELECT * FROM auditlists")
    suspend fun getAllChecklists(): List<AuditList>
}

@Dao
interface CategoriesDao {
    @Insert
    suspend fun insert(category: Categories)

    @Query("SELECT * FROM categories WHERE auditListId = :checklistId")
    suspend fun getCategoriesForAuditList(checklistId: Long): List<Categories>
}

@Dao
interface QuestionsDao {
    @Insert
    suspend fun insert(question: Questions)

    @Query("SELECT * FROM questions WHERE categoryId = :categoryId")
    suspend fun getQuestionsForCategory(categoryId: Long): List<Questions>
}

@Dao
interface AnswersDao {
    @Insert
    suspend fun insert(answer: Answers)

    @Query("SELECT * FROM answers WHERE questionId = :answerId")
    suspend fun getAnswersForQuestion(answerId: Long): List<Answers>
}
