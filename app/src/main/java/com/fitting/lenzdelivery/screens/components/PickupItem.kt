package com.fitting.lenzdelivery.screens.components

import android.graphics.Color.parseColor
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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

@Composable
fun PickupItem(
    onCardClick: () -> Unit,
    onAssignClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(14.dp),
        border = BorderStroke(3.dp, Color.Blue.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier.clickable {
                onCardClick()
            }
                .background(Color.White)
                .padding(top = 16.dp, bottom = 10.dp, start = 16.dp, end = 16.dp)
        ) {
            Text(
                text = "#16b33700-7417-46e8-8fca-08ab2239dc47",
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
                        append("3")
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
                    append("LenZ")
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
                    append("Station Bus Stand, Durgapur")
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
                    append("₹43.09/-")
                },
                fontSize = 16.sp,
                color = Color(parseColor("#008000"))
            )
        }
        HorizontalDivider(color = Color.Black)
        Row(
            modifier = Modifier.fillMaxWidth()
                .height(40.dp)
                .clickable {
                    onAssignClick()
                }
                .background(Color.LightGray),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.padding(4.dp),
                text = "Assign Self",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color(parseColor("#A020F0"))
            )
        }
    }
}