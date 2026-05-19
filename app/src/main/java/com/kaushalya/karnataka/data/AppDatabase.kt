package com.kaushalya.karnataka.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.kaushalya.karnataka.data.dao.PortfolioDao
import com.kaushalya.karnataka.data.dao.ReviewDao
import com.kaushalya.karnataka.data.dao.ServiceDao
import com.kaushalya.karnataka.data.dao.WorkerDao
import com.kaushalya.karnataka.data.entity.PortfolioPhoto
import com.kaushalya.karnataka.data.entity.Review
import com.kaushalya.karnataka.data.entity.Service
import com.kaushalya.karnataka.data.entity.Worker

@Database(
    entities = [
        Worker::class,
        Service::class,
        Review::class,
        PortfolioPhoto::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun workerDao(): WorkerDao
    abstract fun serviceDao(): ServiceDao
    abstract fun reviewDao(): ReviewDao
    abstract fun portfolioDao(): PortfolioDao

    companion object {
        @Volatile private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "kaushalya.db",
            )
                .fallbackToDestructiveMigration()
                .build()
                .also { instance = it }
        }
    }
}
