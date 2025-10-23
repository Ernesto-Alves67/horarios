package com.scherzolambda.horarios.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scherzolambda.horarios.data_transformation.DataStoreHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConfigViewModel @Inject constructor() : ViewModel() {
    private val _showEmptyWeeklyCell = MutableStateFlow(false)
    val showEmptyWeeklyCell: StateFlow<Boolean> = _showEmptyWeeklyCell
    private val _showEmptyDailyCell = MutableStateFlow(false)
    val showEmptyDailyCell: StateFlow<Boolean> = _showEmptyDailyCell

    init {
        DataStoreHelper.getShowEmptyWeeklyCellFlow()
            .onEach { _showEmptyWeeklyCell.value = it }
            .launchIn(viewModelScope)

        DataStoreHelper.getShowEmptyDailyCellFlow()
            .onEach { _showEmptyDailyCell.value = it }
            .launchIn(viewModelScope)
    }
    fun setShowEmptyWeeklyCell(show: Boolean) {
        _showEmptyWeeklyCell.value = show
        viewModelScope.launch {
            DataStoreHelper.setShowEmptyWeeklyCell(show)
        }
    }
    fun setShowEmptyDailyCell(show: Boolean) {
        _showEmptyDailyCell.value = show
        viewModelScope.launch {
            DataStoreHelper.setShowEmptyDailyCell(show)
        }
    }

}