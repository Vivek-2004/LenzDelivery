package com.fitting.lenzdelivery

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitting.lenzdelivery.models.AssignOrderReqBody
import com.fitting.lenzdelivery.models.ChangeWorkingStatus
import com.fitting.lenzdelivery.models.EditPhoneNumber
import com.fitting.lenzdelivery.models.GroupOrderData
import com.fitting.lenzdelivery.models.OtpCode
import com.fitting.lenzdelivery.models.RiderDetails
import com.fitting.lenzdelivery.models.RiderOrder
import com.fitting.lenzdelivery.models.VerifyAdminOtp
import com.fitting.lenzdelivery.network.WebSocketManager
import com.fitting.lenzdelivery.network.deliveryService
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.http.HTTP

class DeliveryViewModel(riderId: String) : ViewModel() {

    private val _deliveryService = deliveryService

    val currentRiderId = riderId
    var riderObjectId by mutableStateOf("")
        private set

    private val _riderDetails = MutableStateFlow<RiderDetails?>(null)
    val riderDetails: StateFlow<RiderDetails?> = _riderDetails.asStateFlow()

    var riderOrders by mutableStateOf<List<RiderOrder>>(emptyList())
        private set

    //
    private val _groupOrders = mutableStateListOf<GroupOrderData>()
    val groupOrders: List<GroupOrderData> get() = _groupOrders
    //

    init {
        getRiderDetails()
        getRiderOrders()
//        connectSocket()
    }

    fun getRiderDetails() {
        viewModelScope.launch {
            try {
                val ridersResponse = _deliveryService.getRiderDetails(
                    riderId = currentRiderId
                )
                _riderDetails.value = ridersResponse
                riderObjectId = ridersResponse._id
            } catch (_: Exception) {
            }
        }
    }

    fun editRiderContact(
        riderId: Int,
        newPhoneNumber: String
    ) {
        viewModelScope.launch {
            try {
                _deliveryService.editRiderPhone(
                    riderId = riderId,
                    newPhoneNumber = EditPhoneNumber(
                        newPhoneNumber = newPhoneNumber
                    )
                )
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

    fun selfAssignRider(
        groupOrderId: String,
        pickupRiderId: String
    ) {
        viewModelScope.launch {
            try {
                val requestBody = AssignOrderReqBody(pickupRiderId = pickupRiderId)
                // Convert to JSON
                val jsonRequest = Gson().toJson(requestBody)
                println("Request JSON: $jsonRequest") // Print the JSON before sending
                val response = _deliveryService.assignRider(
                    groupOrderId = groupOrderId,
                    pickupRiderId = requestBody
                )
                println(response)
            } catch (e: Exception) {
                println(e)
            }
        }
    }

    fun verifyPickupOtp(
        groupOrderId: String,
        otpCode: String
    ) {
        viewModelScope.launch {
            try {
                val requestBody = OtpCode(
                    otpCode = otpCode
                )
                // Convert to JSON
//                val jsonRequest = Gson().toJson(requestBody)
//                println("Request JSON: $jsonRequest")

                val response = _deliveryService.verifyPickupOtp(
                    groupOrderId = groupOrderId,
                    otpCode = requestBody
                )

//                if (response.isSuccessful) {
//                    println("SUCCESFUL"+response)
//                } else {
//                    println("UNSUCCESFUL"+response)
//                }
            } catch (e: Exception) {
//                println(e)
            }
        }
    }

    fun verifyAdminOtp(
        groupOrderId: String,
        otp: String
    ) {
        viewModelScope.launch {
            try {
                val requestBody = VerifyAdminOtp(
                    otpCode = otp,
                    riderObjectId = riderObjectId
                )
                val jsonRequest = Gson().toJson(requestBody)
                println("Request JSON: $jsonRequest")

                val response = _deliveryService.verifyAdminOtp(
                    groupOrderId = groupOrderId,
                    body = requestBody
                )

                if (response.isSuccessful) {
                    println("SUCCESFUL"+response)
                } else {
                    println("UNSUCCESFUL"+response)
                }

            } catch(e: Exception) {
                println(e)
            }
        }
    }

    private fun connectSocket() {
        WebSocketManager.connect()
        viewModelScope.launch {
            WebSocketManager.groupOrderFlow.collect { order ->
                _groupOrders.add(order)
            }
        }
    }

    override fun onCleared() {
        WebSocketManager.disconnect()
        super.onCleared()
    }
}