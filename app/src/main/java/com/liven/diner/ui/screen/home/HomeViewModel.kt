package com.liven.diner.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.liven.diner.data.model.venue.Venue
import com.liven.diner.data.repository.AuthRepository
import com.liven.diner.data.repository.VenuesMenuItemsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface GetVenuesResult {
    object Idle : GetVenuesResult
    object Loading : GetVenuesResult
    data class Success(val venues: List<Venue>) : GetVenuesResult
    data class Error(val message: String) : GetVenuesResult
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val venuesMenuItemsRepository: VenuesMenuItemsRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _venuesResult = MutableStateFlow<GetVenuesResult>(GetVenuesResult.Idle)
    val venuesResult: StateFlow<GetVenuesResult> = _venuesResult.asStateFlow()

    // Channel for one-time navigation events like logout
    private val _navigationEvent = Channel<NavigationEvent>()
    val navigationEvent = _navigationEvent.receiveAsFlow()

    init {
        getVenues()
    }

    fun getVenues(){
        _venuesResult.value = GetVenuesResult.Loading

        viewModelScope.launch {
            val getVenuesResult =
                venuesMenuItemsRepository.getVenues(name = null, cuisineType = null)
            getVenuesResult.onSuccess {
                _venuesResult.value = GetVenuesResult.Success(it.venues)
            }.onFailure {
                _venuesResult.value = GetVenuesResult.Error(it.message.toString())
            }
        }
    }

    fun logoutUser() {
        viewModelScope.launch {
            authRepository.logoutUser()
            _navigationEvent.send(NavigationEvent.NavigateToLogin)
        }
    }

    sealed class NavigationEvent {
        object NavigateToLogin : NavigationEvent()
    }
}