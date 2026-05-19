package com.kaushalya.karnataka.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kaushalya.karnataka.data.entity.PortfolioPhoto
import kotlinx.coroutines.flow.Flow

@Dao
interface PortfolioDao {

    @Query("SELECT * FROM portfolio_photos WHERE workerId = :workerId ORDER BY createdAt DESC")
    fun observeForWorker(workerId: Long): Flow<List<PortfolioPhoto>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(photo: PortfolioPhoto): Long

    @Delete
    suspend fun delete(photo: PortfolioPhoto)
}
