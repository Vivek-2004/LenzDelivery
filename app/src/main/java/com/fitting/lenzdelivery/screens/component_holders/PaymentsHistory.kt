package com.fitting.lenzdelivery.screens.component_holders

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fitting.lenzdelivery.DeliveryViewModel
import com.fitting.lenzdelivery.formDate
import com.fitting.lenzdelivery.screens.components.PaymentHistoryItem
import com.fitting.lenzdelivery.toIST
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@RequiresApi(Build.VERSION_CODES.O)
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
    val scrollBarWidth = 5.dp
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
            if (showLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(80.dp),
                    strokeWidth = 8.dp,
                    color = Color.Black.copy(alpha = 0.8f)
                )

                LaunchedEffect(Unit) {
                    delay(3000)
                    showLoading = false
                }
            } else {
                Text(
                    text = "No Previous Earnings",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    } else {
        PullToRefreshBox(
            state = pullToRefreshState,
            isRefreshing = isRefreshing,
            onRefresh = {
                isRefreshing = true
            },
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White.copy(alpha = 0.1f))
        ) {
            LazyColumn(
                state = listState,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 50.dp)
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
                itemsIndexed(deliveryViewModel.riderOrders.reversed()) { index, item ->
                    if (item.riderId == deliveryViewModel.riderObjectId && item.isCompleted) {
                        PaymentHistoryItem(
                            index = index,
                            orderId = item.orderKey,
                            paymentAmount = item.paymentAmount,
                            date = item.createdAt.formDate(),
                            time = item.createdAt.toIST()
                        )
                        HorizontalDivider(
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}