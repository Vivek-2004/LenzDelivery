package com.fitting.lenzdelivery.screens

import android.graphics.Color.parseColor
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EarningsScreen(
    deliveryViewModel: DeliveryViewModel,
    navController: NavController
) {
    val riderState by deliveryViewModel.riderDetails.collectAsState()
    val pullToRefreshState = rememberPullToRefreshState()
    var isRefreshing by remember { mutableStateOf(false) }

    val primaryGreen = Color(parseColor("#38b000"))
    val cardBackground = MaterialTheme.colorScheme.surface

    Box(modifier = Modifier.fillMaxWidth()) {
        when {
            riderState == null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = primaryGreen,
                        strokeWidth = 4.dp
                    )
                }
            }

            else -> {
                riderState?.let { rider ->
                    LaunchedEffect(isRefreshing) {
                        if (!isRefreshing) return@LaunchedEffect
                        try {
                            deliveryViewModel.getRiderDetails()
                            delay(1500L)
                        } finally {
                            isRefreshing = false
                        }
                    }

                    PullToRefreshBox(
                        state = pullToRefreshState,
                        isRefreshing = isRefreshing,
                        onRefresh = {
                            isRefreshing = true
                        },
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(start = 16.dp, end = 16.dp, bottom = 25.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            ValueInfoCard(
                                title = "Total Orders",
                                value = rider.totalOrders.toString(),
                                dailyValue = rider.dailyOrders,
                                dailyValuePrefix = "",
                                valueSuffix = "",
                                primaryColor = primaryGreen,
                                cardBackground = cardBackground
                            )
                            ValueInfoCard(
                                title = "Total Income",
                                value = rider.totalEarnings.toString(),
                                dailyValue = rider.dailyEarnings.toInt(),
                                dailyValuePrefix = "₹",
                                valueSuffix = "₹",
                                primaryColor = primaryGreen,
                                cardBackground = cardBackground
                            )
                        }
                    }
                }
            }
        }
        FloatingActionButton(
            onClick = {
                navController.navigate(NavigationDestination.PaymentsHistory.name)
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(28.dp),
            containerColor = primaryGreen,
            contentColor = Color.White
        ) {
            Icon(
                painter = painterResource(R.drawable.payment_history),
                contentDescription = "Payment History"
            )
        }
    }
}

@Composable
fun ValueInfoCard(
    title: String,
    value: String,
    dailyValue: Int,
    dailyValuePrefix: String,
    valueSuffix: String,
    primaryColor: Color,
    cardBackground: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = cardBackground),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (valueSuffix.isNotEmpty()) {
                    Text(
                        text = valueSuffix,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryColor
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                }

                Text(
                    text = value,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = primaryColor
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            AnimatedVisibility(
                visible = dailyValue > 0,
                enter = fadeIn(animationSpec = tween(300)),
                exit = fadeOut(animationSpec = tween(300))
            ) {
                Row(
                    modifier = Modifier
                        .wrapContentWidth()
                        .background(
                            color = primaryColor.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(24.dp)
                        )
                        .clip(RoundedCornerShape(24.dp))
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$dailyValuePrefix$dailyValue",
                        fontSize = this@AnimatedVisibility.run { if (dailyValuePrefix.isNotEmpty()) 16.sp else 18.sp },
                        fontWeight = FontWeight.Bold,
                        color = primaryColor
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(primaryColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            modifier = Modifier.size(16.dp),
                            painter = painterResource(R.drawable.arrow_up),
                            contentDescription = "Increment",
                            tint = Color.White
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = dailyValue <= 0,
                enter = fadeIn(animationSpec = tween(300)),
                exit = fadeOut(animationSpec = tween(300))
            ) {
                Text(
                    text = if (title.contains("Orders")) "No Orders Today" else "No Earnings Today",
                    fontSize = (16.sp),
                    color = Color.Gray,
                    modifier = Modifier
                        .background(
                            color = Color.LightGray.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}