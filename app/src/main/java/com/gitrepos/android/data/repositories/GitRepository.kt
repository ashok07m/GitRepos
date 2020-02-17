package com.gitrepos.android.data.repositories

import androidx.lifecycle.LiveData
import com.gitrepos.android.data.network.model.GitRepositories
import com.gitrepos.android.internal.GitApiResponse
import com.gitrepos.android.ui.home.model.RepoItem

interface GitRepository {
    val successLiveData: LiveData<List<RepoItem>>
    val errorLiveData: LiveData<GitApiResponse.Error.ErrorCodes>
    suspend fun getPublicGitRepos()
}