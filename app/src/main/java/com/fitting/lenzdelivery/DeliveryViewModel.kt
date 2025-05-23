package com.fitting.lenzdelivery

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitting.lenzdelivery.models.AssignDeliveryReqBody
import com.fitting.lenzdelivery.models.AssignPickupReqBody
import com.fitting.lenzdelivery.models.ChangeWorkingStatus
import com.fitting.lenzdelivery.models.OtpCode
import com.fitting.lenzdelivery.models.PatchCompleteTransit
import com.fitting.lenzdelivery.models.RiderDetails
import com.fitting.lenzdelivery.models.RiderOrder
import com.fitting.lenzdelivery.models.VerifyAdminOtp
import com.fitting.lenzdelivery.models.VerifyAdminPickupOtpReqBody
import com.fitting.lenzdelivery.network.deliveryService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class DeliveryViewModel(riderId: String) : ViewModel() {

    private val _deliveryService = deliveryService

    private val currentRiderId = riderId
    var riderObjectId by mutableStateOf("")
        private set

    private val _riderDetails = MutableStateFlow<RiderDetails?>(null)
    val riderDetails: StateFlow<RiderDetails?> = _riderDetails.asStateFlow()

    var riderOrders by mutableStateOf<List<RiderOrder>>(emptyList())
        private set

    init {
        getRiderDetails()
        getRiderOrders()
        observeNewOrders()
    }

    private fun observeNewOrders() {
        viewModelScope.launch {
            OrderEventBus.newOrders.collect {
                getRiderOrders()
            }
        }
    }

    fun getRiderDetails() {
        viewModelScope.launch {
            try {
                val riderResponse = _deliveryService.getRiderDetails(
                    riderId = currentRiderId
                )
                _riderDetails.value = riderResponse
                riderObjectId = riderResponse._id
            } catch (_: Exception) {
            }
        }
    }

    fun getRiderOrders() {
        viewModelScope.launch {
            try {
                val historyResponse = _deliveryService.getOrders()
                riderOrders = historyResponse
            } catch (e: Exception) {
                riderOrders = emptyList()
            }
        }
    }

    fun editRiderWorkingStatus(riderId: Int, newStatus: Boolean) {
        viewModelScope.launch {
            try {
                _deliveryService.editWorkingStatus(
                    riderId = riderId,
                    newStatus = ChangeWorkingStatus(
                        newStatus = newStatus
                    )
                )
            } catch (_: Exception) {
            }
        }
    }

    fun assignPickupRider(
        groupOrderId: String,
        pickupRiderId: String = riderObjectId
    ) {
        viewModelScope.launch {
            try {
                val requestBody = AssignPickupReqBody(pickupRiderId = pickupRiderId)
                _deliveryService.assignPickupRider(
                    groupOrderId = groupOrderId,
                    pickupRiderId = requestBody
                )
            } catch (_: Exception) {
            }
        }
    }

    fun assignDeliveryRider(
        pickupKey: String,
        pickupRiderId: String = riderObjectId
    ) {
        viewModelScope.launch {
            try {
                val reqBody = AssignDeliveryReqBody(
                    adminPickupKey = pickupKey,
                    deliveryRiderId = pickupRiderId
                )
                _deliveryService.assignDeliveryRider(
                    deliveryReqBody = reqBody
                )
            } catch (_: HttpException) {
            }
        }
    }

    suspend fun verifyPickupOtp(
        groupOrderId: String,
        otpCode: String
    ): String {
        return try {
            val requestBody = OtpCode(
                otpCode = otpCode
            )
            val response = _deliveryService.verifyPickupOtp(
                groupOrderId = groupOrderId,
                otpCode = requestBody
            )

            if (response.code() == 200) "OTP Verified Successfully"
            else "Incorrect OTP"
        } catch (e: Exception) {
            "Please Try Again"
        }
    }

    suspend fun verifyAdminOtp(
        groupOrderId: String,
        otpCode: String
    ): String {
        return try {
            val requestBody = VerifyAdminOtp(
                otpCode = otpCode,
                riderObjectId = riderObjectId
            )
            val response = _deliveryService.verifyAdminOtp(
                groupOrderId = groupOrderId,
                body = requestBody
            )

            if (response.code() == 200) "OTP Verified Successfully"
            else "Incorrect OTP"
        } catch (e: Exception) {
            "Please Try Again"
        }
    }

    suspend fun verifyAdminPickupOtp(
        orderKey: String,
        otpCode: String,
        riderId: String = riderObjectId
    ): String {
        return try {
            val reqBody = VerifyAdminPickupOtpReqBody(
                riderId = riderId,
                otpCode = otpCode
            )
            val response = _deliveryService.verifyAdminPickupOtp(
                orderKey = orderKey,
                body = reqBody
            )

            if (response.code() == 200) "OTP Verified Successfully"
            else "Incorrect OTP"
        } catch (e: Exception) {
            "Please Try Again"
        }
    }

    suspend fun verifyShopDropOtp(
        groupOrderId: String,
        otpCode: String,
        riderId: String = riderObjectId
    ): String {
        return try {
            val reqBody = VerifyAdminPickupOtpReqBody(
                riderId = riderId,
                otpCode = otpCode
            )
            val response = _deliveryService.verifyShopDropOtp(
                groupOrderId = groupOrderId,
                body = reqBody
            )

            if (response.code() == 200) "OTP Verified Successfully"
            else "Incorrect OTP"
        } catch (e: Exception) {
            "Please Try Again"
        }
    }

    fun completeTransit(
        orderKey: String,
        riderId: String = riderObjectId
    ) {
        viewModelScope.launch {
            try {
                _deliveryService.patchCompleteTransit(
                    orderKey = orderKey,
                    riderId = PatchCompleteTransit(
                        riderId = riderId
                    )
                )
            } catch (_: Exception) {
            }
        }
    }
}