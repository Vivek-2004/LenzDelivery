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

data class GroupOrderResponse(
    val data: List<GroupOrder>
)

data class GroupOrder(
    @SerializedName("rider_details") val riderDetails: RiderDetails,
    val paidAmount: Int,
    val leftAmount: Int,
    @SerializedName("tracking_status") var trackingStatus: String,
    @SerializedName("rider_id") val riderId: String?,
    @SerializedName("admin_id") val adminId: String?,
    @SerializedName("_id") val id: String,
    val userId: String,
    val totalAmount: Int,
    val deliveryCharge: Int,
    val finalAmount: Int,
    val paymentStatus: String,
    val createdAt: String,
    val updatedAt: String,
    val common_pickup_key: String
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

data class EarningHistory(
    @SerializedName("rider_id") val riderId: String,
    @SerializedName("delivery_type") val deliveryType: String,
    @SerializedName("order_key") val orderKey: String,
    val paymentAmount: Double,
    val createdAt: String
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