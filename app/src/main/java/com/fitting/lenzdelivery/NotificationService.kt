package com.fitting.lenzdelivery

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import com.fitting.lenzdelivery.models.RiderOrder
import com.google.gson.Gson
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.URISyntaxException

class NotificationService : Service() {

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "OrderNotifications"
        private const val FOREGROUND_NOTIFICATION_ID = 1
    }

    private lateinit var socket: Socket
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val TAG = "VivekGhosh"

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()

        // Start foreground notification immediately to avoid ANR
        startForeground(FOREGROUND_NOTIFICATION_ID, createForegroundNotification())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        // Setup socket in onStartCommand to avoid blocking onCreate
        setupSocket()

        // If service is killed, restart it
        return START_STICKY
    }

    private fun setupSocket() {
        try {
            val options = IO.Options()
            options.reconnection = true
            options.reconnectionAttempts = 10
            options.reconnectionDelay = 1000

            socket = IO.socket("https://lenz-backend.onrender.com", options)

            // Define listeners before connecting
            socket.on(Socket.EVENT_CONNECT) {
                Log.d(TAG, "Socket.IO connected!")

                // Join admin room after successful connection
                socket.emit("joinAdminRoom")
            }

            socket.on(Socket.EVENT_CONNECT_ERROR) { args ->
                Log.e(TAG, "Socket connection error: ${args[0]}")
            }

            socket.on(Socket.EVENT_DISCONNECT) {
                Log.d(TAG, "Socket disconnected")
            }

            // Listen for new group orders with more detailed logging
            socket.on("newGroupOrder", onNewGroupOrder)

            // Connect to the server
            socket.connect()
            Log.d(TAG, "Socket.IO connection attempt initiated")

        } catch (e: URISyntaxException) {
            Log.e(TAG, "Socket connection error: ${e.message}")
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error setting up socket: ${e.message}")
            e.printStackTrace()
        }
    }

    private val onNewGroupOrder = Emitter.Listener { args ->
        try {
            val data = args[0] as JSONObject
            val orderMessage = data.optString("message", "New order received!")
            val riderOrder =
                Gson().fromJson(data.getJSONObject("data").toString(), RiderOrder::class.java)
            if (riderOrder.riderId == null) {
                showNotification(orderMessage)
            }
            serviceScope.launch {
                OrderEventBus.emitNewOrder(riderOrder)
            }
        } catch (_: Exception) {
        }
    }

    private fun showNotification(message: String) {
        try {
            val soundUri = ("android.resource://" + packageName + "/" + R.raw.vivek).toUri()

            val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

            val pendingIntent = PendingIntent.getActivity(
                this, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setContentTitle("New Order Alert")
                .setContentText(message)
                .setSmallIcon(R.drawable.app_logo)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setSound(soundUri)
                .setContentIntent(pendingIntent)
                .build()

            val notificationId = System.currentTimeMillis().toInt()
            notificationManager.notify(notificationId, notification)
            println("Notification shown with ID: $notificationId")
        } catch (e: Exception) {
            println("Error showing notification: ${e.message}")
            e.printStackTrace()
        }
    }

    private fun createForegroundNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Order Listener Active")
            .setContentText("Checking for New Orders...")
            .setSmallIcon(R.drawable.app_logo)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .build()
    }

    private fun createNotificationChannel() {
        try {
            val soundUri = ("android.resource://" + packageName + "/" + R.raw.vivek).toUri()

            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Order Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for new order alerts"
                enableVibration(true)
                enableLights(true)
                setSound(
                    soundUri, AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
            }

            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
            println("Notification channel created successfully")
        } catch (e: Exception) {
            println("Error creating notification channel: ${e.message}")
            e.printStackTrace()
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        Log.d(TAG, "Service onDestroy called, disconnecting socket")
        if (::socket.isInitialized && socket.connected()) {
            socket.disconnect()
        }
        super.onDestroy()
    }
}