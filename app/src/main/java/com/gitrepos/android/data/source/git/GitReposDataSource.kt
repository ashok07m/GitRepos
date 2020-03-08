package com.gitrepos.android.data.source.git

/**
 * Provides methods to interact with network
 */
interface GitReposDataSource {
    suspend fun searchGitRepos(query: String): GitResult
}