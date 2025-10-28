package com.scherzolambda.horarios.viewmodel

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.advanceUntilIdle
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import java.io.File
import com.scherzolambda.horarios.data_transformation.DataStoreHelper
import com.scherzolambda.horarios.testutils.MainDispatcherRule

class ConfigViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `config viewmodel consome valores do datastore`() = runBlocking {
        val testDir = File("build/tmp/datastore_test_config")
        testDir.mkdirs()
        val dataStore = PreferenceDataStoreFactory.create { testDir.resolve("prefs.preferences_pb") }
        DataStoreHelper.initialize(dataStore)

        // Grava valores antes de instanciar a ViewModel
        DataStoreHelper.setShowEmptyWeeklyCell(true)
        DataStoreHelper.setShowEmptyDailyCell(true)

        // Usa o scope do MainDispatcherRule como externalScope para garantir coleta imediata
        val vm = ConfigViewModel(externalScope = mainDispatcherRule.testCoroutineScope)

        // Aguarda o TestScope processar as coroutines de coleta
        mainDispatcherRule.testScope.advanceUntilIdle()

        // Verifica que os estados foram atualizados com os valores do DataStore
        assertEquals(true, vm.showEmptyWeeklyCell.value)
        assertEquals(true, vm.showEmptyDailyCell.value)
    }
}
