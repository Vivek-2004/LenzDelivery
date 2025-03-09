package com.fitting.lenzdelivery.models

import com.google.gson.annotations.SerializedName

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
    val address: ShopAddress,
    val orders: List<String>
)

data class ShopDetails(
    val shopName: String,
    val dealerName: String,
    val phone: String,
    val alternatePhone: String,
    val address: ShopAddress
)

data class GroupOrders(
    @SerializedName("_id") val groupOrderId: String,
    @SerializedName("tracking_status") val trackingStatus: String
)

data class RiderOrder(
    @SerializedName("rider_id") val riderId: String,
    @SerializedName("delivery_type") val deliveryType: String,
    @SerializedName("order_key") val orderKey: String,
    val isCompleted: Boolean,
    val isPickupVerified: Boolean,
    val isDropVerified: Boolean,
    val paymentAmount: Double,
    val createdAt: String,
    @SerializedName("shop_details") val shopDetails: ShopDetails?,
    @SerializedName("group_order_ids") val groupOrderIds: List<GroupOrders>,
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

data class AssignPickupReqBody(
    @SerializedName("pickup_rider_id") val pickupRiderId: String
)

data class AssignDeliveryReqBody(
    @SerializedName("admin_pickup_key") val adminPickupKey: String,
    @SerializedName("delivery_rider_id") val deliveryRiderId: String
)

data class OtpCode(
    @SerializedName("otp_code") val otpCode: String
)

data class VerifyAdminOtp(
    @SerializedName("otp_code") val otpCode: String,
    @SerializedName("rider_id") val riderObjectId: String
)

data class VerifyAdminPickupOtpReqBody(
    @SerializedName("rider_id") val riderId: String,
    @SerializedName("otp_code") val otpCode: String
)

data class PatchCompleteTransit(
    val riderId: String
)