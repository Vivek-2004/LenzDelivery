package com.fitting.lenzdelivery.screens

import android.graphics.Color.parseColor
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.fitting.lenzdelivery.DeliveryViewModel
import com.fitting.lenzdelivery.R
import com.fitting.lenzdelivery.navigation.NavigationDestination

@Composable
fun EarningsScreen(
    deliveryViewModel: DeliveryViewModel,
    navController: NavController
) {
    val rider = deliveryViewModel.allRiders.first()
    Column(
        modifier = Modifier.fillMaxSize().padding(bottom = 55.dp),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.fillMaxSize().weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Total Orders",
                fontSize = 34.sp,
                fontWeight = FontWeight.Black,
                color = Color.DarkGray
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = rider.totalOrders.toString(),
                fontSize = 50.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(parseColor("#38b000"))
            )

            if (rider.dailyOrders > 0) {
                Row(
                    modifier = Modifier.width(60.dp)
                        .background(Color.Green.copy(alpha = 0.35f), shape = RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp)),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = rider.dailyOrders.toString(),
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        modifier = Modifier.size(15.dp),
                        painter = painterResource(R.drawable.arrow_up),
                        contentDescription = "Orders Increment",
                    )
                }
            }
        }
        HorizontalDivider(thickness = 2.dp)
        Column(
            modifier = Modifier.fillMaxSize().weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Total Income",
                fontSize = 34.sp,
                fontWeight = FontWeight.Black,
                color = Color.DarkGray
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "₹${rider.totalEarnings}",
                fontSize = 50.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(parseColor("#38b000"))
            )
            if (rider.dailyEarnings > 0.0) {
                Row(
                    modifier = Modifier.width(120.dp)
                        .background(Color.Green.copy(alpha = 0.35f), shape = RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp)),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "₹${rider.dailyEarnings}",
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        modifier = Modifier.size(15.dp),
                        painter = painterResource(R.drawable.arrow_up),
                        contentDescription = "Orders Increment",
                    )
                }
            }
        }
    }
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        FloatingActionButton(
            onClick = {
                navController.navigate(NavigationDestination.PaymentsHistory.name)
            },
            modifier = Modifier.align(Alignment.BottomEnd).padding(28.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.payment_history),
                contentDescription = "Payment History"
            )
        }
    }
}