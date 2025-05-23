package com.liven.diner.ui.screen.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.liven.diner.data.model.order.Order
import com.liven.diner.data.model.order.OrderItemRequest
import com.liven.diner.data.model.order.OrderRequest
import com.liven.diner.data.repository.CartOrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class OrderPlacementState {
    object Idle : OrderPlacementState()
    object Loading : OrderPlacementState()
    data class Success(val order: Order) : OrderPlacementState()
    data class Error(val message: String) : OrderPlacementState()
}

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartOrderRepository: CartOrderRepository
) : ViewModel() {

    val cartItems = cartOrderRepository.cartItemsUiState
    val totalPriceCents = cartOrderRepository.totalPriceCents
    val currentVenueId = cartOrderRepository.currentVenueId

    private val _orderPlacementState =
        MutableStateFlow<OrderPlacementState>(OrderPlacementState.Idle)
    val orderPlacementState: StateFlow<OrderPlacementState> = _orderPlacementState

    fun placeOrder() {
        viewModelScope.launch {

            _orderPlacementState.value = OrderPlacementState.Loading

            val (venueId, selectedItems) = cartOrderRepository.getSelectedItemsForOrder()
            if (venueId == null || selectedItems.isEmpty()) {
                _orderPlacementState.value = OrderPlacementState.Error("No items in cart")
                return@launch
            }

            val orderItems: List<OrderItemRequest> = selectedItems.map { (itemId, qty) ->
                OrderItemRequest(itemId, qty)
            }

            val orderRequest = OrderRequest(venueId, orderItems)
            val placeOrderResult = cartOrderRepository.placeOrder(orderRequest)
            placeOrderResult.onSuccess {
                _orderPlacementState.value = OrderPlacementState.Success(it)
            }.onFailure {
                _orderPlacementState.value = OrderPlacementState.Error(it.message.toString())
            }
        }
    }
}