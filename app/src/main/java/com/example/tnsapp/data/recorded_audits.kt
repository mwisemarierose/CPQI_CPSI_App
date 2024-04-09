package com.example.tnsapp.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date


@Entity(tableName = "recordedAudit")
data class RecordedAudit(
    @PrimaryKey(autoGenerate = true) val id: Long? = 0,
    @ColumnInfo(name = "cwsName") val cwsName: String,
    @ColumnInfo(name = "score") val score: Int,
    @ColumnInfo(name = "date") val date: String = Date().toString()
)
