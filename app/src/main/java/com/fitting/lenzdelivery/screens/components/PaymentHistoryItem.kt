package com.fitting.lenzdelivery.screens.components

import android.graphics.Color.parseColor
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fitting.lenzdelivery.R

@Composable
fun PaymentHistoryItem(
    index: Int,
    orderId: String,
    paymentAmount: Double,
    date: String,
    time: String
) {
    val primaryGreen = Color(parseColor("#38b000"))
    var isHovered by remember { mutableStateOf(false) }

    val backgroundColor by animateColorAsState(
        targetValue = if (isHovered) Color(0xFFF1F8E9) else Color.White,
        animationSpec = tween(300),
        label = "bgColor"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
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

            Spacer(modifier = Modifier.width(16.dp))

            // Content - Order ID, Date and Time
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Order #$orderId",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.DarkGray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "$date · $time",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    fontStyle = FontStyle.Normal
                )
            }

            // Amount
            Text(
                text = "₹$paymentAmount",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = primaryGreen
            )
        }
    }
}