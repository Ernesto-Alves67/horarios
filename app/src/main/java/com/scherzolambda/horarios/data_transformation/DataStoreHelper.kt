package com.scherzolambda.horarios.data_transformation

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object DataStoreHelper {
    private lateinit var dataStore: DataStore<Preferences>

    private val FILE_LOADED_KEY = booleanPreferencesKey("is_file_loaded")
    private val FISRT_ACCESS_KEY = booleanPreferencesKey("if_first_access")
    private val ACCESS_TOKEN_KEY = stringPreferencesKey("acess_token")
    private val THEME_KEY = stringPreferencesKey("theme")
    private val WEEK_UI_KRY = booleanPreferencesKey("show_empty_weekly_cell")
    private val DAY_UI_KEY = booleanPreferencesKey("show_empty_daily_cell")

    // Função para inicializar o DataStore
    fun initialize(dataStore: DataStore<Preferences>) {
        this.dataStore = dataStore
    }

    fun isFileLoadedFlow(): Flow<Boolean> =
        dataStore.data.map { it[FILE_LOADED_KEY] ?: false }

    suspend fun setFileLoaded(value: Boolean) {
        dataStore.edit { prefs ->
            prefs[FILE_LOADED_KEY] = value
        }
    }

    fun getAccessTokenFlow(): Flow<String?> =
        dataStore.data.map { it[ACCESS_TOKEN_KEY] }

    suspend fun setAccessToken(value: String) {
        dataStore.edit { prefs ->
            prefs[ACCESS_TOKEN_KEY] = value
        }
    }
    fun isFirstAccessFlow(): Flow<Boolean> =
        dataStore.data.map { it[FISRT_ACCESS_KEY] ?: true }

    /** Define se é o primeiro acesso do usuário
     *  Quando True, registra dados. Quando False, atualiza.
     * */
    suspend fun setFirstAccess(value: Boolean) {
        dataStore.edit { prefs ->
            prefs[FISRT_ACCESS_KEY] = value}
    }

    fun getThemeFlow(): Flow<String?> =
        dataStore.data.map { it[THEME_KEY] }

    suspend fun setTheme(value: String) {
        dataStore.edit { prefs ->
            prefs[THEME_KEY] = value
        }
    }

    fun getShowEmptyWeeklyCellFlow(): Flow<Boolean> =
        dataStore.data.map { it[WEEK_UI_KRY] ?: false }

    suspend fun setShowEmptyWeeklyCell(value: Boolean) {
        dataStore.edit { prefs ->
            prefs[WEEK_UI_KRY] = value
        }
    }
    fun getShowEmptyDailyCellFlow(): Flow<Boolean> =
        dataStore.data.map { it[DAY_UI_KEY] ?: false }
    suspend fun setShowEmptyDailyCell(value: Boolean) {
        dataStore.edit { prefs ->
            prefs[DAY_UI_KEY] = value}
    }
}
