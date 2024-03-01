package com.example.tnsapp.database
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "auditlists")
data class AuditList(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String
)