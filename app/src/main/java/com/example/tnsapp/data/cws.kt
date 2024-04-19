package com.example.tnsapp.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cws")

data class Cws (
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "cws_name") val cwsName: String,
    @ColumnInfo(name = "cws_leader") val cwsLeader: String,
    @ColumnInfo(name = "location") val location: String,
    @ColumnInfo(name = "audit_id") val auditId: Int
)