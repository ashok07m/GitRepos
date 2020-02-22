package com.gitrepos.android.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gitrepos.android.R
import com.gitrepos.android.data.network.source.git.GitResult
import com.gitrepos.android.data.repositories.GitRepository
import com.gitrepos.android.ui.home.model.RepoItem
import kotlinx.coroutines.launch

class HomeViewModel(private val gitRepository: GitRepository) : ViewModel() {

    private val _successMutableLiveData = MutableLiveData<List<RepoItem>>()
    val successLiveData: LiveData<List<RepoItem>> = _successMutableLiveData

    private val _errorMutableLiveData = MutableLiveData<Int>()
    val errorLiveData: LiveData<Int> = _errorMutableLiveData

    init {
        gitRepository.successLiveData.observeForever {
            _successMutableLiveData.value = it
        }

        gitRepository.errorLiveData.observeForever {
            if (it == GitResult.Error.ErrorCodes.NoConnectivityError) {
                _errorMutableLiveData.value = R.string.message_internet_unavailable
            } else {
                _errorMutableLiveData.value = R.string.message_error_fetching_data
            }
        }
    }

    /**
     * Fetch list of repositories from network
     */
    fun fetchRepositories() = viewModelScope.launch {
        gitRepository.getPublicGitRepos()
    }

    companion object {
        const val TAG = "HomeViewModel"
    }
}