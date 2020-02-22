package com.gitrepos.android.data.network.source.git

/**
 * Provides methods to interact with network
 */
interface GitReposDataSource {

    suspend fun getPublicGitRepos(lastVisitedIndex: Int): GitResult
    suspend fun fetchRepoLanguages(owner: String, repo: String): GitResult


}