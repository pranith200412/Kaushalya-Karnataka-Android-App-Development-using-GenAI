package com.kaushalya.karnataka.data.repo

import com.kaushalya.karnataka.data.AppDatabase
import com.kaushalya.karnataka.data.entity.PortfolioPhoto
import com.kaushalya.karnataka.data.entity.Review
import com.kaushalya.karnataka.data.entity.Service
import com.kaushalya.karnataka.data.entity.Worker
import com.kaushalya.karnataka.data.entity.WorkerWithStats
import kotlinx.coroutines.flow.Flow

class WorkerRepository(db: AppDatabase) {

    private val workerDao = db.workerDao()
    private val serviceDao = db.serviceDao()
    private val reviewDao = db.reviewDao()
    private val portfolioDao = db.portfolioDao()

    fun search(query: String, category: String?): Flow<List<WorkerWithStats>> =
        workerDao.search(query, category)

    fun observeWorker(id: Long): Flow<WorkerWithStats?> = workerDao.observeWithStats(id)

    fun observeServices(workerId: Long): Flow<List<Service>> =
        serviceDao.observeForWorker(workerId)

    fun observeReviews(workerId: Long): Flow<List<Review>> =
        reviewDao.observeForWorker(workerId)

    fun observePortfolio(workerId: Long): Flow<List<PortfolioPhoto>> =
        portfolioDao.observeForWorker(workerId)

    suspend fun getWorker(id: Long): Worker? = workerDao.getById(id)

    suspend fun workerCount(): Int = workerDao.count()

    suspend fun upsertWorker(worker: Worker): Long =
        if (worker.id == 0L) workerDao.insert(worker)
        else { workerDao.update(worker); worker.id }

    suspend fun deleteWorker(worker: Worker) = workerDao.delete(worker)

    suspend fun upsertService(service: Service): Long = serviceDao.upsert(service)
    suspend fun deleteService(service: Service) = serviceDao.delete(service)

    suspend fun addReview(review: Review): Long = reviewDao.insert(review)

    suspend fun addPortfolioPhoto(photo: PortfolioPhoto): Long = portfolioDao.insert(photo)
    suspend fun deletePortfolioPhoto(photo: PortfolioPhoto) = portfolioDao.delete(photo)
}
