package com.gitrepos.android.data.repositories

import androidx.lifecycle.LiveData
import com.gitrepos.android.data.network.model.GitRepositories
import com.gitrepos.android.internal.GitApiResponse

interface GitRepository {
    suspend fun getPublicGitRepos(): GitApiResponse
}