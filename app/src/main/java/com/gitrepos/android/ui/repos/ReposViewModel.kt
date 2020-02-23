package com.gitrepos.android.ui.repos

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gitrepos.android.data.repositories.DatabaseRepository
import com.gitrepos.android.ui.home.model.RepoItem
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ReposViewModel(private val dbRepository: DatabaseRepository) : ViewModel() {

    private val reposMutableLiveData = MutableLiveData<List<RepoItem>>()
    val reposLiveData: LiveData<List<RepoItem>> = reposMutableLiveData

    /**
     * Fetch list of repositories from network
     */
    fun fetchRepositories() = viewModelScope.launch {
        val repos = dbRepository.fetchSavedRepos()
        repos.collect {
            Log.d("TAG", "fetchRepositories :$it")
            reposMutableLiveData.value = it
        }
    }
}