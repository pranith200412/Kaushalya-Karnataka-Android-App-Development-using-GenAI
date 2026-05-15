package com.kaushalya.karnataka.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "portfolio_photos",
    foreignKeys = [
        ForeignKey(
            entity = Worker::class,
            parentColumns = ["id"],
            childColumns = ["workerId"],
            onDelete = ForeignKey.CASCADE,
        )
    ],
    indices = [Index("workerId")],
)
data class PortfolioPhoto(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val workerId: Long,
    val imageUri: String,
    val caption: String = "",
    val createdAt: Long = System.currentTimeMillis(),
)
