package com.kaushalya.karnataka.data.entity

import androidx.room.Embedded

data class WorkerWithStats(
    @Embedded val worker: Worker,
    val averageRating: Float,
    val reviewCount: Int,
    val serviceCount: Int,
    val minPrice: Int?,
)
