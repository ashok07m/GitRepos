package com.gitrepos.android.data.repositories

import android.util.Log
import com.gitrepos.android.data.database.dao.ReposDao
import com.gitrepos.android.data.database.entity.ReposEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DatabaseRepositoryImpl(private val reposDao: ReposDao) : DatabaseRepository {

    override suspend fun saveRepo(reposEntity: ReposEntity) = withContext(Dispatchers.IO) {
        val result = reposDao.insert(reposEntity)
        Log.d("DatabaseRepositoryImpl", "saveRepo() :: $result")
        return@withContext result > 0
    }

}