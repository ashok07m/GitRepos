package com.gitrepos.android.data.repositories

import com.gitrepos.android.data.network.model.login.LoggedInUser
import com.gitrepos.android.data.source.login.LoginResult

interface LoginRepository {
    suspend fun login(username: String, password: String): LoginResult<LoggedInUser>
    suspend fun logout()
}