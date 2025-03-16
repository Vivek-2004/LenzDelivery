package com.fitting.lenzdelivery.network

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import com.fitting.lenzdelivery.MainActivity
import com.fitting.lenzdelivery.R
import com.fitting.lenzdelivery.models.RiderDetails
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CloudNotificationService : FirebaseMessagingService() {
    private val serviceScope = CoroutineScope(Dispatchers.IO)
    private val networkService = deliveryService

    override fun onCreate() {
        super.onCreate()
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("VIVEK", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            val token = task.result
            sendTokenToServer(token)
        })
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        sendTokenToServer(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        var riderDetails: RiderDetails? by mutableStateOf(null)
        val sharedPref = getSharedPreferences("LenZDelivery", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPref.getBoolean("isLoggedIn", false)
        val riderId = sharedPref.getString("riderId", "")
        if (isLoggedIn) {
            serviceScope.launch {
                riderDetails = networkService.getRiderDetails(
                    riderId = riderId!!
                )
                if (riderDetails != null) {
                    if (riderDetails!!.isWorking && riderDetails!!.isAvailable) {
                        remoteMessage.notification?.let {
                            showNotification(
                                title = it.title,
                                message = it.body
                            )
                        }
                    }
                }
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
            .setSound(soundUri)  // Set the sound here too
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(0, notification)
    }

    private fun sendTokenToServer(token: String) {
        Log.d("BEAT", token)
    }
}