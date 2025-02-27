package com.fitting.lenzdelivery.models

import com.google.gson.annotations.SerializedName

//
data class GroupOrderData(
    val id: String,
    val userId: String,
    val totalAmount: Double,
    val paymentStatus: String
)
//

data class GroupOrder(
    @SerializedName("_id") val id: String,
    @SerializedName("tracking_status") var trackingStatus: String,
    val userId: String
//    val common_pickup_key: String,
//    val shop_pickup_key: String
)

data class RiderDetails(
    val _id: String,
    val riderId: String,
    val name: String,
    val phone: String,
    val email: String,
    val vehicleNumber: String,
    var isAvailable: Boolean,
    val isWorking: Boolean,
    val totalOrders: Int,
    val dailyOrders: Int,
    val totalEarnings: Double,
    val dailyEarnings: Double,
    val createdAt: String
)

data class ShopAddress(
    val line1: String,
    val line2: String,
    val landmark: String,
    val city: String,
    val state: String,
    val pinCode: String
)

data class GroupedOrders(
    val shopName: String,
    val dealerName: String,
    val phone: String,
    val alternatePhone: String,
    val address: ShopAddress
)

data class ShopDetails(
    val shopName: String,
    val dealerName: String,
    val phone: String,
    val alternatePhone: String,
    val address: ShopAddress
)

data class RiderOrder(
    @SerializedName("rider_id") val riderId: String,
    @SerializedName("delivery_type") val deliveryType: String,
    @SerializedName("order_key") val orderKey: String,
    val isCompleted: Boolean,
    val paymentAmount: Double,
    val createdAt: String,
    @SerializedName("shop_details") val shopDetails: ShopDetails?,
    @SerializedName("group_order_ids") val groupOrderIds: List<String>,
    @SerializedName("grouped_orders") val groupedOrders: List<GroupedOrders>
)

data class EditPhoneNumber(
    val newPhoneNumber: String
)

data class ChangeWorkingStatus(
    val newStatus: Boolean
)

data class LogInRider(
    val riderEmail: String,
    val password: String
)

data class LogInRiderResponse(
    val message: String,
    val riderId: String,
    val confirmation: Boolean
)

data class AssignOrderReqBody(
    @SerializedName("pickup_rider_id") val pickupRiderId: String
)