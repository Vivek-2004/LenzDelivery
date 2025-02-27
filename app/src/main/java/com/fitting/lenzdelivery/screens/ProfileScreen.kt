package com.fitting.lenzdelivery.screens

import android.content.Intent
import android.graphics.Color.parseColor
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fitting.lenzdelivery.DeliveryViewModel
import com.fitting.lenzdelivery.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@Composable
fun ProfileScreen(deliveryViewModel: DeliveryViewModel) {
    val context = LocalContext.current
    var errorMessage by remember { mutableStateOf("") }
    var showEditDialog by remember { mutableStateOf(false) }
    var updatePhone by remember { mutableStateOf(false) }
    var updateRider by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var phoneNumber by remember { mutableStateOf("") }

    val riderState by deliveryViewModel.riderDetails.collectAsState()

    LaunchedEffect(riderState) {
        riderState?.phone?.let { phoneNumber = it }
    }

    LaunchedEffect(Unit) {
        deliveryViewModel.getRiderDetails()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            riderState == null -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            else -> {
                riderState?.let { rider ->
                    val buttonContainerColor = if (!rider.isWorking && rider.isAvailable) Color(
                        parseColor("#38b000")
                    )
                    else if (rider.isWorking && !rider.isAvailable) Color.Gray
                    else Color.Red
                    LaunchedEffect(updatePhone) {
                        if (updatePhone) {
                            try {
                                withContext(Dispatchers.IO) {
                                    deliveryViewModel.editRiderContact(
                                        riderId = rider.riderId.toInt(),
                                        newPhoneNumber = phoneNumber
                                    )
                                }
                                Toast.makeText(
                                    context,
                                    "Phone number updated",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } catch (e: Exception) {
                                Toast.makeText(
                                    context,
                                    "Update failed: ${e.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } finally {
                                updatePhone = false
                                showEditDialog = false
                                deliveryViewModel.getRiderDetails()
                            }
                        }
                    }

                    LaunchedEffect(updateRider) {
                        if (updateRider) {
                            try {
                                withContext(Dispatchers.IO) {
                                    deliveryViewModel.editRiderWorkingStatus(
                                        riderId = rider.riderId.toInt(),
                                        newStatus = !rider.isWorking
                                    )
                                }
                                delay(1500)
                                deliveryViewModel.getRiderDetails()
                            } catch (e: Exception) {
                                Toast.makeText(
                                    context,
                                    "Status update failed: ${e.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } finally {
                                updateRider = false
                                isLoading = false
                            }
                        }
                    }

                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(13.dp))
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(horizontal = 13.dp, vertical = 4.dp),
                            elevation = CardDefaults.cardElevation(16.dp),
                            border = BorderStroke(
                                3.dp,
                                Color(parseColor("#38b000")).copy(alpha = 0.4f)
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.LightGray)
                                    .padding(vertical = 6.dp, horizontal = 14.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Rider Details",
                                        fontSize = 30.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = buildAnnotatedString {
                                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                            append("Rider Id: ")
                                        }
                                        append(rider.riderId)
                                    },
                                    fontSize = 16.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = buildAnnotatedString {
                                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                            append("Rider Name: ")
                                        }
                                        append(rider.name)
                                    },
                                    fontSize = 16.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = buildAnnotatedString {
                                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                                append("Phone: ")
                                            }
                                            append("+91 $phoneNumber")
                                        },
                                        fontSize = 16.sp
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Row(
                                        modifier = Modifier
                                            .wrapContentSize()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color.Gray)
                                            .clickable { showEditDialog = true },
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            modifier = Modifier
                                                .size(26.dp)
                                                .padding(3.dp),
                                            painter = painterResource(R.drawable.edit),
                                            contentDescription = "Edit Phone Number",
                                            tint = Color.Black
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = buildAnnotatedString {
                                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                            append("Mail: ")
                                        }
                                        append(rider.email)
                                    },
                                    fontSize = 16.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = buildAnnotatedString {
                                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                            append("Vehicle Number: ")
                                        }
                                        append(rider.vehicleNumber)
                                    },
                                    fontSize = 16.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(75.dp)
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                            onClick = {
                                if (!isLoading) {
                                    isLoading = true
                                    updateRider = true
                                }
                                if (rider.isWorking && !rider.isAvailable) {
                                    Toast.makeText(
                                        context,
                                        "Complete Drops to Port Out",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            enabled = !isLoading,
                            shape = RoundedCornerShape(12.dp),

                            colors = ButtonDefaults.buttonColors(
                                containerColor = buttonContainerColor,
                                contentColor = Color.Black
                            )
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(color = Color.Black)
                            } else {
                                Column(

                                ) {
                                    Text(
                                        text = if (rider.isWorking) "PORT OUT" else "PORT IN",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 25.sp,
                                        letterSpacing = 1.2.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(start = 13.dp, end = 13.dp, bottom = 6.dp, top = 4.dp),
                            elevation = CardDefaults.cardElevation(16.dp),
                            border = BorderStroke(3.dp, Color.Black)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.LightGray)
                                    .padding(vertical = 6.dp, horizontal = 14.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "Contact Us",
                                    fontSize = 34.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconButton(
                                        onClick = {
                                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                                data = Uri.parse("tel:+917496450124")
                                            }
                                            context.startActivity(intent)
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Call,
                                            contentDescription = "Call LenZ"
                                        )
                                    }
                                    Text(
                                        text = "+91 7496450124",
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconButton(
                                        onClick = {
                                            val intent = Intent(
                                                Intent.ACTION_VIEW,
                                                Uri.parse("https://wa.me/917496450124")
                                            )
                                            context.startActivity(intent)
                                        }
                                    ) {
                                        Icon(
                                            modifier = Modifier.size(24.dp),
                                            painter = painterResource(R.drawable.whatsapp),
                                            contentDescription = "WhatsApp LenZ"
                                        )
                                    }
                                    Text(
                                        text = "+91 7496450124",
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconButton(
                                        onClick = {
                                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                                data = Uri.parse("mailto:service.lenz@gmail.com")
                                            }
                                            context.startActivity(intent)
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Email,
                                            contentDescription = "Mail LenZ"
                                        )
                                    }
                                    Text(
                                        text = "service.lenz@gmail.com",
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showEditDialog) {
            AlertDialog(
                onDismissRequest = {
                    showEditDialog = false
                    errorMessage = ""
                },
                text = {
                    Column {
                        Text(
                            text = "Edit Contact Number",
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = phoneNumber,
                            onValueChange = { phoneNumber = it },
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Number
                            ),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Enter New Number", fontSize = 16.sp) },
                            label = { Text("+91") }
                        )
                        if (errorMessage.isNotEmpty()) {
                            Text(
                                text = errorMessage,
                                fontSize = 12.sp,
                                color = Color.Red
                            )
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        when {
                            phoneNumber.length != 10 -> {
                                errorMessage = "Phone Number must be 10 Digits"
                            }

                            phoneNumber == riderState?.phone -> {
                                errorMessage = "No changes detected"
                            }

                            else -> {
                                updatePhone = true
                                errorMessage = ""
                            }
                        }
                    }) {
                        Text("Update", fontSize = 14.5.sp)
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        errorMessage = ""
                        showEditDialog = false
                    }) {
                        Text("Cancel", fontSize = 14.5.sp)
                    }
                }
            )
        }
    }
}