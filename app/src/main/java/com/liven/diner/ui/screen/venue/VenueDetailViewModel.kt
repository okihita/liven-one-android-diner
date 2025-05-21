package com.liven.diner.ui.screen.venue

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.liven.diner.data.model.order.MenuItem
import com.liven.diner.data.repository.VenuesMenuItemsRepository
import com.liven.diner.ui.navigation.NAV_ARG_VENUE_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CartItemUiState(
    val menuItem: MenuItem,
    val quantity: Int
)

data class VenueMenuScreenState(
    val isLoadingMenu: Boolean = true,
    val menuItems: List<MenuItem> = emptyList(),
    val selectedItems: Map<Long, Int> = emptyMap(), // MenuItem.id to quantity
    val cartItems: List<CartItemUiState> = emptyList(),
    val totalPriceCents: Int = 0,
    val error: String? = null
)

@HiltViewModel
class VenueDetailViewModel @Inject constructor(
    private val venuesMenuItemsRepository: VenuesMenuItemsRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val venueId: Long = checkNotNull(savedStateHandle[NAV_ARG_VENUE_ID])

    private val _screenState = MutableStateFlow(VenueMenuScreenState())
    val screenState: StateFlow<VenueMenuScreenState> = _screenState.asStateFlow()

    init {
        getVenueDetail()
    }

    fun getVenueDetail() {
        viewModelScope.launch {
            _screenState.update { it.copy(isLoadingMenu = true, error = null) }

            val getSingleVenueMenusResult = venuesMenuItemsRepository.getSingleVenueMenus(venueId)

            getSingleVenueMenusResult.onSuccess { fetchedMenuItems ->
                _screenState.update { currentState ->
                    currentState.copy(
                        isLoadingMenu = false,
                        menuItems = fetchedMenuItems,
                        cartItems = calculateCartItems(
                            fetchedMenuItems,
                            currentState.selectedItems
                        ),
                        totalPriceCents = calculateTotalPriceCents(
                            fetchedMenuItems,
                            currentState.selectedItems
                        )
                    )
                }
            }.onFailure { err ->
                _screenState.update { state ->
                    state.copy(
                        isLoadingMenu = false,
                        error = err.message ?: "Unknown error"
                    )
                }
            }
        }
    }

    fun incrementItemQty(itemId: Long) {
        _screenState.update { state ->
            val currentQty = state.selectedItems[itemId] ?: 0
            val newSelectedItem = state.selectedItems.toMutableMap().apply {
                put(itemId, currentQty + 1)
            }

            state.copy(
                selectedItems = newSelectedItem,
                cartItems = calculateCartItems(state.menuItems, newSelectedItem),
                totalPriceCents = calculateTotalPriceCents(state.menuItems, newSelectedItem)
            )
        }
    }

    fun decrementItemQty(itemId: Long) {
        _screenState.update { state ->
            val currentQty = state.selectedItems[itemId] ?: 0

            if (currentQty > 0) {
                val newSelectedItem = state.selectedItems.toMutableMap().apply {
                    if (currentQty > 1) {
                        put(itemId, currentQty - 1)
                    } else {
                        remove(itemId)
                    }
                }
                state.copy(
                    selectedItems = newSelectedItem,
                    cartItems = calculateCartItems(state.menuItems, newSelectedItem),
                    totalPriceCents = calculateTotalPriceCents(state.menuItems, newSelectedItem)
                )
            } else {
                state
            }
        }
    }

    private fun calculateCartItems(
        menuItems: List<MenuItem>,
        selectedItems: Map<Long, Int>
    ): List<CartItemUiState> = selectedItems.mapNotNull { (itemId, qty) ->
        menuItems.find { menuItem -> menuItem.id == itemId }?.let { menuItem ->
            CartItemUiState(menuItem, qty)
        }
    }


    private fun calculateTotalPriceCents(
        menuItems: List<MenuItem>,
        selectedItems: Map<Long, Int>
    ) = selectedItems.entries.sumOf { (itemId, qty) ->
        val itemPrice = menuItems.find { menuItem -> menuItem.id == itemId }?.priceInCents ?: 0
        itemPrice * qty
    }

    fun clearError() = _screenState.update { it.copy(error = null) }
}