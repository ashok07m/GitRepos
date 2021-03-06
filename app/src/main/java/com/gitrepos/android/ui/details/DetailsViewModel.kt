package com.gitrepos.android.ui.details

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gitrepos.android.RepoApp
import com.gitrepos.android.data.auth.BioAuthManager
import com.gitrepos.android.data.database.entity.ReposEntity
import com.gitrepos.android.data.repositories.DatabaseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetailsViewModel(
    private val appContext: Context,
    private val dbRepository: DatabaseRepository
) : ViewModel() {

    /**
     * Save repos details in database
     */
    fun saveRepoDetails(
        reposEntity: ReposEntity,
        bioAuthManager: BioAuthManager
    ): LiveData<Boolean> {
        val saveResult = MutableLiveData<Boolean>()
        viewModelScope.launch {
            val encryptedRepo = encryptRepositoryDetails(reposEntity, bioAuthManager)
            saveResult.value = dbRepository.saveRepo(encryptedRepo)
            (appContext.applicationContext as RepoApp).setAlarm()
        }
        return saveResult
    }

    /**
     * Encrypts repository info
     */
    private suspend fun encryptRepositoryDetails(
        reposEntity: ReposEntity,
        bioAuthManager: BioAuthManager
    ): ReposEntity = withContext(Dispatchers.Default) {
        reposEntity.apply {
            name = bioAuthManager.encryptData(name) ?: name
            fullName = bioAuthManager.encryptData(fullName) ?: fullName
            starsCount = bioAuthManager.encryptData(starsCount) ?: starsCount
            forksCount = bioAuthManager.encryptData(forksCount) ?: forksCount
            description = description?.let { bioAuthManager.encryptData(it) } ?: description
            language = language?.let { bioAuthManager.encryptData(it) } ?: language
            homepage = bioAuthManager.encryptData(homepage) ?: homepage
        }
    }


}
