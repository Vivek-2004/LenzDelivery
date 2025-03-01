package com.fitting.lenzdelivery.screens.component_holders.details

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.GifBox
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.filled.PinDrop
import androidx.compose.material.icons.filled.Support
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fitting.lenzdelivery.DeliveryViewModel
import com.fitting.lenzdelivery.models.GroupedOrders
import com.fitting.lenzdelivery.models.RiderOrder
import com.fitting.lenzdelivery.models.ShopAddress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TransitOrderDetails(
    order: RiderOrder,
    deliveryViewModel: DeliveryViewModel
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val dateFormatter = DateTimeFormatter
        .ofPattern("MMM dd, yyyy • hh:mm a")
        .withZone(ZoneId.systemDefault())

    val createdAtInstant = Instant.parse(order.createdAt)
    val formattedDate = dateFormatter.format(createdAtInstant)
    val scrollState = rememberScrollState()

    var showOtpDialog by remember { mutableStateOf(false) }
    var enteredOtp by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var verifyOtp by remember { mutableStateOf(false) }
    var otpVerifyToast by remember { mutableStateOf("") }
    var currentGroupOrderId by remember { mutableStateOf("") }

    LaunchedEffect(verifyOtp) {
        if (!verifyOtp) return@LaunchedEffect
        try {
            if (order.deliveryType == "pickup") {
                if (!order.isPickupVerified) { // Shop Pickup
                    otpVerifyToast = deliveryViewModel.verifyPickupOtp(
                        groupOrderId = order.groupOrderIds.first(),
                        otpCode = enteredOtp
                    )
                } else { // Admin Drop
                    otpVerifyToast = deliveryViewModel.verifyAdminOtp(
                        groupOrderId = order.groupOrderIds.first(),
                        otpCode = enteredOtp
                    )
                }
            }
            if (order.deliveryType == "delivery") {
                if(!order.isPickupVerified) { // Admin Pickup
                    otpVerifyToast = deliveryViewModel.verifyAdminPickupOtp(
                        orderKey = order.orderKey,
                        otpCode = enteredOtp
                    )
                } else { // Admin Drop
                    if (currentGroupOrderId.isNotEmpty()) {
                        otpVerifyToast = deliveryViewModel.verifyShopDropOtp(
                            groupOrderId = currentGroupOrderId,
                            otpCode = enteredOtp
                        )
                    }
                }
            }
            delay(1500)
        } finally {
            deliveryViewModel.getRiderOrders()
            verifyOtp = false
            enteredOtp = ""
            currentGroupOrderId = ""
        }
    }

    if (otpVerifyToast.isNotEmpty()) {
        Toast.makeText(context, otpVerifyToast, Toast.LENGTH_SHORT).show()
        otpVerifyToast = ""
    }

    // OTP Dialog
    if (showOtpDialog) {
        OtpVerificationDialog(
            onVerify = {
                showOtpDialog = false
                errorMessage = ""
                verifyOtp = true
            },
            onDismiss = {
                showOtpDialog = false
                errorMessage = ""
                enteredOtp = ""
            },
            tempOtp = enteredOtp,
            onOtpChange = { enteredOtp = it },
            errorMessage = errorMessage
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
    ) {
        // Status Header - Enhanced with animated gradients for visual appeal
        OrderStatusHeader(
            isCompleted = order.isCompleted,
            formattedDate = formattedDate
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Order Info Cards - Using a consistent card style
        OrderInfoCards(
            deliveryType = order.deliveryType,
            paymentAmount = order.paymentAmount.toString()
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Location Details - Enhanced with Maps integration hint
        LocationDetails(order = order)

        // Group Orders - Enhanced with better visual hierarchy
        if(order.isPickupVerified && order.isDropVerified) {
            Card(
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.LightGray.copy(alpha = 0.6f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(text = "No more Drops • Complete Transit", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        } else {
            if (order.deliveryType == "delivery" && !order.isPickupVerified) {
                Button(
                    onClick = { showOtpDialog = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Pin,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Pickup OTP",
                        style = MaterialTheme.typography.labelMedium,
                        fontSize = 18.sp
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(16.dp))
                GroupOrderSection(
                    groupOrderIds = order.groupOrderIds,
                    isPickupVerified = order.isPickupVerified,
                    onVerifyOtp = { groupId ->
                        currentGroupOrderId = groupId  // Store the selected ID
                        showOtpDialog = true
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        ActionButtons(
            isPickupVerified = order.isPickupVerified,
            isDropVerified = order.isDropVerified,
            onContactHelp = {
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:+918967310388")
                }
                context.startActivity(intent)
            },
            onCompleteTransit = {
                coroutineScope.launch {
                    withContext(Dispatchers.IO) {
                        deliveryViewModel.completeTransit(
                            orderKey = order.orderKey
                        )
                        delay(1200)
                        deliveryViewModel.getRiderOrders()
                    }
                }
            }
        )
        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
fun OtpVerificationDialog(
    onDismiss: () -> Unit,
    tempOtp: String,
    onOtpChange: (String) -> Unit,
    errorMessage: String,
    onVerify: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Enter OTP to Verify",
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = tempOtp,
                    onValueChange = onOtpChange,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.NumberPassword
                    ),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    shape = RoundedCornerShape(8.dp),
                    placeholder = { Text("Enter 4-digit OTP") }
                )

                AnimatedVisibility(visible = errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                enabled = tempOtp.length == 4,
                onClick = onVerify,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text = "Verify OTP", fontSize = 14.sp)
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text = "Cancel", fontSize = 14.sp)
            }
        }
    )
}

@Composable
fun OrderStatusHeader(
    isCompleted: Boolean,
    formattedDate: String
) {
    val primaryColor = if (isCompleted)
        MaterialTheme.colorScheme.primary
    else
        MaterialTheme.colorScheme.secondary

    val containerColor = if (isCompleted)
        MaterialTheme.colorScheme.primaryContainer
    else
        MaterialTheme.colorScheme.secondaryContainer

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(containerColor)
            .padding(vertical = 24.dp, horizontal = 16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(primaryColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isCompleted)
                        Icons.Default.CheckCircle
                    else
                        Icons.Default.LocalShipping,
                    contentDescription = if (isCompleted) "Completed" else "In Transit",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = if (isCompleted) "Completed" else "In Transit",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = primaryColor
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = formattedDate,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun OrderInfoCards(
    deliveryType: String,
    paymentAmount: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.DirectionsBike,
                    contentDescription = "Delivery Type",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Type",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(4.dp))

                val deliveryTypeFormatted = when (deliveryType.lowercase()) {
                    "pickup" -> "PICKUP"
                    "delivery" -> "DELIVERY"
                    else -> deliveryType.uppercase()
                }

                Text(
                    text = deliveryTypeFormatted,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Payments,
                    contentDescription = "Payment Amount",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Amount",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "₹$paymentAmount",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun LocationDetails(order: RiderOrder) {
    if (order.deliveryType == "pickup") {
        Section(
            title = "Pickup From",
            icon = Icons.Default.LocationOn
        ) {
            order.shopDetails?.address?.let {
                ShopAddressCardRedesigned(
                    shopName = order.shopDetails.shopName,
                    dealerName = order.shopDetails.dealerName,
                    address = it,
                    phone = order.shopDetails.phone
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Section(
            title = "Drop At",
            icon = Icons.Default.PinDrop
        ) {
            AdminAddressCardRedesigned()
        }
    } else {
        Section(
            title = "Pickup From",
            icon = Icons.Default.LocationOn
        ) {
            AdminAddressCardRedesigned()
        }

        Spacer(modifier = Modifier.height(16.dp))

        Section(
            title = "Drop At",
            icon = Icons.Default.PinDrop
        ) {
            order.groupedOrders.forEach { groupedOrder ->
                ShopAddressCardForDeliveryRedesigned(order = groupedOrder)
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun ShopAddressCardRedesigned(
    shopName: String,
    dealerName: String,
    address: ShopAddress,
    phone: String
) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = shopName.take(1).uppercase(),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = shopName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Dealer: $dealerName",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            AddressSeparator()

            Spacer(modifier = Modifier.height(12.dp))

            if (address != null) {
                Row(
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(16.dp)
                            .padding(top = 2.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Column {
                        Text(
                            text = "${address.line1}, ${address.line2}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        if (address.landmark.isNotBlank()) {
                            Text(
                                text = address.landmark,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Text(
                            text = "${address.city} - ${address.pinCode}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                onClick = {
                    val intent = Intent(Intent.ACTION_DIAL).apply {
                        data = Uri.parse("tel:$phone")
                    }
                    context.startActivity(intent)
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Icon(
                    imageVector = Icons.Outlined.Call,
                    contentDescription = "Call Shop",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Call Shop",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
fun ShopAddressCardForDeliveryRedesigned(order: GroupedOrders) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = order.shopName.take(1).uppercase(),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = order.shopName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Dealer: ${order.dealerName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            AddressSeparator()

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(16.dp)
                        .padding(top = 2.dp)
                )

                Spacer(modifier = Modifier.width(4.dp))

                Column {
                    Text(
                        text = "${order.address.line1}, ${order.address.line2}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    if (order.address.landmark.isNotBlank()) {
                        Text(
                            text = order.address.landmark,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Text(
                        text = "${order.address.city} - ${order.address.pinCode}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (order.orders.isNotEmpty()) {
                ExpandableOrderList(orders = order.orders)

                Spacer(modifier = Modifier.height(16.dp))
            }

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                onClick = {
                    val intent = Intent(Intent.ACTION_DIAL).apply {
                        data = Uri.parse("tel:${order.phone}")
                    }
                    context.startActivity(intent)
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Icon(
                    imageVector = Icons.Outlined.Call,
                    contentDescription = "Call Shop",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Call Shop",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
fun ExpandableOrderList(orders: List<String>) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable { expanded = !expanded }
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.GifBox,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Orders (${orders.size})",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
            }

            Icon(
                imageVector = if (expanded)
                    Icons.Default.ExpandLess
                else
                    Icons.Default.ExpandMore,
                contentDescription = if (expanded) "Collapse" else "Expand",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        AnimatedVisibility(visible = expanded) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .height((orders.size * 48).coerceAtMost(192).dp)
            ) {

                itemsIndexed(orders) { index, orderId ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "ORDER",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = orderId.takeLast(5).uppercase(),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminAddressCardRedesigned() {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "L",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "LenZ",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            AddressSeparator()

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(16.dp)
                        .padding(top = 2.dp)
                )

                Spacer(modifier = Modifier.width(4.dp))

                Column {
                    Text(
                        text = "MG Road, Shalimar",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = "Near Congress Bhavan",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = "Nashik - 422001",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                onClick = {
                    val intent = Intent(Intent.ACTION_DIAL).apply {
                        data = Uri.parse("tel:+918967310388")
                    }
                    context.startActivity(intent)
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Icon(
                    imageVector = Icons.Outlined.Call,
                    contentDescription = "Call LenZ",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Call LenZ",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
fun AddressSeparator() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
    )
}

@Composable
fun GroupOrderSection(
    groupOrderIds: List<String>,
    isPickupVerified: Boolean,
    onVerifyOtp: (String) -> Unit
) {
    Section(
        title = "Group Order (${groupOrderIds.size})",
        icon = Icons.Default.People
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                groupOrderIds.forEachIndexed { index, groupOrderId ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${index + 1}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = "ORDER ID",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Text(
                                    text = groupOrderId.takeLast(5).uppercase(Locale.ROOT),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        Button(
                            onClick = { onVerifyOtp(groupOrderId) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isPickupVerified)
                                    MaterialTheme.colorScheme.secondaryContainer
                                else
                                    MaterialTheme.colorScheme.primaryContainer,
                                contentColor = if (isPickupVerified)
                                    MaterialTheme.colorScheme.secondary
                                else
                                    MaterialTheme.colorScheme.primary
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Pin,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isPickupVerified) "Drop OTP" else "Pickup OTP",
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }

                    if (index < groupOrderIds.size - 1) {
                        Spacer(modifier = Modifier.height(8.dp))
                        AddressSeparator()
                    }
                }
            }
        }
    }
}

@Composable
fun Section(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        content()
    }
}

@Composable
fun ActionButtons(
    isPickupVerified: Boolean,
    isDropVerified: Boolean,
    onContactHelp: () -> Unit,
    onCompleteTransit: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Contact Help Button
        OutlinedButton(
            onClick = onContactHelp,
            modifier = Modifier
                .weight(1f)
                .height(55.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Support,
                    contentDescription = "Help",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Help",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }

        // Complete Transit Button
        Button(
            onClick = onCompleteTransit,
            modifier = Modifier
                .weight(1f)
                .height(55.dp),
            enabled = isPickupVerified && isDropVerified,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isPickupVerified && isDropVerified)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.surfaceVariant,
                contentColor = if (isPickupVerified && isDropVerified)
                    MaterialTheme.colorScheme.onPrimary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.DoneAll,
                    contentDescription = "Complete Transit",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Complete Transit",
                )
            }
        }
    }
}