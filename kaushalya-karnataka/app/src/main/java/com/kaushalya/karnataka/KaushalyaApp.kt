package com.kaushalya.karnataka

import android.app.Application
import com.kaushalya.karnataka.data.AppDatabase
import com.kaushalya.karnataka.data.SeedData
import com.kaushalya.karnataka.data.repo.WorkerRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class KaushalyaApp : Application() {

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val database by lazy { AppDatabase.getInstance(this) }
    val repository by lazy { WorkerRepository(database) }

    override fun onCreate() {
        super.onCreate()
        appScope.launch { SeedData.populateIfEmpty(repository) }
    }
}
