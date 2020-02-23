package com.gitrepos.android.data.repositories

import com.gitrepos.android.data.database.entity.ReposEntity
import com.gitrepos.android.ui.home.model.RepoItem
import kotlinx.coroutines.flow.Flow

interface DatabaseRepository {
    suspend fun saveRepo(reposEntity: ReposEntity): Boolean
    suspend fun fetchSavedRepos(): Flow<List<RepoItem>>
}