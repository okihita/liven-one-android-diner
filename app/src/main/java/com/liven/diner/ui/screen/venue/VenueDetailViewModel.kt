package com.liven.diner.ui.screen.venue

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.liven.diner.data.model.order.MenuItem
import com.liven.diner.data.repository.CartOrderRepository
import com.liven.diner.data.repository.VenuesMenuItemsRepository
import com.liven.diner.ui.navigation.NAV_ARG_VENUE_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class VenueMenuScreenState(
    val isLoadingMenu: Boolean = true,
    val menuItems: List<MenuItem> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class VenueDetailViewModel @Inject constructor(
    private val venuesMenuItemsRepository: VenuesMenuItemsRepository,
    private val cartOrderRepository: CartOrderRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val venueId: Long = checkNotNull(savedStateHandle[NAV_ARG_VENUE_ID])

    private val _screenState = MutableStateFlow(VenueMenuScreenState())
    val screenState: StateFlow<VenueMenuScreenState> = _screenState.asStateFlow()

    val cartItemsUiState = cartOrderRepository.cartItemsUiState
    val totalPriceCents = cartOrderRepository.totalPriceCents

    init {
        getVenueDetail()
    }

    fun getVenueDetail() {
        viewModelScope.launch {
            _screenState.update { it.copy(isLoadingMenu = true, error = null) }
            val getSingleVenueMenusResult = venuesMenuItemsRepository.getSingleVenueMenus(venueId)

            getSingleVenueMenusResult.onSuccess { fetchedMenuItems ->

                // Update Venue context
                cartOrderRepository.setVenueContext(venueId, fetchedMenuItems)

                _screenState.update { currentState ->
                    currentState.copy(
                        isLoadingMenu = false,
                        menuItems = fetchedMenuItems,
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

    fun incrementItemQty(itemId: Long) = cartOrderRepository.incrementItemQty(itemId)

    fun decrementItemQty(itemId: Long) = cartOrderRepository.decrementItemQty(itemId)

    fun clearError() = _screenState.update { it.copy(error = null) }
}