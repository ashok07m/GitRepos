package com.gitrepos.android.ui.repos

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gitrepos.android.data.auth.BioAuthManager
import com.gitrepos.android.data.repositories.DatabaseRepository
import com.gitrepos.android.ui.home.model.RepoItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ReposViewModel(private val dbRepository: DatabaseRepository) : ViewModel() {

    private val reposMutableLiveData = MutableLiveData<List<RepoItem>>()
    val reposLiveData: LiveData<List<RepoItem>> = reposMutableLiveData

    /**
     * Fetch list of repositories from network
     */
    fun fetchRepositories(bioAuthManager: BioAuthManager) = viewModelScope.launch {
        val repos = dbRepository.fetchSavedRepos()
        repos.collect {

            it.map { repoItem ->
                decryptRepositoryDetails(repoItem, bioAuthManager)
            }

            Log.d("TAG", "fetchRepositories :$it")
            reposMutableLiveData.value = it
        }
    }

    /**
     * Decrypts repository info
     */
    private suspend fun decryptRepositoryDetails(
        repoItem: RepoItem,
        bioAuthManager: BioAuthManager
    ): RepoItem = withContext(Dispatchers.Default) {
        repoItem.repo.apply {
            avatarUrl = bioAuthManager.decryptData(avatarUrl) ?: avatarUrl
            owner = bioAuthManager.decryptData(owner) ?: owner
            title = bioAuthManager.decryptData(title) ?: title
            description = description?.let { bioAuthManager.decryptData(it) } ?: description
            language = language?.let { bioAuthManager.decryptData(it) } ?: language
            fullName = "$owner/$title"
        }
        RepoItem(repoItem.repo)
    }
}