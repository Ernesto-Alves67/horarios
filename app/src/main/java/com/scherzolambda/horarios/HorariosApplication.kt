package com.scherzolambda.horarios


import android.app.Application
import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.scherzolambda.horarios.data_transformation.DataStoreHelper
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class HorariosApplication : Application() {
    private val Context.dataStore by preferencesDataStore(name = "app_preferences")

    override fun onCreate() {
        super.onCreate()
        // Inicializa o DataStoreHelper com o DataStore antes de qualquer requisição
        DataStoreHelper.initialize(this.dataStore)
    }
}