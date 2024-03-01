package com.example.tnsapp.database
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "questions")
data class Questions(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val categoryId: Long
)