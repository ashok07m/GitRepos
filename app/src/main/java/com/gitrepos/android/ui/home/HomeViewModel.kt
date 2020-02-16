package com.gitrepos.android.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gitrepos.android.data.repositories.GitRepository
import com.gitrepos.android.internal.GitApiResponse
import com.gitrepos.android.ui.home.model.RepoItem
import kotlinx.coroutines.launch
import org.koin.core.context.GlobalContext.get

class HomeViewModel(private val gitRepository: GitRepository) : ViewModel() {

    private val _gitReposMutableLiveData = MutableLiveData<List<RepoItem>>()
    val gitReposLiveData: LiveData<List<RepoItem>> = _gitReposMutableLiveData

    /**
     * Fetch list of repositories from network
     */
    fun fetchRepositories() = viewModelScope.launch {
        when (val response = gitRepository.getPublicGitRepos()) {
            is GitApiResponse.Success -> {
                Log.d(TAG, "success : ${response.repositories}")
            }
            is GitApiResponse.Error -> {
                Log.d(TAG, "Error : ${response.errorCode}")
            }
        }
    }

    companion object {
        const val TAG = "HomeViewModel"
    }
}