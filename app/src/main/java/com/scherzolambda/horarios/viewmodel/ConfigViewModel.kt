package com.scherzolambda.horarios.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scherzolambda.horarios.data_transformation.DataStoreHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
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

    // Será inicializado no init com viewModelScope, ou pela secondary constructor em testes
    private lateinit var collectionScope: CoroutineScope

    init {
        collectionScope = viewModelScope
        DataStoreHelper.getShowEmptyWeeklyCellFlow()
            .onEach { _showEmptyWeeklyCell.value = it }
            .launchIn(collectionScope)

        DataStoreHelper.getShowEmptyDailyCellFlow()
            .onEach { _showEmptyDailyCell.value = it }
            .launchIn(collectionScope)
    }

    // Secondary constructor para testes
    constructor(externalScope: CoroutineScope) : this() {
        collectionScope = externalScope
        // Relaunch collectors usando o externalScope
        DataStoreHelper.getShowEmptyWeeklyCellFlow()
            .onEach { _showEmptyWeeklyCell.value = it }
            .launchIn(collectionScope)

        DataStoreHelper.getShowEmptyDailyCellFlow()
            .onEach { _showEmptyDailyCell.value = it }
            .launchIn(collectionScope)
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