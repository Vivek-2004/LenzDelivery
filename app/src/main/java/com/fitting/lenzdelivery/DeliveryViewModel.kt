package com.fitting.lenzdelivery

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
import com.fitting.lenzdelivery.models.RiderDetails
import com.fitting.lenzdelivery.network.WebSocketManager
import com.fitting.lenzdelivery.network.deliveryService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DeliveryViewModel(riderId: String) : ViewModel() {

    private val _deliveryService = deliveryService
    val currentRiderId = riderId
    private val _riderDetails = MutableStateFlow<RiderDetails?>(null)
    val riderDetails: StateFlow<RiderDetails?> = _riderDetails.asStateFlow()
    var allGroupOrders by mutableStateOf<List<GroupOrder>>(emptyList())
        private set

    var earningHistory by mutableStateOf<List<EarningHistory>>(emptyList())
        private set

    private val _groupOrders = mutableStateListOf<GroupOrderData>()
    val groupOrders: List<GroupOrderData> get() = _groupOrders

    init {
        getRiderDetails()
        getGroupOrders()
//        connectSocket()
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
                val ridersResponse = _deliveryService.getRiderDetails(
                    riderId = currentRiderId
                )
                _riderDetails.value = ridersResponse
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
            } catch (_: Exception) {
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