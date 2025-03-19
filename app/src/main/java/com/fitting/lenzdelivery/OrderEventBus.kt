package com.fitting.lenzdelivery

import com.fitting.lenzdelivery.models.NotificationData
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object OrderEventBus {
    private val _newOrders = MutableSharedFlow<NotificationData>()
    val newOrders = _newOrders.asSharedFlow()

    suspend fun emitNewOrder(order: NotificationData) {
        _newOrders.emit(order)
    }
}