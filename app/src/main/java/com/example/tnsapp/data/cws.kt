package com.example.tnsapp.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "cws")
data class Cws(
    @PrimaryKey
    val id: UUID = UUID.randomUUID(),
    @ColumnInfo(name = "cws_name") val cwsName: String,
    @ColumnInfo(name = "cws_leader") val cwsLeader: String,
    @ColumnInfo(name = "location") val location: String,
)