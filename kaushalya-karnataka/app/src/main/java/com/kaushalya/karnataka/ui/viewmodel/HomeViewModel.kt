package com.kaushalya.karnataka.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kaushalya.karnataka.data.entity.WorkerWithStats
import com.kaushalya.karnataka.data.repo.WorkerRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(private val repo: WorkerRepository) : ViewModel() {

    val query = MutableStateFlow("")
    val category = MutableStateFlow<String?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val workers: Flow<List<WorkerWithStats>> =
        combine(query, category) { q, c -> q to c }
            .flatMapLatest { (q, c) -> repo.search(q, c) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    class Factory(private val repo: WorkerRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            HomeViewModel(repo) as T
    }
}
