package com.example.tnsapp.database
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Categories(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val auditName :String,
    @ColumnInfo(name = "icon_path") val iconPath: String,

)
