package com.example.tnsapp.database
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "categories",
    foreignKeys = [
        ForeignKey(
            entity = AuditCategories::class,
            parentColumns = ["id"],
            childColumns = ["audit_id"],
            onDelete = ForeignKey.CASCADE
        )
    ])
data class Categories(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    @ColumnInfo(name = "icon_path") val iconPath: String,
    @ColumnInfo(name = "audit_id") val auditId: Long
)
