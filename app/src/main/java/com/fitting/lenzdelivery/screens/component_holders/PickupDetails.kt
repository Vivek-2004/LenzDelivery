package com.fitting.lenzdelivery.screens.component_holders

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.fitting.lenzdelivery.DeliveryViewModel

@Composable
fun PickupDetails(
    orderKey: String,
    deliveryViewModel: DeliveryViewModel
) {
    val order = deliveryViewModel.riderOrders.first { it.orderKey == orderKey }
    Column {
        Text(order.toString())
    }
}