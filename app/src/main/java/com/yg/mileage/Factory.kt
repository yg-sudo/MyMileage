package com.yg.mileage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yg.mileage.data.Repository

// ---- ViewModel Factory for CarViewModel ----
class CarViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CarViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CarViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
