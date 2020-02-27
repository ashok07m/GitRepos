package com.gitrepos.android.data.repositories

import com.gitrepos.android.data.network.model.login.LoggedInUser
import com.gitrepos.android.data.source.login.LoginDataSource
import com.gitrepos.android.data.source.login.LoginResult

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

class LoginRepositoryImpl(private val dataSource: LoginDataSource) : LoginRepository {

    // in-memory cache of the loggedInUser object
    var user: LoggedInUser? = null
        private set

    val isLoggedIn: Boolean
        get() = user != null

    init {
        user = null
    }

    private fun setLoggedInUser(loggedInUser: LoggedInUser) {
        this.user = loggedInUser
    }

    override suspend fun login(username: String, password: String): LoginResult<LoggedInUser> {

        val result = dataSource.login(username, password)

        if (result is LoginResult.Success) {
            setLoggedInUser(result.data)
        }

        return result
    }

    override suspend fun logout() {
        user = null
        dataSource.logout()
    }
}
