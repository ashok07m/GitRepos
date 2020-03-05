package com.gitrepos.android.ui.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gitrepos.android.data.auth.BioAuthManager
import com.gitrepos.android.data.database.entity.ReposEntity
import com.gitrepos.android.data.repositories.DatabaseRepository
import com.gitrepos.android.data.repositories.GitRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetailsViewModel(
    private val gitRepository: GitRepository,
    private val dbRepository: DatabaseRepository
) : ViewModel() {

    private var langMutableLiveData = MutableLiveData<String>()
    val langLiveData: LiveData<String> = langMutableLiveData

    /**
     * Fetches language from github for the specified repo
     */
    fun fetchLanguage(owner: String, repo: String) = viewModelScope.launch {
        val language = gitRepository.fetchGitLanguage(owner, repo)
        langMutableLiveData.value = language
    }

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
            avatarUrl = bioAuthManager.encryptData(avatarUrl) ?: avatarUrl
            owner = bioAuthManager.encryptData(owner) ?: owner
            title = bioAuthManager.encryptData(title) ?: title
            description = description?.let { bioAuthManager.encryptData(it) } ?: description
            language = language?.let { bioAuthManager.encryptData(it) } ?: language
        }
    }


}
