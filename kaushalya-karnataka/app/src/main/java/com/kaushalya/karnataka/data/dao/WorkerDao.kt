package com.kaushalya.karnataka.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.kaushalya.karnataka.data.entity.Worker
import com.kaushalya.karnataka.data.entity.WorkerWithStats
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkerDao {

    @Query("""
        SELECT w.*,
               COALESCE(AVG(r.rating), 0.0) AS averageRating,
               (SELECT COUNT(*) FROM reviews WHERE workerId = w.id) AS reviewCount,
               (SELECT COUNT(*) FROM services WHERE workerId = w.id) AS serviceCount,
               (SELECT MIN(price) FROM services WHERE workerId = w.id) AS minPrice
        FROM workers w
        LEFT JOIN reviews r ON r.workerId = w.id
        WHERE (:category IS NULL OR w.category = :category)
          AND (
            :query = ''
            OR LOWER(w.name) LIKE '%' || LOWER(:query) || '%'
            OR LOWER(w.bio)  LIKE '%' || LOWER(:query) || '%'
            OR LOWER(w.town) LIKE '%' || LOWER(:query) || '%'
            OR LOWER(w.category) LIKE '%' || LOWER(:query) || '%'
          )
        GROUP BY w.id
        ORDER BY averageRating DESC, w.name COLLATE NOCASE ASC
    """)
    fun search(query: String, category: String?): Flow<List<WorkerWithStats>>

    @Query("""
        SELECT w.*,
               COALESCE(AVG(r.rating), 0.0) AS averageRating,
               (SELECT COUNT(*) FROM reviews WHERE workerId = w.id) AS reviewCount,
               (SELECT COUNT(*) FROM services WHERE workerId = w.id) AS serviceCount,
               (SELECT MIN(price) FROM services WHERE workerId = w.id) AS minPrice
        FROM workers w
        LEFT JOIN reviews r ON r.workerId = w.id
        WHERE w.id = :id
        GROUP BY w.id
    """)
    fun observeWithStats(id: Long): Flow<WorkerWithStats?>

    @Query("SELECT * FROM workers WHERE id = :id")
    suspend fun getById(id: Long): Worker?

    @Query("SELECT COUNT(*) FROM workers")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(worker: Worker): Long

    @Update
    suspend fun update(worker: Worker)

    @Delete
    suspend fun delete(worker: Worker)
}
