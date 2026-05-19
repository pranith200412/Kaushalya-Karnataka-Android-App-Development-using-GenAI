package com.kaushalya.karnataka.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workers")
data class Worker(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val category: String,
    val phone: String,
    val town: String,
    val bio: String = "",
    val avatarUri: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
)
