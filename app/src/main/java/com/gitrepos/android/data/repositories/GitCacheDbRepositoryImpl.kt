package com.gitrepos.android.data.repositories

import android.util.Log
import com.gitrepos.android.data.database.dao.GitItemsDao
import com.gitrepos.android.data.database.entity.GitItem
import com.gitrepos.android.ui.home.model.RepoItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GitCacheDbRepositoryImpl(private val gitItemDao: GitItemsDao) : GitCacheDbRepository {

    override suspend fun saveGitRepos(gitItemsList: List<GitItem>) {
        coroutineScope {
            launch(Dispatchers.IO) {
                gitItemDao.keepOnlyLatest50Rows()
                gitItemDao.insert(gitItemsList)
                Log.d("GitCacheDbRepositoryImpl", "saveGitRepos")
            }
        }
    }

    override suspend fun fetchCachedRepos(queryText: String): List<GitItem> =
        withContext(Dispatchers.IO) {
            val itemList = arrayListOf<RepoItem>()
            // appending '%' so we can allow other characters to be before and after the query string
            val query = "%${queryText.replace(' ', '%')}%"
            val reposList = gitItemDao.fetchReposByName(query)
            return@withContext reposList
        }

    override suspend fun deleteAllData() {
        gitItemDao.deleteAll()
    }
}