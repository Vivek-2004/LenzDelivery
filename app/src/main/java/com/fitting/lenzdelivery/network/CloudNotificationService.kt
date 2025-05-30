package com.fitting.lenzdelivery.network

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import com.fitting.lenzdelivery.MainActivity
import com.fitting.lenzdelivery.OrderEventBus
import com.fitting.lenzdelivery.R
import com.fitting.lenzdelivery.globalRiderId
import com.fitting.lenzdelivery.mapToNotificationData
import com.fitting.lenzdelivery.sendTokenToServer
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CloudNotificationService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        if (globalRiderId.isNotEmpty()) {
            sendTokenToServer(globalRiderId, token)
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        remoteMessage.notification?.let {
            showNotification(
                title = it.title,
                message = it.body
            )
        }
        remoteMessage.data.let { fcmData ->
            val formattedData = mapToNotificationData(fcmData)
            CoroutineScope(Dispatchers.IO).launch {
                OrderEventBus.emitNewOrder(formattedData)
            }
        }
    }

    private fun showNotification(title: String?, message: String?) {
        val soundUri = "android.resource://$packageName/${R.raw.vivek}".toUri()
        val channelId = "order_notifications"
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.app_logo)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setSound(soundUri)
            .setContentIntent(pendingIntent)
            .build()

        val notificationId = System.currentTimeMillis().toInt()
        notificationManager.notify(notificationId, notification)
    }
}