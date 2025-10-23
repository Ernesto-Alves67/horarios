package com.scherzolambda.horarios.ui.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scherzolambda.horarios.data_transformation.DataStoreHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch


class ThemeViewModel : ViewModel() {
    private val _theme = MutableStateFlow(AppTheme.SYSTEM)
    val theme: StateFlow<AppTheme> = _theme.asStateFlow()

    init {
        // Observa o DataStore e atualiza o tema ao inicializar
        DataStoreHelper.getThemeFlow()
            .onEach { themeName ->
                themeName?.let {
                    _theme.value = try {
                        AppTheme.valueOf(it)
                    } catch (e: Exception) {
                        AppTheme.SYSTEM
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    fun setTheme(theme: AppTheme) {
        _theme.value = theme
        viewModelScope.launch {
            DataStoreHelper.setTheme(theme.name)
        }
    }
}

// Enum para os temas disponíveis
enum class AppTheme {
    LIGHT, DARK, SYSTEM
}
