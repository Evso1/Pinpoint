package com.burland.pinpoint

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.burland.pinpoint.data.GeocoderRepository
import com.burland.pinpoint.domain.LocationModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Represents the transient state of the UI
data class MainUiState(
    val query: String = "",
    val results: List<LocationModel> = emptyList(),
    val selectedResult: LocationModel? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class MainViewModel(
    private val repository: GeocoderRepository = GeocoderRepository() // Manual DI
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    fun onQueryChange(newQuery: String) {
        _uiState.value = _uiState.value.copy(query = newQuery, error = null)
    }

    fun searchLocation() {
        val query = _uiState.value.query.takeIf { it.isNotBlank() } ?: return
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, results = emptyList(), selectedResult = null)
            val locations = repository.getCoordinates(query)
            if (locations.isNotEmpty()) {
                // If only 1 result, auto-select it. Otherwise show list.
                val selected = if (locations.size == 1) locations.first() else null
                _uiState.value = _uiState.value.copy(isLoading = false, results = locations, selectedResult = selected)
            } else {
                _uiState.value = _uiState.value.copy(isLoading = false, error = "Location not found")
            }
        }
    }

    fun selectLocation(location: LocationModel) {
        _uiState.value = _uiState.value.copy(selectedResult = location)
    }

    fun clearSelection() {
        _uiState.value = _uiState.value.copy(selectedResult = null)
    }

    // The Kill Switch function
    fun clearAll() {
        _uiState.value = MainUiState() // Reset to initial empty state
    }
}
