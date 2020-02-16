package com.gitrepos.android.data.network.source

import com.gitrepos.android.data.network.model.GitRepositories
import com.gitrepos.android.internal.GitApiResponse

/**
 * Provides methods to interact with network
 */
interface GitReposDataSource {

    suspend fun getPublicGitRepos(lastVisitedIndex: Int): GitApiResponse

}