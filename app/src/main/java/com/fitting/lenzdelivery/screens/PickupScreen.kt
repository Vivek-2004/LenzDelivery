package com.fitting.lenzdelivery.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.fitting.lenzdelivery.DeliveryViewModel
import com.fitting.lenzdelivery.screens.components.PickupItem
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PickupScreen(
    deliveryViewModel: DeliveryViewModel,
    navController: NavController,
) {
    val listState = rememberLazyListState()
    val scrollBarWidth = 5.dp
    val scrollState = rememberScrollState()
    val pullToRefreshState = rememberPullToRefreshState()
    var isRefreshing by remember { mutableStateOf(false) }

    val riderState by deliveryViewModel.riderDetails.collectAsState()

    val eligibleOrders = deliveryViewModel.allGroupOrders.filter {
        (it.trackingStatus == "Internal Tracking") || it.trackingStatus == "Order Placed For Pickup"
    }
    println(eligibleOrders)
    val orderMap = mutableMapOf<String, Int>()

    eligibleOrders.forEach { order ->
        val pickupKey = order.common_pickup_key ?: return@forEach
        if(!orderMap.containsKey(pickupKey)) {
            orderMap[pickupKey] = 1
        } else {
            orderMap[pickupKey] = (orderMap.getValue(pickupKey) + 1)
        }
    }
    println(orderMap)

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            riderState == null -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            else -> {
                riderState?.let { rider ->
                    LaunchedEffect(isRefreshing) {
                        if (!isRefreshing) return@LaunchedEffect
                        try {
                            deliveryViewModel.getGroupOrders()
                            delay(1500L)
                        } finally {
                            isRefreshing = false
                        }
                    }
                    PullToRefreshBox(
                        state = pullToRefreshState,
                        isRefreshing = isRefreshing,
                        onRefresh = {
                            isRefreshing = true
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White.copy(alpha = 0.09f))
                    ) {
                        LazyColumn(
                            state = listState,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxSize()
                                .drawBehind {
                                    val elementHeight = this.size.height / listState.layoutInfo.totalItemsCount
                                    val offset =
                                        listState.layoutInfo.visibleItemsInfo.first().index * elementHeight
                                    val scrollbarHeight =
                                        listState.layoutInfo.visibleItemsInfo.size * elementHeight
                                    drawRect(
                                        color = Color.Black.copy(alpha = 0.5f),
                                        topLeft = Offset(this.size.width - scrollBarWidth.toPx(), offset),
                                        size = Size(scrollBarWidth.toPx(), scrollbarHeight)
                                    )
                                }
                                .padding(end = scrollBarWidth)
                        ) {
                            val listOfPairs = orderMap.toList()
                            itemsIndexed(listOfPairs) { index, (key, value) ->
                                PickupItem(
                                    delId = key,
                                    quantity = value,
                                    from = "LenZ",
                                    to = "Shop",
                                    earning = eligibleOrders.filter { it.common_pickup_key == key }.first().delAmount,
                                    onCardClick = {},
                                    onAssignClick = {}
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}