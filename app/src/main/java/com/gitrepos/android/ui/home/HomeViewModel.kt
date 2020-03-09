package com.gitrepos.android.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gitrepos.android.R
import com.gitrepos.android.data.repositories.GitRepository
import com.gitrepos.android.data.source.git.GitResult
import com.gitrepos.android.ui.home.model.RepoItem
import kotlinx.coroutines.launch

class HomeViewModel(
    private val gitRepository: GitRepository

) : ViewModel() {

    private val successMutableLiveData = MutableLiveData<List<RepoItem>>()
    val successLiveData: LiveData<List<RepoItem>> = successMutableLiveData

    private val _errorMutableLiveData = MutableLiveData<Int>()
    val errorLiveData: LiveData<Int> = _errorMutableLiveData

    private var lastSearchedQuery: String = DEFAULT_QUERY

    init {
        gitRepository.successLiveData.observeForever {
            successMutableLiveData.value = it
        }

        gitRepository.errorLiveData.observeForever {
            if (it == GitResult.Error.ErrorCodes.NoConnectivityError) {
                _errorMutableLiveData.value = R.string.message_internet_unavailable
            } else {
                _errorMutableLiveData.value = R.string.message_error_fetching_data
            }
        }

        // load default repos
        searchRepositories(DEFAULT_QUERY)
    }

    /**
     * Fetch list of repositories from network
     */
    fun searchRepositories(queryString: String) = viewModelScope.launch {
        lastSearchedQuery = queryString
        gitRepository.searchPublicGitRepos(queryString)
    }

    fun listScrolled(visibleItemCount: Int, lastVisibleItemPosition: Int, totalItemCount: Int) {
        val count = visibleItemCount + lastVisibleItemPosition + VISIBLE_THRESHOLD
        if (count >= totalItemCount) {
            viewModelScope.launch {
                gitRepository.loadMoreGitRepos(lastSearchedQuery)
            }
        }
    }

    companion object {
        const val TAG = "HomeViewModel"
        const val VISIBLE_THRESHOLD = 7
        const val DEFAULT_QUERY = "Android"
    }
}