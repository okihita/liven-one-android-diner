package com.liven.diner.data.repository

import com.liven.diner.data.model.order.MenuItem
import com.liven.diner.data.model.order.OrderRequest
import com.liven.diner.data.model.order.OrderResponse
import com.liven.diner.data.remote.LivenOneApi
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.Eagerly
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

interface CartOrderRepository {
    val cartItemsUiState: StateFlow<List<CartItemUiState>>
    val totalPriceCents: StateFlow<Int>
    val currentVenueId: StateFlow<Long?>
    fun setVenueContext(venueId: Long, menuItemsForVenue: List<MenuItem>)
    fun incrementItemQty(itemId: Long)
    fun decrementItemQty(itemId: Long)
    fun getSelectedItemsForOrder(): Pair<Long?, Map<Long, Int>>
    fun clearCart()

    suspend fun placeOrder(request: OrderRequest): Result<OrderResponse>
}

data class CartItemUiState(val menuItem: MenuItem, val quantity: Int)

@Singleton
class CartOrderRepositoryImpl @Inject constructor(
    private val livenOneApi: LivenOneApi,
) : CartOrderRepository {

    private data class CartState(
        val venueId: Long? = null,
        val selectedItems: Map<Long, Int> = emptyMap(), // MenuItem.id to quantity
        val allMenuItemsForCurrentVenue: List<MenuItem> = emptyList()
    )

    private val _cartState = MutableStateFlow(CartState())

    @OptIn(DelicateCoroutinesApi::class)
    override val cartItemsUiState: StateFlow<List<CartItemUiState>> = _cartState.map { state ->
        state.selectedItems.mapNotNull { (itemId, qty) ->
            state.allMenuItemsForCurrentVenue.find { it.id == itemId }?.let { menuItem ->
                CartItemUiState(menuItem, qty)
            }
        }
    }.stateIn(GlobalScope, Eagerly, emptyList())

    @OptIn(DelicateCoroutinesApi::class)
    override val totalPriceCents: StateFlow<Int> = _cartState.map {
        it.selectedItems.entries.sumOf { (itemId, qty) ->
            val price = it.allMenuItemsForCurrentVenue.find { it.id == itemId }?.priceInCents ?: 0
            price.times(qty)
        }
    }.stateIn(GlobalScope, Eagerly, 0)

    @OptIn(DelicateCoroutinesApi::class)
    override val currentVenueId = _cartState.map { it.venueId }.stateIn(GlobalScope, Eagerly, null)

    override fun setVenueContext(venueId: Long, menuItemsForVenue: List<MenuItem>) =
        // If changing venue, clear the cart
        if (_cartState.value.venueId != venueId) {
            _cartState.value = CartState(
                venueId = venueId,
                selectedItems = emptyMap(),
                allMenuItemsForCurrentVenue = menuItemsForVenue
            )
        }
        // Else just update the menu items list if it's the same venue e.g. refresh
        else {
            _cartState.value = _cartState.value.copy(
                allMenuItemsForCurrentVenue = menuItemsForVenue
            )
        }

    override fun incrementItemQty(itemId: Long) {
        _cartState.update { state ->

            // If item ID isn't found in the current venue, return the original state
            if (state.allMenuItemsForCurrentVenue.none { it.id == itemId })
                state

            val currentQty = state.selectedItems[itemId] ?: 0
            val newSelectedItems = state.selectedItems.toMutableMap().apply {
                put(itemId, currentQty + 1)
            }

            state.copy(selectedItems = newSelectedItems)
        }
    }

    override fun decrementItemQty(itemId: Long) {
        _cartState.update { state ->
            val currentQty = state.selectedItems[itemId] ?: 0
            if (currentQty <= 0) state

            val newSelectedItems = state.selectedItems.toMutableMap().apply {
                if (currentQty <= 1) remove(itemId)
                else put(itemId, currentQty - 1)
            }
            state.copy(selectedItems = newSelectedItems)
        }
    }

    override fun getSelectedItemsForOrder() =
        Pair(_cartState.value.venueId, _cartState.value.selectedItems.toMap())

    override fun clearCart() {
        _cartState.value = CartState()
    }

    override suspend fun placeOrder(request: OrderRequest) = try {
        val result = livenOneApi.postOrder(request)
        Result.success(result)
    } catch (e: Exception) {
        Result.failure(e)
    }
}