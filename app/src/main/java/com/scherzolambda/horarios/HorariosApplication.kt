package com.scherzolambda.horarios


import android.app.Application
import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.scherzolambda.horarios.data_transformation.DataStoreHelper
import com.scherzolambda.horarios.data_transformation.EnvConfig
import dagger.hilt.android.HiltAndroidApp

private val Context.dataStore by preferencesDataStore(name = "app_preferences")

@HiltAndroidApp
class HorariosApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Inicializa o DataStoreHelper com o DataStore antes de qualquer requisição
        EnvConfig.load(this)
        DataStoreHelper.initialize(applicationContext.dataStore)
    }
}