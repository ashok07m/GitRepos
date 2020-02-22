package com.gitrepos.android.data.repositories

import androidx.lifecycle.LiveData
import com.gitrepos.android.data.network.source.git.GitResult
import com.gitrepos.android.ui.home.model.RepoItem

interface GitRepository {
    val successLiveData: LiveData<List<RepoItem>>
    val errorLiveData: LiveData<GitResult.Error.ErrorCodes>
    suspend fun getPublicGitRepos()
}