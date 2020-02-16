package com.gitrepos.android.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.gitrepos.android.data.network.model.GitRepositories
import com.gitrepos.android.data.network.source.GitReposDataSource
import com.gitrepos.android.internal.GitApiResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GitRepositoryImpl(private val gitReposDataSource: GitReposDataSource) : GitRepository {

    private var lastVisitedIndex = 0

    override suspend fun getPublicGitRepos(): GitApiResponse = withContext(Dispatchers.IO) {
        gitReposDataSource.getPublicGitRepos(lastVisitedIndex)
    }

    /**
     * Reset last visited index to initial value
     */
    fun resetLastVisitedIndex() {
        lastVisitedIndex = 0
    }

}