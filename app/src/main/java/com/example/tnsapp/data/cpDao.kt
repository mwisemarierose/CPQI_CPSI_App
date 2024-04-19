package com.example.tnsapp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface AnswersDao {


    @Query("SELECT * FROM answers")
    fun getAll(): Array<Answers>

    @Query("SELECT * FROM answers WHERE q_id = :questionId")
    suspend fun getByQuestionId(questionId: Long): Array<Answers>

    @Query("SELECT * FROM answers WHERE audit_id = :auditId")
     fun getAllByAuditId(auditId: Int): Array<Answers>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(answer: Answers)

    @Insert(onConflict = OnConflictStrategy.NONE)
    fun insertAll(answers: Array<Answers>)

    @Delete
    suspend fun delete(answer: Answers)
    @Query("SELECT COUNT(*) > 0 FROM answers WHERE cws_name = :cwsName")
    fun hasCompletedAudit(cwsName: String): Boolean
}
@Dao
interface CwsDao {
    @Query("SELECT * FROM cws WHERE cws_name = :name LIMIT 1")
    suspend fun getCwsByName(name: String): Cws?

    @Query("SELECT * FROM cws WHERE cws_name = :name")
    suspend fun getAllCwsByName(name: String): Cws?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(cws: Cws)

    @Query("SELECT * FROM cws")
    fun getAll(): Array<Cws>
    @Insert(onConflict = OnConflictStrategy.NONE)
    fun insertAll(cws: Array<Cws>)

    @Delete
    suspend fun delete(cws: Cws)
    @Query("SELECT * FROM cws WHERE audit_id = :auditId")
    fun getCwsByAuditId(auditId: Int): List<Cws>

//    @Transaction
//    @Query("SELECT * FROM cws")
//    suspend fun getAllWithIds(): List<CwsWithId>
}
//data class CwsWithId(
//    val id: Long,
//    val cwsName: String, // Assuming cwsName exists in your Cws class
//)

@Dao
interface RecordedAuditDao {

    @Query("SELECT * FROM RecordedAudit")
    suspend fun getAll(): List<RecordedAudit>

    @Query("SELECT EXISTS(SELECT * FROM RecordedAudit WHERE cwsName = :cwsName AND date = :date)")
    suspend fun existsForCwsToday(cwsName: String, date: String): Boolean
}