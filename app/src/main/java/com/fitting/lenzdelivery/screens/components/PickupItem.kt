package com.fitting.lenzdelivery.screens.components

import android.graphics.Color.parseColor
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fitting.lenzdelivery.R
import com.fitting.lenzdelivery.screens.component_holders.SwipeToButton

@Composable
fun PickupItem(
    delId: String,
    quantity: Int,
    orderType: String,
    earning: Double,
    bgColor: Color,
    onCardClick: () -> Unit,
    onAssignSwipe: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 6.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(14.dp),
        border = BorderStroke(3.dp, Color.Black)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clickable { onCardClick() }
                .background(bgColor)
                .padding(horizontal = 21.dp, vertical = 12.dp)
        ) {
            Text(
                text = "#$delId",
                fontSize = 12.sp,
                fontStyle = FontStyle.Italic,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("No. Of Deliveries: ")
                        }
                        append(quantity.toString())
                    },
                    fontSize = 16.sp,
                    color = Color.Red.copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(R.drawable.touch_click),
                    contentDescription = "Click to View Details",
                    tint = Color.Magenta
                )
            }
            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("Order Type: ")
                    }
                    append(orderType)
                },
                fontSize = 16.sp,
                color = Color(parseColor("#8338ec"))
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("Estimated Earning: ")
                    }
                    append("₹$earning/-")
                },
                fontSize = 16.sp,
                color = Color(parseColor("#008000"))
            )

            Spacer(modifier = Modifier.height(12.dp))

            SwipeToButton(
                onSwipeComplete = {
                    onAssignSwipe()
                }
            )
        }
    }
}