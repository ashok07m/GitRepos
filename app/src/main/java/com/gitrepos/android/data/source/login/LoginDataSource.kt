package com.gitrepos.android.data.source.login

import com.gitrepos.android.data.network.model.login.LoggedInUser
import java.io.IOException
import java.util.*

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource {

    fun login(username: String, password: String): LoginResult<LoggedInUser> {
        try {
            // TODO: handle loggedInUser authentication
            val fakeUser =
                LoggedInUser(
                    UUID.randomUUID().toString(),
                    "Jane Doe"
                )
            return LoginResult.Success(fakeUser)
        } catch (e: Throwable) {
            return LoginResult.Error(
                IOException(
                    "Error logging in",
                    e
                )
            )
        }
    }

    fun logout() {
        // TODO: revoke authentication
    }
}

