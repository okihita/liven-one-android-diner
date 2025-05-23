package com.liven.diner.ui.screen.order.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.liven.diner.data.model.order.Order
import com.liven.diner.data.repository.CartOrderRepository
import com.liven.diner.ui.navigation.NAV_ARG_ORDER_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OrderDetailScreenState(
    val isLoading: Boolean = false,
    val order: Order? = null,
    val error: String? = null
)

@HiltViewModel
class OrderDetailViewModel @Inject constructor(
    private val cartOrderRepository: CartOrderRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Get the orderId from navigation argument via SavedStateHandle
    private val orderId: Long = checkNotNull(savedStateHandle[NAV_ARG_ORDER_ID])

    private val _uiState = MutableStateFlow(OrderDetailScreenState())
    val uiState = _uiState.asStateFlow()

    init {
        getOrderDetail()
    }

    fun getOrderDetail() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            cartOrderRepository.getDinerSpecificOrder(orderId)
                .onSuccess { order ->
                    _uiState.update { it.copy(isLoading = false, order = order) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }
}