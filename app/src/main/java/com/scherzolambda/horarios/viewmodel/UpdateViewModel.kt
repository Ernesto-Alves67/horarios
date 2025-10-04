package com.scherzolambda.horarios.viewmodel

import android.util.Log
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

    var latestVersion by mutableStateOf<String?>(null)
        private set

    var downloadUrl by mutableStateOf<String?>(null)
        private set

    init {
        checkForUpdate()
    }

    fun checkForUpdate() {
        viewModelScope.launch {
            try {
                val release = api.api.getLatestRelease()
                val currentVersion = "v${BuildConfig.VERSION_NAME}"
                Log.d("UpdateCheck", "Current version: $currentVersion, Latest version: ${release.tagName}")
                Log.d("UpdateCheck", "Release details: $release")
                if (release.tagName != currentVersion) {
                    latestVersion = release.tagName
                    downloadUrl = release.assets?.firstOrNull()?.downloadUrl
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
