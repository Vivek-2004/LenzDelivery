package com.fitting.lenzdelivery.screens.component_holders.details

import android.content.Intent
import android.widget.Toast
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.core.net.toUri
import com.fitting.lenzdelivery.DeliveryViewModel
import com.fitting.lenzdelivery.models.Address
import com.fitting.lenzdelivery.models.GroupOrders
import com.fitting.lenzdelivery.models.GroupedOrders
import com.fitting.lenzdelivery.models.LenzAdmin
import com.fitting.lenzdelivery.models.RiderOrder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun TransitOrderDetails(
    riderOrder: RiderOrder,
    deliveryViewModel: DeliveryViewModel
) {
    val order by remember(riderOrder) { mutableStateOf(riderOrder) }
    val riderState by deliveryViewModel.riderDetails.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val dateFormatter = DateTimeFormatter
        .ofPattern("MMM dd, yyyy • hh:mm a")
        .withZone(ZoneId.systemDefault())

    val createdAtInstant = Instant.parse(order.createdAt)
    val formattedDate = dateFormatter.format(createdAtInstant)
    val scrollState = rememberScrollState()

    var isLoading by remember { mutableStateOf(false) }
    var showOtpDialog by remember { mutableStateOf(false) }
    var enteredOtp by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var verifyOtp by remember { mutableStateOf(false) }
    var otpVerifyToast by remember { mutableStateOf("") }
    var currentGroupOrderId by remember { mutableStateOf("") }

    LaunchedEffect(verifyOtp) {
        if (!verifyOtp) return@LaunchedEffect
        isLoading = true
        try {
            if (order.deliveryType == "pickup") {
                if (!order.isPickupVerified) { // Shop Pickup
                    otpVerifyToast = deliveryViewModel.verifyPickupOtp(
                        groupOrderId = order.groupOrderIds.first().groupOrderId,
                        otpCode = enteredOtp
                    )
                } else { // Admin Drop
                    otpVerifyToast = deliveryViewModel.verifyAdminOtp(
                        groupOrderId = order.groupOrderIds.first().groupOrderId,
                        otpCode = enteredOtp
                    )
                }
            }
            if (order.deliveryType == "delivery") {
                if (!order.isPickupVerified) { // Admin Pickup
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
            isLoading = false
            enteredOtp = ""
            currentGroupOrderId = ""
        }
    }

    if (otpVerifyToast.isNotEmpty()) {
        if (otpVerifyToast == "OTP Verified Successfully") {
            if (!order.isPickupVerified) {
                order.isPickupVerified = true
            } else {
                order.isDropVerified = true
            }
        }
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
            order = order,
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
        riderState?.lenzAdminId?.let { admin ->
            LocationDetails(
                order = order,
                admin = admin
            )
        }

        // Group Orders - Enhanced with better visual hierarchy
        if (order.isPickupVerified && order.isDropVerified) {
            Card(
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.LightGray.copy(alpha = 0.6f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "No More Drops • Complete Transit",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            if (order.deliveryType == "delivery" && !order.isPickupVerified) {
                Button(
                    enabled = !isLoading,
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
                    if (isLoading) {
                        LinearProgressIndicator(
                            color = Color.DarkGray,
                            gapSize = 4.dp
                        )
                    } else {
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
                }
            } else {
                Spacer(modifier = Modifier.height(16.dp))
                GroupOrderSection(
                    groupOrderIds = order.groupOrderIds,
                    isPickupVerified = order.isPickupVerified,
                    isLoading = isLoading,
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
                    data = "tel:+918584932580".toUri()
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
    order: RiderOrder,
    formattedDate: String
) {
    val primaryColor = if (order.isPickupVerified && order.isDropVerified)
        MaterialTheme.colorScheme.primary
    else
        MaterialTheme.colorScheme.secondary

    val containerColor = if (order.isPickupVerified && order.isDropVerified)
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
                    imageVector = if (order.isPickupVerified && order.isDropVerified)
                        Icons.Default.CheckCircle
                    else
                        Icons.Default.LocalShipping,
                    contentDescription = if (order.isPickupVerified && order.isDropVerified) "Dropped" else "In Transit",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = if (order.isPickupVerified && order.isDropVerified) "Dropped" else "In Transit",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = primaryColor
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Order #${order.orderKey}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold
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
fun LocationDetails(
    order: RiderOrder,
    admin: LenzAdmin
) {
    if (order.deliveryType == "pickup") {
        Section(
            title = "Pickup From",
            icon = Icons.Default.LocationOn
        ) {
            order.shopDetails?.address?.let {
                ShopAddressCard(
                    shopName = order.shopDetails.shopName,
                    dealerName = order.shopDetails.dealerName,
                    address = it,
                    phone = order.shopDetails.phone,
                    isPickupVerified = order.isPickupVerified
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Section(
            title = "Drop At",
            icon = Icons.Default.PinDrop
        ) {
            AdminAddressCard(
                order = order,
                admin = admin
            )
        }
    } else {
        Section(
            title = "Pickup From",
            icon = Icons.Default.LocationOn
        ) {
            AdminAddressCard(
                order = order,
                admin = admin
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Section(
            title = "Drop At",
            icon = Icons.Default.PinDrop
        ) {
            order.groupedOrders.forEach { groupedOrder ->
                ShopAddressCardForDelivery(
                    order = groupedOrder,
                    riderOrder = order,
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun ShopAddressCard(
    shopName: String,
    dealerName: String,
    address: Address,
    phone: String,
    isPickupVerified: Boolean
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
                .background(
                    if (isPickupVerified) Color.Green.copy(alpha = 0.3f)
                    else Color.Unspecified
                )
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
                        data = "tel:$phone".toUri()
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
fun ShopAddressCardForDelivery(
    order: GroupedOrders,
    riderOrder: RiderOrder
) {
    val context = LocalContext.current

    val allOrdersCompleted = order.orders.all { orderId ->
        riderOrder.groupOrderIds.any { groupOrder ->
            groupOrder.groupOrderId == orderId && groupOrder.trackingStatus == "Order Completed"
        }
    }

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
                .background(
                    if (riderOrder.isDropVerified || allOrdersCompleted) Color.Green.copy(alpha = 0.3f)
                    else Color.Unspecified
                )
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
                        data = "tel:${order.phone}".toUri()
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
fun AdminAddressCard(
    order: RiderOrder,
    admin: LenzAdmin
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
                .background(
                    if (
                        (order.deliveryType == "pickup" && order.isDropVerified) ||
                        (order.deliveryType == "delivery" && order.isPickupVerified)
                    ) {
                        Color.Green.copy(alpha = 0.3f)
                    } else {
                        Color.Unspecified
                    }
                )
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

                Column {
                    Text(
                        text = "LenZ",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = admin.name,
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
                        text = "${admin.address.line1}, ${admin.address.line2}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = admin.address.landmark,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )

                    Text(
                        text = "${admin.address.city} - ${admin.address.pinCode}",
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
                        data = "tel:+91${admin.orderPhone.takeLast(10)}".toUri()
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
    groupOrderIds: List<GroupOrders>,
    isPickupVerified: Boolean,
    onVerifyOtp: (String) -> Unit,
    isLoading: Boolean
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
                groupOrderIds.reversed().forEachIndexed { index, groupOrder ->
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
                                    text = groupOrder.groupOrderId.takeLast(5)
                                        .uppercase(Locale.ROOT),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                        Button(
                            enabled = !isLoading && groupOrder.trackingStatus != "Order Completed",
                            onClick = { onVerifyOtp(groupOrder.groupOrderId) },
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
                            if (isLoading) {
                                LinearProgressIndicator(
                                    modifier = Modifier.width(70.dp),
                                    color = Color.DarkGray,
                                    gapSize = 4.dp
                                )
                            } else {
                                if (groupOrder.trackingStatus == "Order Completed") {
                                    Text(
                                        text = "Verified",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = Color(0xFF008000)
                                    )
                                } else {
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
    var isTransitClicked by remember { mutableStateOf(false) }
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
            onClick = {
                onCompleteTransit()
                isTransitClicked = true
            },
            modifier = Modifier
                .weight(1f)
                .height(55.dp),
            enabled = isPickupVerified && isDropVerified && !isTransitClicked,
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
                if (isTransitClicked) {
                    CircularProgressIndicator(
                        color = Color.DarkGray,
                        strokeWidth = 4.dp
                    )
                } else {
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
}