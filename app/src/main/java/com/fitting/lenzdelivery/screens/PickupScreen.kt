package com.fitting.lenzdelivery.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardDoubleArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.fitting.lenzdelivery.DeliveryViewModel
import com.fitting.lenzdelivery.navigation.NavigationDestination
import com.fitting.lenzdelivery.screens.component_holders.details.TransitOrderDetails
import com.fitting.lenzdelivery.screens.components.PickupItem
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PickupScreen(
    deliveryViewModel: DeliveryViewModel,
    navController: NavController,
) {
    LaunchedEffect(Unit) {
        deliveryViewModel.getRiderOrders()
    }
    val scrollState = rememberScrollState()
    val listState = rememberLazyListState()
    val scrollBarWidth = 5.dp
    val pullToRefreshState = rememberPullToRefreshState()
    var isRefreshing by remember { mutableStateOf(false) }
    var isAssigned by remember { mutableStateOf(false) }

    val riderState by deliveryViewModel.riderDetails.collectAsState()
    var riderIsWorking by remember { mutableStateOf(false) }
    if (riderState != null) {
        riderState?.let { rider ->
            riderIsWorking = rider.isWorking
        }
    }

    val allUnassignedOrders = deliveryViewModel.riderOrders.filter {
        it.riderId == null
    }
    val riderOrders = deliveryViewModel.riderOrders.filter {
        it.riderId == deliveryViewModel.riderObjectId
    }
    val riderIncompleteOrder =
        riderOrders.firstOrNull { it.riderId == deliveryViewModel.riderObjectId && !it.isCompleted }

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
            .background(Color.Gray.copy(alpha = 0.6f))
    ) {
        if (!riderIsWorking) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .align(Alignment.Center),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Card(
                        modifier = Modifier
                            .padding(16.dp)
                            .shadow(8.dp, RoundedCornerShape(16.dp)),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 6.dp
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "PORT IN",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFD90429)
                            )
                            Text(
                                text = "to Assign Orders",
                                fontSize = 18.sp,
                                color = Color(0xFF2B2D42),
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    navController.navigate(NavigationDestination.Profile.name) {
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                    isRefreshing = true
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFD90429),
                                    contentColor = Color.White
                                ),
                                modifier = Modifier
                                    .height(48.dp)
                                    .fillMaxWidth(0.7f)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "Continue",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Icon(
                                        imageVector = Icons.Filled.KeyboardDoubleArrowRight,
                                        contentDescription = "Navigate to Profile Page"
                                    )
                                }
                            }
                        }
                    }
                }
            }
//            Box(
//                modifier = Modifier.fillMaxSize(),
//            ) {
//                Column(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .align(Alignment.Center),
//                    verticalArrangement = Arrangement.Center,
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    Text(
//                        text = "PORT IN to Assign Orders",
//                        fontSize = 20.sp,
//                        color = Color(0xFFD90429)
//                    )
//                    Spacer(modifier = Modifier.height(12.dp))
//                    Button(
//                        onClick = {
//                            navController.navigate(NavigationDestination.Profile.name) {
//                                popUpTo(navController.graph.startDestinationId) {
//                                    saveState = true
//                                }
//                                launchSingleTop = true
//                                restoreState = true
//                            }
//                            isRefreshing = true
//                        },
//                        shape = CircleShape,
//                        colors = ButtonDefaults.buttonColors(
//                            containerColor = Color.Black.copy(alpha = 0.9f),
//                            contentColor = Color.White
//                        )
//                    ) {
//                        Icon(
//                            imageVector = Icons.Filled.KeyboardDoubleArrowRight,
//                            contentDescription = "Navigate to Profile Page"
//                        )
//                    }
//                }
//            }
        } else if (riderIncompleteOrder == null) {
            if (allUnassignedOrders.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No Orders Available Now"
                    )
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
                            onCardClick = {
                                navController.navigate(NavigationDestination.PickupDetails.name + "/${item.orderKey}")
                            },
                            onAssignSwipe = {
                                if (item.deliveryType == "pickup") {
                                    deliveryViewModel.assignPickupRider(
                                        groupOrderId = item.groupOrderIds.first().groupOrderId
                                    )
                                } else {
                                    deliveryViewModel.assignDeliveryRider(
                                        pickupKey = item.orderKey
                                    )
                                }
                                isAssigned = true
                            }
                        )
                    }
                }
            }
        } else {
            TransitOrderDetails(
                riderOrder = riderIncompleteOrder,
                deliveryViewModel = deliveryViewModel
            )
        }
    }
}