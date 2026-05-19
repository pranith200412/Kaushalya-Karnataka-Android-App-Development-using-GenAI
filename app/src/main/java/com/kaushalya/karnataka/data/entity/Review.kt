package com.kaushalya.karnataka.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "reviews",
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
data class Review(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val workerId: Long,
    val reviewerName: String,
    val rating: Int,
    val comment: String,
    val createdAt: Long = System.currentTimeMillis(),
)
