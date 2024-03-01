package com.example.tnsapp.database
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Categories(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val auditListId: Long
)