package com.gitrepos.android.data.repositories

import com.gitrepos.android.data.database.entity.GitItem

interface GitCacheDbRepository {
    suspend fun saveGitRepos(gitItemsList: List<GitItem>)
    suspend fun fetchCachedRepos(queryText: String): List<GitItem>
    suspend fun deleteAllData()
}