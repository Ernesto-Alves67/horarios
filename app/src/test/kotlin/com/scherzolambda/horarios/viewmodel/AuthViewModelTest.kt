package com.scherzolambda.horarios.viewmodel

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import java.io.File
import com.scherzolambda.horarios.data_transformation.DataStoreHelper
import com.scherzolambda.horarios.data_transformation.api.models.responses.AuthResponse
import com.scherzolambda.horarios.data_transformation.api.repositories.IAuthRepository
import com.scherzolambda.horarios.testutils.MainDispatcherRule
import retrofit2.Response

class AuthViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private class FakeSuccessRepository(private val token: String) : IAuthRepository {
        override suspend fun initializeSession(): Response<AuthResponse> {
            return Response.success(AuthResponse(token))
        }
    }

    @Test
    fun `initializeAppSuspend salva token e esconde splash`() = runBlocking {
        // prepara DataStore temporário
        val testDir = File("build/tmp/datastore_test_auth")
        testDir.mkdirs()
        val dataStore = PreferenceDataStoreFactory.create { testDir.resolve("prefs.preferences_pb") }
        DataStoreHelper.initialize(dataStore)

        val token = "tokentest123"
        val repo = FakeSuccessRepository(token)
        val vm = AuthViewModel(repository = repo)

        // chama a versão suspend para executar sincronamente
        vm.initializeAppSuspend()

        val saved = DataStoreHelper.getAccessTokenFlow().first()
        assertEquals(token, saved)
        assertFalse(vm.isSplashVisible.value)
    }
}
