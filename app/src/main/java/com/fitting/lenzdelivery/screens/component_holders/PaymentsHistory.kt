package com.fitting.lenzdelivery.screens.component_holders

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fitting.lenzdelivery.DeliveryViewModel
import com.fitting.lenzdelivery.screens.components.PaymentHistoryItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@Composable
fun PaymentsHistory(
    deliveryViewModel: DeliveryViewModel
) {
    val rider = deliveryViewModel.allRiders.first()
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            deliveryViewModel.getRiderEarningHistory(
                riderId = rider._id
            )
        }
    }
    val listState = rememberLazyListState()
    val scrollBarWidth = 5.dp

    if (deliveryViewModel.earningHistory.isEmpty()) {
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
            itemsIndexed(deliveryViewModel.earningHistory.reversed()) { index, item ->
                PaymentHistoryItem(
                    index = index,
                    orderId = item.orderKey,
                    paymentAmount = item.paymentAmount
                )
                HorizontalDivider(
                    color = Color.Black
                )
            }
        }
    }
}