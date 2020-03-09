package com.gitrepos.android.data.repositories

import android.util.Log
import com.gitrepos.android.data.database.dao.ReposDao
import com.gitrepos.android.data.database.entity.ReposEntity
import com.gitrepos.android.ui.home.model.Repo
import com.gitrepos.android.ui.home.model.RepoItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DatabaseRepositoryImpl(private val reposDao: ReposDao) : DatabaseRepository {

    override suspend fun saveRepo(reposEntity: ReposEntity) = withContext(Dispatchers.IO) {
        val result = reposDao.insert(reposEntity)
        Log.d("DatabaseRepositoryImpl", "saveRepo() :: $result")
        return@withContext result > 0
    }

    override suspend fun fetchSavedRepos(): List<RepoItem> = withContext(Dispatchers.IO) {
        val itemList = arrayListOf<RepoItem>()
        val reposList = reposDao.fetchSavedRepos()
        reposList.map { item ->
            val repo =
                Repo(
                    name = item.name,
                    fullName = item.fullName,
                    starsCount = item.starsCount,
                    forksCount = item.forksCount,
                    description = item.description,
                    language = item.language,
                    homePage = item.homepage
                )
            itemList.add(RepoItem(repo))
        }
        itemList
    }


    override suspend fun deleteAllData() {
        reposDao.deleteAll()
    }
}