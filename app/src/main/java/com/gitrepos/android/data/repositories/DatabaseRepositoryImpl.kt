package com.gitrepos.android.data.repositories

import android.util.Log
import com.gitrepos.android.data.database.dao.ReposDao
import com.gitrepos.android.data.database.entity.ReposEntity
import com.gitrepos.android.ui.home.model.Repo
import com.gitrepos.android.ui.home.model.RepoItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext

class DatabaseRepositoryImpl(private val reposDao: ReposDao) : DatabaseRepository {

    override suspend fun saveRepo(reposEntity: ReposEntity) = withContext(Dispatchers.IO) {
        val result = reposDao.insert(reposEntity)
        Log.d("DatabaseRepositoryImpl", "saveRepo() :: $result")
        return@withContext result > 0
    }

    override suspend fun fetchSavedRepos(): Flow<List<RepoItem>> = flow {
        val itemList = arrayListOf<RepoItem>()
        val reposList = reposDao.fetchSavedRepos().flowOn(Dispatchers.IO)

        reposList.map {
            it.map { item ->
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
        }.flowOn(Dispatchers.Default)
            .collect {
                Log.d("TAG", "fetchSavedRepos :$it")
                emit(it)
            }


    }

    override suspend fun deleteAllData() {
        reposDao.deleteAll()
    }
}