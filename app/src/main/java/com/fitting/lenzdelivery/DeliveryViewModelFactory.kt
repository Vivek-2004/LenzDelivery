package com.fitting.lenzdelivery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class DeliveryViewModelFactory(private val riderId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DeliveryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DeliveryViewModel(riderId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}