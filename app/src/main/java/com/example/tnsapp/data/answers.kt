package com.example.tnsapp.data
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "answers")

data class Answers(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "responder_name") val responderName: String,
    var answer: String,
    @ColumnInfo(name = "q_id") val qId: Long,
    @ColumnInfo(name = "cws_name") val cwsName: String,
) {
    companion object {
        const val YES = "yes"
        const val NO = "no"
        const val IGNORE = "ignore"
    }
}