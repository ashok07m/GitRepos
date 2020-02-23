package com.gitrepos.android.ui.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gitrepos.android.data.database.entity.ReposEntity
import com.gitrepos.android.data.repositories.DatabaseRepository
import com.gitrepos.android.data.repositories.GitRepository
import kotlinx.coroutines.launch

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
    fun saveRepoDetails(reposEntity: ReposEntity): LiveData<Boolean> {
        val saveResult = MutableLiveData<Boolean>()
        viewModelScope.launch {
            saveResult.value = dbRepository.saveRepo(reposEntity)
        }
        return saveResult
    }

}
