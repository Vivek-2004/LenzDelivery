package com.fitting.lenzdelivery

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitting.lenzdelivery.models.ChangeWorkingStatus
import com.fitting.lenzdelivery.models.EditPhoneNumber
import com.fitting.lenzdelivery.models.GroupOrderData
import com.fitting.lenzdelivery.models.RiderDetails
import com.fitting.lenzdelivery.models.RiderOrder
import com.fitting.lenzdelivery.network.WebSocketManager
import com.fitting.lenzdelivery.network.deliveryService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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