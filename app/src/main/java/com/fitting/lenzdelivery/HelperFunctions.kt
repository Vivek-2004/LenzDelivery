package com.fitting.lenzdelivery

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.fitting.lenzdelivery.models.FcmToken
import com.fitting.lenzdelivery.models.NotificationData
import com.fitting.lenzdelivery.network.deliveryService
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField
import java.util.Locale

fun String.formDate(): String {
    val instant = Instant.parse(this)
    val zonedDateTime = instant.atZone(ZoneId.of("Asia/Kolkata"))
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH)
    return zonedDateTime.format(formatter)
}

fun String.toIST(): String {
    val utcDateTime = ZonedDateTime.parse(this)
    val istDateTime = utcDateTime.withZoneSameInstant(ZoneId.of("Asia/Kolkata"))
    val formatter = DateTimeFormatterBuilder()
        .appendPattern("hh:mm ")
        .appendText(ChronoField.AMPM_OF_DAY, mapOf(0L to "a.m.", 1L to "p.m."))
        .toFormatter(Locale.ENGLISH)
    return istDateTime.format(formatter)
}

var globalRiderId by mutableStateOf("")

fun sendTokenToServer(riderId: String, token: String) {
    Log.d("SEND", token)
    val reqBody = FcmToken(
        riderId = riderId,
        fcmToken = token
    )
    CoroutineScope(Dispatchers.IO).launch {
        try {
            deliveryService.updateFcmToken(
                reqBody = reqBody
            )
        } catch (_: Exception) {
        }
    }
}

fun registerFcmTokenAfterLogin(riderId: String) {
    FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
        if (!task.isSuccessful) {
            Log.w("FCM", "Fetching FCM registration token failed", task.exception)
            return@OnCompleteListener
        }
        val token = task.result
        sendTokenToServer(riderId, token)
    })
}

fun deleteFcmTokenAfterLogout() {
    FirebaseMessaging.getInstance().deleteToken()
}

fun mapToNotificationData(fcmData: Map<String, String>): NotificationData {
    require(fcmData.containsKey("order_key") && fcmData.containsKey("operation")) {
        "Missing required keys: order_key or operation"
    }

    return NotificationData(
        orderKey = fcmData["order_key"]!!,
        operation = fcmData["operation"]!!
    )
}