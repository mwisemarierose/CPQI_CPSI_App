package com.example.tnsapp.database
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "answers",
    foreignKeys = [
        ForeignKey(
            entity = Questions::class,
            parentColumns = ["id"],
            childColumns = ["q_id"],
            onDelete = ForeignKey.CASCADE
        )
    ])
data class Answers(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "responder_name") val responderName: String,
    val answer: String,
    @ColumnInfo(name = "q_id") val qId: Long
)
