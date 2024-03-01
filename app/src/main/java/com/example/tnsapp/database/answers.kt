package com.example.tnsapp.database
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "answers")
data class Answers(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val responder: String,
    val questionId: Long,
    val answer: String
)