package com.fitting.lenzdelivery.screens.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fitting.lenzdelivery.screens.component_holders.SwipeToButton

@Composable
fun PickupItem(
    delId: String,
    quantity: Int,
    orderType: String,
    earning: Double,
    onCardClick: () -> Unit,
    onAssignSwipe: () -> Unit
) {
    val goldAccent = Color(0xFFD4AF37)
    val cream = Color(0xFFF5F5F0)
    val charcoal = Color(0xFF363636)
    val darkNavy = Color(0xFF1A2A3A)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        border = BorderStroke(1.dp, goldAccent.copy(alpha = 0.7f)),
        colors = CardDefaults.cardColors(
            containerColor = cream
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clickable { onCardClick() }
                .padding(20.dp)
        ) {
            // Elegant header with ID
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Order #$delId",
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Serif,
                    fontStyle = FontStyle.Italic,
                    color = charcoal,
                    letterSpacing = 1.sp
                )

                Divider(
                    modifier = Modifier
                        .width(80.dp)
                        .padding(horizontal = 8.dp),
                    color = goldAccent.copy(alpha = 0.5f),
                    thickness = 0.5.dp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main content in an elegant box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                Column {
                    // Deliveries count
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Total Drops",
                            fontSize = 15.sp,
                            fontFamily = FontFamily.Serif,
                            color = charcoal
                        )

                        Text(
                            text = quantity.toString(),
                            fontSize = 15.sp,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            color = darkNavy
                        )
                    }

                    Divider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = Color.LightGray.copy(alpha = 0.5f),
                        thickness = 0.5.dp
                    )

                    // Order type
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Category",
                            fontSize = 15.sp,
                            fontFamily = FontFamily.Serif,
                            color = charcoal
                        )

                        Text(
                            text = orderType,
                            fontSize = 15.sp,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            color = darkNavy
                        )
                    }

                    Divider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = Color.LightGray.copy(alpha = 0.5f),
                        thickness = 0.5.dp
                    )

                    // Earnings with elegant styling
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Estimated Earning",
                            fontSize = 15.sp,
                            fontFamily = FontFamily.Serif,
                            color = charcoal
                        )

                        Text(
                            text = "₹$earning/-",
                            fontSize = 15.sp,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            color = goldAccent
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onCardClick() },
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "View Details",
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Medium,
                    color = darkNavy,
                    letterSpacing = 0.5.sp
                )

                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "View Details",
                    tint = darkNavy,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Refined swipe button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(2.dp))
                    .background(cream)
            ) {
                SwipeToButton(
                    onSwipeComplete = {
                        onAssignSwipe()
                    }
                )
            }
        }
    }
}