package com.fitting.lenzdelivery

import com.fitting.lenzdelivery.models.RiderOrder
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object OrderEventBus {
    private val _newOrders = MutableSharedFlow<RiderOrder>()
    val newOrders = _newOrders.asSharedFlow()

    suspend fun emitNewOrder(order: RiderOrder) {
        _newOrders.emit(order)
    }
}