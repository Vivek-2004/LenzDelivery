package com.fitting.lenzdelivery

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
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

    private lateinit var socket: Socket
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val TAG = "OrderNotificationService"

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Service onCreate called")
        createNotificationChannel()

        // Start foreground notification immediately to avoid ANR
        startForeground(FOREGROUND_NOTIFICATION_ID, createForegroundNotification())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "Service onStartCommand called")

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
            showNotification(orderMessage)
            val riderOrder =
                Gson().fromJson(data.getJSONObject("data").toString(), RiderOrder::class.java)
            serviceScope.launch {
                OrderEventBus.emitNewOrder(riderOrder)
            }

        } catch (e: Exception) {
            Log.e(TAG, "Error parsing order: ${e.message}")
        }
    }

    private fun showNotification(message: String) {
        println("Notification called")
        try {
            Log.d(TAG, "Showing notification with message: $message")
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setContentTitle("New Order Alert")
                .setContentText(message)
                .setSmallIcon(R.drawable.app_logo)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build()

            val notificationId = System.currentTimeMillis().toInt()
            notificationManager.notify(notificationId, notification)
            Log.d(TAG, "Notification shown with ID: $notificationId")
        } catch (e: Exception) {
            Log.e(TAG, "Error showing notification: ${e.message}")
            e.printStackTrace()
        }
    }

    private fun createForegroundNotification(): Notification {
        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Order Listener Active")
            .setContentText("Listening for new orders...")
            .setSmallIcon(R.drawable.app_logo)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setOngoing(true)
            .build()
    }

    private fun createNotificationChannel() {
        try {
            Log.d(TAG, "Creating notification channel")
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Order Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for new order alerts"
                enableVibration(true)
                enableLights(true)
            }

            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
            Log.d(TAG, "Notification channel created successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error creating notification channel: ${e.message}")
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

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "OrderNotifications"
        private const val FOREGROUND_NOTIFICATION_ID = 1
    }
}