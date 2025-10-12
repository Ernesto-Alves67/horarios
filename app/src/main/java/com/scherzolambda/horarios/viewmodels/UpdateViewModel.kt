package com.scherzolambda.horarios.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scherzolambda.horarios.BuildConfig
import com.scherzolambda.horarios.data_transformation.api.services.GitHubService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdateViewModel @Inject constructor(
    private val api: GitHubService
) : ViewModel() {

    var updateInfo by mutableStateOf(AppUpdateInfo())
        private set

    init {
        checkForUpdate()
    }

    fun checkForUpdate() {
        viewModelScope.launch {
            try {
                val release = api.api.getLatestRelease()
                val currentVersion = BuildConfig.VERSION_NAME
                if (release.tagName != currentVersion) {
                    updateInfo.latestVersion = release.tagName
                    updateInfo.downloadUrl = release.assets?.firstOrNull()?.downloadUrl.toString()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

data class AppUpdateInfo(
    var latestVersion: String ="",
    var downloadUrl: String= ""
)
