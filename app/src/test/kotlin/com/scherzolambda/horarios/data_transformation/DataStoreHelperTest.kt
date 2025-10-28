package com.scherzolambda.horarios.data_transformation

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import java.io.File

class DataStoreHelperTest {

    @Test
    fun `data store helper grava e le valores corretamente`() = runBlocking {
        val testDir = File("build/tmp/datastore_test_ds")
        testDir.mkdirs()
        val dataStore = PreferenceDataStoreFactory.create { testDir.resolve("prefs.preferences_pb") }

        // Inicializa o helper com o DataStore de teste
        DataStoreHelper.initialize(dataStore)

        // Usa chamadas suspend para gravar
        DataStoreHelper.setFirstAccess(false)
        DataStoreHelper.setAccessToken("token_abc")
        DataStoreHelper.setTheme("LIGHT")
        DataStoreHelper.setShowEmptyWeeklyCell(true)
        DataStoreHelper.setShowEmptyDailyCell(false)
        DataStoreHelper.setFileLoaded(true)

        // Lê os valores e verifica
        val firstAccess = DataStoreHelper.isFirstAccessFlow().first()
        val token = DataStoreHelper.getAccessTokenFlow().first()
        val theme = DataStoreHelper.getThemeFlow().first()
        val weekly = DataStoreHelper.getShowEmptyWeeklyCellFlow().first()
        val daily = DataStoreHelper.getShowEmptyDailyCellFlow().first()
        val fileLoaded = DataStoreHelper.isFileLoadedFlow().first()

        assertEquals(false, firstAccess)
        assertEquals("token_abc", token)
        assertEquals("LIGHT", theme)
        assertEquals(true, weekly)
        assertEquals(false, daily)
        assertEquals(true, fileLoaded)
    }
}
