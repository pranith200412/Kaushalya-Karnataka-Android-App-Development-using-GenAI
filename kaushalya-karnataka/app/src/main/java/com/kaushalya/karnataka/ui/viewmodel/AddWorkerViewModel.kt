package com.kaushalya.karnataka.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kaushalya.karnataka.data.entity.Worker
import com.kaushalya.karnataka.data.repo.WorkerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class AddWorkerViewModel(
    private val repo: WorkerRepository,
    private val workerId: Long,
) : ViewModel() {

    val initial = MutableStateFlow<Worker?>(null)

    init {
        if (workerId != 0L) {
            viewModelScope.launch { initial.value = repo.getWorker(workerId) }
        }
    }

    suspend fun save(
        name: String,
        category: String,
        phone: String,
        town: String,
        bio: String,
        avatarPath: String?,
    ): Long {
        val existing = initial.value
        val toSave = (existing ?: Worker(
            name = "", category = "", phone = "", town = "",
        )).copy(
            name = name.trim(),
            category = category,
            phone = phone.trim(),
            town = town.trim(),
            bio = bio.trim(),
            avatarUri = avatarPath ?: existing?.avatarUri,
        )
        return repo.upsertWorker(toSave)
    }

    suspend fun delete() {
        initial.value?.let { repo.deleteWorker(it) }
    }

    class Factory(
        private val repo: WorkerRepository,
        private val workerId: Long,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            AddWorkerViewModel(repo, workerId) as T
    }
}
