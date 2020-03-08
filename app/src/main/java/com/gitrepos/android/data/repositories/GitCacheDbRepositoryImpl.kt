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
/*

        reposList.map {
            it.map { entity ->
                val repo =
                    Repo(
                        entity.owner.avatarUrl, entity.owner.login, entity.name,
                        "${entity.fullName}", entity.description, entity.language
                    )
                itemList.add(RepoItem(repo))
            }
            itemList
        }.flowOn(Dispatchers.Default)
            .collect {
                Log.d("TAG", "fetchSavedRepos :$it")
                emit(it)
            }

*/
            return@withContext reposList
        }

    override suspend fun deleteAllData() {
        gitItemDao.deleteAll()
    }
}