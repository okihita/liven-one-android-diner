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
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface GetVenueDetailResult {
    object Idle : GetVenueDetailResult
    object Loading : GetVenueDetailResult
    data class Success(val menuItems: List<MenuItem>) : GetVenueDetailResult
    data class Error(val message: String) : GetVenueDetailResult
}

@HiltViewModel
class VenueDetailViewModel @Inject constructor(
    private val venuesMenuItemsRepository: VenuesMenuItemsRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val venueId: Long = checkNotNull(savedStateHandle[NAV_ARG_VENUE_ID])

    private val _getMenuByVenueIdResult =
        MutableStateFlow<GetVenueDetailResult>(GetVenueDetailResult.Idle)
    val getMenuByVenueIdResult: StateFlow<GetVenueDetailResult> =
        _getMenuByVenueIdResult.asStateFlow()

    fun getVenueDetail() {
        viewModelScope.launch {
            _getMenuByVenueIdResult.value = GetVenueDetailResult.Loading

            val getSingleVenueMenusResult = venuesMenuItemsRepository.getSingleVenueMenus(venueId)

            getSingleVenueMenusResult.onSuccess {
                _getMenuByVenueIdResult.value = GetVenueDetailResult.Success(it)
            }.onFailure {
                _getMenuByVenueIdResult.value = GetVenueDetailResult.Error(it.message.toString())
            }

        }
    }
}