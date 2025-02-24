package com.fitting.lenzdelivery.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.fitting.lenzdelivery.navigation.NavigationDestination
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
    val pullToRefreshState = rememberPullToRefreshState()
    var isRefreshing by remember { mutableStateOf(false) }

    val eligibleOrders = deliveryViewModel.riderOrders.filter {
        it.riderId == null
    }
    println(eligibleOrders)

    LaunchedEffect(isRefreshing) {
        if (!isRefreshing) return@LaunchedEffect
        try {
            deliveryViewModel.getRiderOrders()
            delay(1500L)
        } finally {
            isRefreshing = false
        }
    }

    PullToRefreshBox(
        state = pullToRefreshState,
        isRefreshing = isRefreshing,
        onRefresh = { isRefreshing = true },
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
                    val elementHeight = size.height / listState.layoutInfo.totalItemsCount
                    val offset = listState.firstVisibleItemIndex * elementHeight
                    val scrollbarHeight = listState.layoutInfo.visibleItemsInfo.size * elementHeight
                    drawRect(
                        color = Color.Black.copy(alpha = 0.5f),
                        topLeft = Offset(size.width - scrollBarWidth.toPx(), offset),
                        size = Size(scrollBarWidth.toPx(), scrollbarHeight)
                    )
                }
                .padding(end = scrollBarWidth)
        ) {
            itemsIndexed(eligibleOrders.reversed()) { index, item ->
                PickupItem(
                    delId = item.orderKey,
                    quantity = item.groupOrderIds.size,
                    orderType = when (item.deliveryType) {
                        "pickup" -> "Shop Pickup"
                        else -> "LenZ Pickup"
                    },
                    earning = item.paymentAmount,
                    onCardClick = {
                        navController.navigate(NavigationDestination.PickupDetails.name + "/${item.orderKey}")
                    },
                    onAssignSwipe = {}
                )
            }
        }
    }
}