package com.kaushalya.karnataka.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kaushalya.karnataka.data.entity.PortfolioPhoto
import com.kaushalya.karnataka.data.entity.Review
import com.kaushalya.karnataka.data.entity.Service
import com.kaushalya.karnataka.data.entity.Worker
import com.kaushalya.karnataka.data.entity.WorkerWithStats
import com.kaushalya.karnataka.data.repo.WorkerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val repo: WorkerRepository,
    private val workerId: Long,
) : ViewModel() {

    val worker: Flow<WorkerWithStats?> =
        repo.observeWorker(workerId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val services: Flow<List<Service>> =
        repo.observeServices(workerId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val reviews: Flow<List<Review>> =
        repo.observeReviews(workerId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val portfolio: Flow<List<PortfolioPhoto>> =
        repo.observePortfolio(workerId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun upsertService(service: Service) = viewModelScope.launch {
        repo.upsertService(service.copy(workerId = workerId))
    }

    fun deleteService(service: Service) = viewModelScope.launch {
        repo.deleteService(service)
    }

    fun addReview(name: String, rating: Int, comment: String) = viewModelScope.launch {
        repo.addReview(
            Review(
                workerId = workerId,
                reviewerName = name.trim(),
                rating = rating,
                comment = comment.trim(),
            )
        )
    }

    fun addPortfolio(path: String) = viewModelScope.launch {
        repo.addPortfolioPhoto(PortfolioPhoto(workerId = workerId, imageUri = path))
    }

    fun deletePortfolio(photo: PortfolioPhoto) = viewModelScope.launch {
        repo.deletePortfolioPhoto(photo)
    }

    fun deleteWorker(worker: Worker) = viewModelScope.launch {
        repo.deleteWorker(worker)
    }

    class Factory(
        private val repo: WorkerRepository,
        private val workerId: Long,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            ProfileViewModel(repo, workerId) as T
    }
}
