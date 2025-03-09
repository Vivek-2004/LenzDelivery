package com.fitting.lenzdelivery.screens.component_holders

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import com.fitting.lenzdelivery.DeliveryViewModel
import com.fitting.lenzdelivery.R
import com.fitting.lenzdelivery.formDate
import com.fitting.lenzdelivery.screens.components.PaymentHistoryItem
import com.fitting.lenzdelivery.toIST
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentsHistory(
    deliveryViewModel: DeliveryViewModel
) {
    val riderState by deliveryViewModel.riderDetails.collectAsState()
    var updateHistory by remember { mutableStateOf(true) }
    var isRefreshing by remember { mutableStateOf(false) }
    LaunchedEffect(updateHistory) {
        if (!updateHistory) return@LaunchedEffect
        try {
            withContext(Dispatchers.IO) {
                riderState?.let { rider ->
                    deliveryViewModel.getRiderOrders()
                }
            }
        } finally {
            updateHistory = false
        }
    }


    val primaryGreen = Color("#38b000".toColorInt())
    val scrollBarWidth = 4.dp
    val listState = rememberLazyListState()
    val pullToRefreshState = rememberPullToRefreshState()

    if (isRefreshing) {
        LaunchedEffect(Unit) {
            updateHistory = true
            delay(2000L)
            isRefreshing = false
        }
    }

    if (deliveryViewModel.riderOrders.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            var showLoading by remember { mutableStateOf(true) }

            AnimatedVisibility(
                visible = showLoading,
                enter = fadeIn(),
                exit = fadeOut(animationSpec = tween(500))
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(80.dp),
                    strokeWidth = 6.dp,
                    color = primaryGreen
                )
            }

            LaunchedEffect(Unit) {
                delay(3000)
                showLoading = false
            }

            AnimatedVisibility(
                visible = !showLoading,
                enter = fadeIn(animationSpec = tween(500)) +
                        slideInVertically(animationSpec = tween(500)) { it / 2 }
            ) {
                Card(
                    modifier = Modifier
                        .padding(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.payment_history),
                            contentDescription = "No Payments",
                            tint = Color.Gray,
                            modifier = Modifier.size(56.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "No Previous Earnings",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.DarkGray
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Completed deliveries will appear here",
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

    } else {
        PullToRefreshBox(
            state = pullToRefreshState,
            isRefreshing = isRefreshing,
            onRefresh = {
                isRefreshing = true
            },
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Summary card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Payment Summary",
                            fontWeight = FontWeight.SemiBold,
                            color = Color.DarkGray
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        val totalPayments = deliveryViewModel.riderOrders
                            .filter { it.riderId == deliveryViewModel.riderObjectId && it.isCompleted }
                            .sumOf { it.paymentAmount }

                        Text(
                            text = "₹${totalPayments}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = primaryGreen
                        )

                        Text(
                            text = "Total Earnings",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }

                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 16.dp,
                        top = 8.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .drawBehind {
                            if (listState.layoutInfo.totalItemsCount > 0) {
                                val scrollableHeight = this.size.height
                                val scrollbarHeight =
                                    (listState.layoutInfo.visibleItemsInfo.size.toFloat() /
                                            listState.layoutInfo.totalItemsCount) * scrollableHeight
                                val scrollPosition = (listState.firstVisibleItemIndex.toFloat() /
                                        listState.layoutInfo.totalItemsCount) * (scrollableHeight - scrollbarHeight)

                                drawRoundRect(
                                    color = Color.Gray.copy(alpha = 0.3f),
                                    topLeft = Offset(
                                        this.size.width - scrollBarWidth.toPx(),
                                        scrollPosition
                                    ),
                                    size = Size(scrollBarWidth.toPx(), scrollbarHeight),
                                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(
                                        scrollBarWidth.toPx() / 2
                                    )
                                )
                            }
                        }
                ) {
                    itemsIndexed(
                        items = deliveryViewModel.riderOrders
                            .filter { it.riderId == deliveryViewModel.riderObjectId && it.isCompleted }
                            .reversed()
                    ) { index, item ->
                        PaymentHistoryItem(
                            index = index,
                            orderId = item.orderKey,
                            paymentAmount = item.paymentAmount,
                            date = item.createdAt.formDate(),
                            time = item.createdAt.toIST()
                        )
                    }
                }
            }
        }
    }
}