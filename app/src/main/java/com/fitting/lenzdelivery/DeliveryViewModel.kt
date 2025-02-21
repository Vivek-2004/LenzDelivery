package com.fitting.lenzdelivery

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitting.lenzdelivery.models.ChangeWorkingStatus
import com.fitting.lenzdelivery.models.EarningHistory
import com.fitting.lenzdelivery.models.EditPhoneNumber
import com.fitting.lenzdelivery.models.GroupOrder
import com.fitting.lenzdelivery.models.GroupOrderData
import com.fitting.lenzdelivery.models.LogInRider
import com.fitting.lenzdelivery.models.RiderDetails
import com.fitting.lenzdelivery.network.WebSocketManager
import com.fitting.lenzdelivery.network.deliveryService
import kotlinx.coroutines.launch

class DeliveryViewModel : ViewModel() {
    private val _deliveryService = deliveryService

    private val _groupOrders = mutableStateListOf<GroupOrderData>()
    val groupOrders: List<GroupOrderData> get() = _groupOrders

    var allGroupOrders by mutableStateOf<List<GroupOrder>>(emptyList())
        private set

    var allRiders by mutableStateOf<List<RiderDetails>>(emptyList())
        private set

    var earningHistory by mutableStateOf<List<EarningHistory>>(emptyList())
        private set

    var riderLogInMessage by mutableStateOf("")
    var loginRiderId by mutableStateOf("")

    init {
        getGroupOrders()
        connectSocket()
        getRiderDetails()
    }

    fun getGroupOrders() {
        viewModelScope.launch {
            try {
                val groupsResponse = _deliveryService.getGroupOrders()
                allGroupOrders = groupsResponse.data
            } catch (e: Exception) {
                allGroupOrders = emptyList()
            }
        }
    }

    fun getRiderDetails() {
        viewModelScope.launch {
            try {
                val ridersResponse = _deliveryService.getAllRiderDetails()
                allRiders = ridersResponse
            } catch (e: Exception) {
                allRiders = emptyList()
            }
        }
    }

    fun riderLogin(
        riderEmail: String,
        password: String
    ) {
        viewModelScope.launch {
            try {
                val riderLogInResponse = _deliveryService.logInRider(
                    loginBody = LogInRider(
                        riderEmail = riderEmail,
                        password = password
                    )
                )
                riderLogInMessage = riderLogInResponse.message
                loginRiderId = riderLogInResponse.riderId
            } catch (e: Exception) {
                riderLogInMessage = "Invalid ID or Password"
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

    fun getRiderEarningHistory(riderId: String) {
        viewModelScope.launch {
            try {
                val historyResponse = _deliveryService.getEarningHistory(
                    riderId = riderId
                )
                earningHistory = historyResponse
            } catch (e: Exception) {
                earningHistory = emptyList()
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
            } catch(_: Exception) {
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