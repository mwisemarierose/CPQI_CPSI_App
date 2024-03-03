package com.example.tnsapp.database
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.tnsapp.database.Categories

@Entity(tableName = "questions",
    foreignKeys = [
        ForeignKey(
            entity = Categories::class,
            parentColumns = ["id"],
            childColumns = ["cat_id"],
            onDelete = ForeignKey.CASCADE
        )
    ])
data class Questions(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "q_name") val qName: String,
    @ColumnInfo(name = "cat_id") val catId: Long
)
