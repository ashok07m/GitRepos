package com.gitrepos.android.data.repositories

import com.gitrepos.android.data.database.entity.ReposEntity

interface DatabaseRepository {
    suspend fun saveRepo(reposEntity: ReposEntity): Boolean
}