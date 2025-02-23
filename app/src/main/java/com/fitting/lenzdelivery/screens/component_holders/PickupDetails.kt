package com.fitting.lenzdelivery.screens.component_holders

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.fitting.lenzdelivery.DeliveryViewModel

@Composable
fun PickupDetails(deliveryViewModel: DeliveryViewModel) {
    Column {
        Column {
            Text(text = deliveryViewModel.allGroupOrders.filter {
                (it.trackingStatus == "Internal Tracking") || it.trackingStatus == "Order Placed For Pickup"
            }.toString())
        }
    }
}