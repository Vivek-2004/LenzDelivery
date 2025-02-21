package com.fitting.lenzdelivery.screens.components

import android.graphics.Color.parseColor
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PaymentHistoryItem(
    index: Int,
    orderId: String,
    paymentAmount: Double
) {
    val indexColor = if(index % 2 == 0) Color.LightGray.copy(alpha = 0.8f)
                    else Color.White
    Column(
        modifier = Modifier.fillMaxWidth()
            .height(105.dp)
            .background(indexColor)
            .padding(top = 13.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.width(25.dp))
            Text(
                text = "#$orderId",
                fontSize = 13.5.sp,
                color = Color.Black.copy(alpha = 0.8f),
                fontStyle = FontStyle.Italic
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth().weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Text(
                text = "02:34 p.m.",
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray
            )
            Text(
                text = "+ ₹${paymentAmount}/-",
                fontWeight = FontWeight.Bold,
                color = Color(parseColor("#38b000"))
            )
        }
    }
}