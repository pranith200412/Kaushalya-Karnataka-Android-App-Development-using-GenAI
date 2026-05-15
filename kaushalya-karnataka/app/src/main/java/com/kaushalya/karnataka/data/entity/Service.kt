package com.kaushalya.karnataka.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "services",
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
data class Service(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val workerId: Long,
    val name: String,
    val price: Int,
    val isStartingAt: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
)
