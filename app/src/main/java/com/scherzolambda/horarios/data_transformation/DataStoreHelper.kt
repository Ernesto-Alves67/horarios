package com.scherzolambda.horarios.data_transformation

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "app_preferences")

object DataStoreHelper {
//    private val FIRST_ACCESS_KEY = booleanPreferencesKey("is_first_access")
    private val FILE_LOADED_KEY = booleanPreferencesKey("is_file_loaded")

//    fun isFirstAccessFlow(context: Context): Flow<Boolean> =
//        context.dataStore.data.map { it[FIRST_ACCESS_KEY] ?: true }
//
//    suspend fun setFirstAccess(context: Context, value: Boolean) {
//        context.dataStore.edit { prefs ->
//            prefs[FIRST_ACCESS_KEY] = value
//        }
//    }

    fun isFileLoadedFlow(context: Context): Flow<Boolean> =
        context.dataStore.data.map { it[FILE_LOADED_KEY] ?: false }

    suspend fun setFileLoaded(context: Context, value: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[FILE_LOADED_KEY] = value
        }
    }
}

