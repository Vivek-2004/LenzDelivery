package com.fitting.lenzdelivery.screens.components

import android.graphics.Color.parseColor
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import com.fitting.lenzdelivery.screens.component_holders.SwipeButton
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun PickupItem(
    delId: String,
    quantity: Int,
    from: String,
    to: String,
    earning: Double,
    onCardClick: () -> Unit,
    onAssignClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(14.dp),
        border = BorderStroke(3.dp, Color.Black)
    ) {
        Column(
            modifier = Modifier.clickable {
                onCardClick()
            }
                .background(Color.White)
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
                        append("Pick From: ")
                    }
                    append(from)
                },
                fontSize = 16.sp,
                color = Color(parseColor("#8338ec"))
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("Deliver To: ")
                    }
                    append(to)
                },
                fontSize = 16.sp,
                color = Color(parseColor("#1e6091"))
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

            val coroutineScope = rememberCoroutineScope()
            var isComplete by remember { mutableStateOf(false) }

            Spacer(modifier = Modifier.height(12.dp))
            SwipeButton(
                text = "Assign Self",
                isComplete = isComplete,
                onSwipe = {
                    onAssignClick()
                    coroutineScope.launch {
                        delay(2000)
                        isComplete = true
                    }
                }
            )
        }


//        Row(
//            modifier = Modifier.fillMaxWidth()
//                .height(60.dp)
//                .background(Color.White)
//                .padding(horizontal = 4.dp),
//            horizontalArrangement = Arrangement.Center,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            val coroutineScope = rememberCoroutineScope()
//            var isComplete by remember { mutableStateOf(false) }
//            SwipeButton(
//                text = "Assign Self",
//                isComplete = isComplete,
//                onSwipe = {
//                    onAssignClick()
//                    coroutineScope.launch {
//                        delay(2000)
//                        isComplete = true
//                    }
//                }
//            )
//        }
    }
}