package com.fitting.lenzdelivery.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.fitting.lenzdelivery.DeliveryViewModel
import com.fitting.lenzdelivery.navigation.NavigationDestination
import com.fitting.lenzdelivery.screens.component_holders.Details.TransitOrderDetails
import com.fitting.lenzdelivery.screens.components.PickupItem
import kotlinx.coroutines.delay

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PickupScreen(
    deliveryViewModel: DeliveryViewModel = viewModel(),
    navController: NavController,
) {
    val listState = rememberLazyListState()
    val scrollBarWidth = 5.dp
    val pullToRefreshState = rememberPullToRefreshState()
    var isRefreshing by remember { mutableStateOf(false) }
    var isAssigned by remember { mutableStateOf(false) }

    val allUnassignedOrders = deliveryViewModel.riderOrders.filter {
        it.riderId == null
    }
    val riderOrders = deliveryViewModel.riderOrders.filter {
        it.riderId == deliveryViewModel.riderObjectId
    }

    val incompleteOrder =
        riderOrders.firstOrNull { it.riderId == deliveryViewModel.riderObjectId && !it.isCompleted }
    println(incompleteOrder.toString() + "gosh")

    LaunchedEffect(isRefreshing) {
        if (!isRefreshing) return@LaunchedEffect
        try {
            deliveryViewModel.getRiderOrders()
            delay(1500L)
        } finally {
            isRefreshing = false
        }
    }

    LaunchedEffect(isAssigned) {
        if (!isAssigned) return@LaunchedEffect
        try {
            delay(1500L)
            isRefreshing = true
        } finally {
            isAssigned = false
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
        if (incompleteOrder == null) {
            if (allUnassignedOrders == null) {
                Column {
                    Text("VIVEK")
                }
            } else {
                LazyColumn(
                    state = listState,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxSize()
                        .drawBehind {
                            val elementHeight = size.height / listState.layoutInfo.totalItemsCount
                            val offset = listState.firstVisibleItemIndex * elementHeight
                            val scrollbarHeight =
                                listState.layoutInfo.visibleItemsInfo.size * elementHeight
                            drawRect(
                                color = Color.Black.copy(alpha = 0.5f),
                                topLeft = Offset(size.width - scrollBarWidth.toPx(), offset),
                                size = Size(scrollBarWidth.toPx(), scrollbarHeight)
                            )
                        }
                        .padding(end = scrollBarWidth)
                ) {
                    itemsIndexed(allUnassignedOrders.reversed()) { index, item ->
                        PickupItem(
                            delId = item.orderKey,
                            quantity = item.groupOrderIds.size,
                            orderType = when (item.deliveryType) {
                                "pickup" -> "Shop Pickup"
                                else -> "LenZ Pickup"
                            },
                            earning = item.paymentAmount,
                            bgColor = Color.White,
                            onCardClick = {
                                navController.navigate(NavigationDestination.PickupDetails.name + "/${item.orderKey}")
                            },
                            onAssignSwipe = {
                                deliveryViewModel.selfAssignRider(
                                    groupOrderId = item.groupOrderIds.first(),
                                    pickupRiderId = deliveryViewModel.riderObjectId
                                )
                                isAssigned = true
                            }
                        )
                    }
                }
            }
        } else {

            TransitOrderDetails(
                order = incompleteOrder,
                deliveryViewModel = deliveryViewModel
            )
        }
    }
}