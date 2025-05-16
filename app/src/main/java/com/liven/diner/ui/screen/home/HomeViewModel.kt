package com.liven.diner.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.liven.diner.data.model.venue.Venue
import com.liven.diner.data.repository.VenuesMenuItemsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    private val venuesMenuItemsRepository: VenuesMenuItemsRepository
) : ViewModel() {

    private val _venuesResult = MutableStateFlow<GetVenuesResult>(GetVenuesResult.Idle)
    val venuesResult: StateFlow<GetVenuesResult> = _venuesResult.asStateFlow()

    init {
        getVenues()
    }

    fun getVenues(){
        _venuesResult.value = GetVenuesResult.Loading

        viewModelScope.launch {
            val getVenuesResult = venuesMenuItemsRepository.getVenues()
            getVenuesResult.onSuccess {
                _venuesResult.value = GetVenuesResult.Success(it.venues)
            }.onFailure {
                _venuesResult.value = GetVenuesResult.Error(it.message.toString())
            }
        }
    }
}