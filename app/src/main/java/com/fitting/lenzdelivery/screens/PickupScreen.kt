package com.fitting.lenzdelivery.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.fitting.lenzdelivery.DeliveryViewModel
import com.fitting.lenzdelivery.navigation.NavigationDestination
import com.fitting.lenzdelivery.screens.components.PickupItem

@Composable
fun PickupScreen(
    deliveryViewModel: DeliveryViewModel,
    navController: NavController
) {
    val eligibleOrders = deliveryViewModel.allGroupOrders.filter {
        (it.trackingStatus == "Internal Tracking") || it.trackingStatus == "Order Placed For Pickup"
    }
    println(eligibleOrders)

    val orderMap = mutableMapOf<String, Int>()

    eligibleOrders.forEach { order ->
        val pickupKey = order.common_pickup_key
        if(!orderMap.containsKey(pickupKey)) {
            orderMap[pickupKey] = 1
        } else {
            orderMap[pickupKey] = (orderMap.getValue(pickupKey) + 1)
        }
    }

    println(orderMap.toString())

    Column {
        PickupItem(
            onCardClick = {
                navController.navigate(NavigationDestination.PickupDetails.name)
            },
            onAssignClick = {

            }
        )
    }
}