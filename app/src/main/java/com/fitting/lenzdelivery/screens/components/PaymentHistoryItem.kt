package com.fitting.lenzdelivery.screens.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import com.fitting.lenzdelivery.R

@Composable
fun PaymentHistoryItem(
    index: Int,
    orderId: String,
    paymentAmount: Double,
    date: String,
    time: String
) {
    val primaryGreen = Color("#38b000".toColorInt())

    val backgroundColor = if ((index) % 2 == 0) Color.LightGray else Color.White

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Payment icon with circle background
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(primaryGreen.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.payment_history),
                    contentDescription = "Payment",
                    tint = primaryGreen,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Content - Order ID, Date and Time
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "#$orderId",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "$date · $time",
                    fontSize = 10.5.sp,
                    color = Color.Gray,
                    fontStyle = FontStyle.Normal
                )
            }

            // Amount
            Text(
                text = "₹$paymentAmount",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = primaryGreen
            )
        }
    }
}