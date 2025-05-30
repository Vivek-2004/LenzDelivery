package com.fitting.lenzdelivery.models

import com.google.gson.annotations.SerializedName

data class RiderDetails(
    val _id: String,
    val riderId: String,
    val name: String,
    val phone: String,
    val email: String,
    val vehicleNumber: String,
    val lenzAdminId: LenzAdmin,
    var isAvailable: Boolean,
    val isWorking: Boolean,
    val totalOrders: Int,
    val dailyOrders: Int,
    val totalEarnings: Double,
    val dailyEarnings: Double,
    val createdAt: String
)

data class LenzAdmin(
    val address: Address,
    val _id: String,
    val name: String,
    val email: String,
    val phone: String,
    val orderPhone: String,
    val adminId: String
)

data class Address(
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
    val address: Address,
    val orders: List<String>
)

data class ShopDetails(
    val shopName: String,
    val dealerName: String,
    val phone: String,
    val alternatePhone: String,
    val address: Address
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
    var isPickupVerified: Boolean,
    var isDropVerified: Boolean,
    val paymentAmount: Double,
    val createdAt: String,
    @SerializedName("shop_details") val shopDetails: ShopDetails?,
    @SerializedName("group_order_ids") val groupOrderIds: List<GroupOrders>,
    @SerializedName("grouped_orders") val groupedOrders: List<GroupedOrders>
)

data class ChangeWorkingStatus(
    val newStatus: Boolean
)

data class LogInRider(
    val riderEmail: String,
    val password: String
)

data class SignUpRider(
    val name: String,
    val phone: String,
    val email: String,
    val password: String,
    val vehicleNumber: String,
    val adminId: String,
    val adminAuth: String
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

data class FcmToken(
    val riderId: String,
    val fcmToken: String
)

data class NotificationData(
    @SerializedName("order_key") val orderKey: String,
    val operation: String
)