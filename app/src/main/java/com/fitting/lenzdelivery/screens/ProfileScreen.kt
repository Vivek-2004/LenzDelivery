package com.fitting.lenzdelivery.screens

import android.content.Intent
import android.widget.Toast
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.core.net.toUri
import com.fitting.lenzdelivery.DeliveryViewModel
import com.fitting.lenzdelivery.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@Composable
fun ProfileScreen(
    deliveryViewModel: DeliveryViewModel
) {
    deliveryViewModel.getRiderDetails()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var errorMessage by remember { mutableStateOf("") }
    var showEditDialog by remember { mutableStateOf(false) }
    var updatePhone by remember { mutableStateOf(false) }
    var updateRider by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var phoneNumber by remember { mutableStateOf("") }

    val riderState by deliveryViewModel.riderDetails.collectAsState()
    val primaryColor = Color("#38b000".toColorInt())

    LaunchedEffect(riderState) {
        riderState?.phone?.let { phoneNumber = it }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        when {
            riderState == null -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center), color = primaryColor
                    )
                }
            }

            else -> {
                riderState?.let { rider ->
                    val buttonContainerColor = when {
                        !rider.isWorking && rider.isAvailable -> primaryColor
                        rider.isWorking && !rider.isAvailable -> Color.Gray
                        else -> Color.Red
                    }

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
                                    context, "Phone number updated", Toast.LENGTH_SHORT
                                ).show()
                            } catch (e: Exception) {
                                Toast.makeText(
                                    context, "Update failed: ${e.message}", Toast.LENGTH_SHORT
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
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp),
                            onClick = {
                                if (!isLoading) {
                                    isLoading = true
                                    updateRider = true
                                }
                                if (rider.isWorking && !rider.isAvailable) {
                                    Toast.makeText(
                                        context, "Complete Drops to Port Out", Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            enabled = !isLoading,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = buttonContainerColor, contentColor = Color.White
                            ),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 6.dp, pressedElevation = 2.dp
                            )
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    color = Color.Black, strokeWidth = 5.dp
                                )
                            } else {
                                Text(
                                    text = if (rider.isWorking) "PORT OUT" else "PORT IN",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 26.sp,
                                    letterSpacing = 1.sp
                                )
                            }
                        }

                        ElevatedCard(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = "Rider Details",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = primaryColor
                                )

                                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.7f))

                                InfoRow(label = "Rider ID", value = rider.riderId)
                                InfoRow(label = "Name", value = rider.name)

                                InfoRow(
                                    label = "Phone", value = "+91 $phoneNumber", trailingContent = {
                                        IconButton(
                                            onClick = { showEditDialog = true },
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(CircleShape)
                                                .background(primaryColor.copy(alpha = 0.1f))
                                        ) {
                                            Icon(
                                                imageVector = Icons.Rounded.Edit,
                                                contentDescription = "Edit Phone Number",
                                                tint = primaryColor
                                            )
                                        }
                                    })

                                InfoRow(label = "Email", value = rider.email)
                                InfoRow(label = "Vehicle Number", value = rider.vehicleNumber)

                                StatusIndicator(
                                    isWorking = rider.isWorking, isAvailable = rider.isAvailable
                                )
                            }
                        }

                        ContactCard()
                    }
                }
            }
        }

        if (showEditDialog) {
            AlertDialog(onDismissRequest = {
                showEditDialog = false
                errorMessage = ""
            }, title = {
                Text(
                    text = "Edit Contact Number", fontSize = 20.sp, fontWeight = FontWeight.Bold
                )
            }, text = {
                Column {
                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { phoneNumber = it },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Enter New Number") },
                        label = { Text("+91") },
                        isError = errorMessage.isNotEmpty()
                    )

                    if (errorMessage.isNotEmpty()) {
                        Text(
                            text = errorMessage,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }, confirmButton = {
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
                    Text("Update", fontSize = 16.sp, color = primaryColor)
                }
            }, dismissButton = {
                TextButton(onClick = {
                    errorMessage = ""
                    showEditDialog = false
                }) {
                    Text("Cancel", fontSize = 16.sp)
                }
            })
        }
    }
}

@Composable
fun InfoRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    trailingContent: @Composable (() -> Unit)? = null
) {
    val labelColor = MaterialTheme.colorScheme.primary
    val valueColor = MaterialTheme.colorScheme.onSurface
    val backgroundColor = MaterialTheme.colorScheme.surfaceVariant
    val accentColor = MaterialTheme.colorScheme.tertiary

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(36.dp)
                    .background(
                        color = accentColor, shape = RoundedCornerShape(8.dp)
                    )
            )

            Column(
                modifier = Modifier
                    .padding(start = 12.dp)
                    .weight(1f)
            ) {
                Text(
                    text = label,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = labelColor,
                    letterSpacing = 0.5.sp,
                    style = TextStyle(
                        shadow = Shadow(
                            color = Color.Black.copy(alpha = 0.1f),
                            offset = Offset(1f, 1f),
                            blurRadius = 1f
                        )
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = value,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = valueColor,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            }

            // Add trailing content if provided
            trailingContent?.invoke()
        }
    }
}

@Composable
private fun StatusIndicator(isWorking: Boolean, isAvailable: Boolean) {
    val statusText = when {
        isWorking && isAvailable -> "Active & Available"
        isWorking && !isAvailable -> "Active but Busy"
        !isWorking -> "Not Active"
        else -> "Unknown Status"
    }

    val statusColor = when {
        isWorking && isAvailable -> Color("#38b000".toColorInt())
        isWorking && !isAvailable -> Color("#FFA500".toColorInt())
        !isWorking -> Color.Gray
        else -> Color.Red
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(statusColor.copy(alpha = 0.1f))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(statusColor)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = statusText, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = statusColor
        )
    }
}

@Composable
private fun ContactCard() {
    val context = LocalContext.current
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Contact Us",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = Color.LightGray.copy(alpha = 0.7f)
            )

            ContactMethod(
                icon = Icons.Default.Call, text = "+91 8967310388", onClick = {
                    val intent = Intent(Intent.ACTION_DIAL).apply {
                        data = "tel:+918967310388".toUri()
                    }
                    context.startActivity(intent)
                })

            ContactMethod(
                icon = painterResource(R.drawable.whatsapp), text = "+91 8967310388", onClick = {
                    val intent = Intent(
                        Intent.ACTION_VIEW, "https://wa.me/918967310388".toUri()
                    )
                    context.startActivity(intent)
                })

            ContactMethod(
                icon = Icons.Default.Email, text = "connect.lenz@gmail.com", onClick = {
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = "mailto:connect.lenz@gmail.com".toUri()
                    }
                    context.startActivity(intent)
                })
        }
    }
}

@Composable
private fun ContactMethod(
    icon: Any, text: String, onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        when (icon) {
            is ImageVector -> {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color("#38b000".toColorInt()),
                    modifier = Modifier.size(24.dp)
                )
            }

            is androidx.compose.ui.graphics.painter.Painter -> {
                Icon(
                    painter = icon,
                    contentDescription = null,
                    tint = Color("#38b000".toColorInt()),
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
    }
}