package com.gitrepos.android.data.source.git

/**
 * Provides methods to interact with network
 */
interface GitReposDataSource {
    suspend fun searchGitRepos(query: String, pageIndex: Int = 1): GitResult
    suspend fun loadMoreGitRepos(queryString: String): GitResult
}