package com.liven.diner.ui.screen.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.liven.diner.data.model.order.Order
import com.liven.diner.data.repository.CartOrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OrderHistoryState(
    val isLoading: Boolean = true,
    val orders: List<Order> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class OrderHistoryViewModel @Inject constructor(
    private val cartOrderRepository: CartOrderRepository
) : ViewModel() {

    private val _orderHistoryState = MutableStateFlow(OrderHistoryState())
    val orderHistoryState = _orderHistoryState

    init {
        getOrderHistory()
    }

    fun getOrderHistory(status: String? = null) {
        viewModelScope.launch {
            _orderHistoryState.value = OrderHistoryState(isLoading = true, error = null)
            val result = cartOrderRepository.getDinerOrders(status)
            result.onSuccess {
                _orderHistoryState.value = OrderHistoryState(orders = it, isLoading = false)
            }.onFailure {
                _orderHistoryState.value = OrderHistoryState(error = it.message, isLoading = false)
            }
        }
    }
}