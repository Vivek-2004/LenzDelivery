package com.fitting.lenzdelivery.network

import com.fitting.lenzdelivery.models.GroupOrderData
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONObject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object WebSocketManager {
    private const val SERVER_URL = "https://lenz-backend.onrender.com"
    private lateinit var socket: Socket

    private val _groupOrderFlow = MutableSharedFlow<GroupOrderData>()
    val groupOrderFlow = _groupOrderFlow.asSharedFlow()

    fun connect() {
        val opts = IO.Options().apply {
            reconnection = true
            transports = arrayOf("websocket")
        }

        socket = IO.socket(SERVER_URL, opts)

        socket.on(Socket.EVENT_CONNECT) {
            // Join admin room after connection
            socket.emit("joinAdminRoom")
        }

        socket.on("newGroupOrder") { args ->
            val data = args[0] as JSONObject
            val groupOrder = GroupOrderData(
                id = data.getString("groupOrderId"),
                userId = data.getString("userId"),
                totalAmount = data.getDouble("totalAmount"),
                paymentStatus = data.getString("paymentStatus")
            )
            _groupOrderFlow.tryEmit(groupOrder)
        }

        socket.connect()
    }

    fun disconnect() {
        socket.disconnect()
        socket.off()
    }
}