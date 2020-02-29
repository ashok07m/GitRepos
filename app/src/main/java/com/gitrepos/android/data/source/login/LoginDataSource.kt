package com.gitrepos.android.data.source.login

import android.util.Log
import com.gitrepos.android.data.network.model.login.LoggedInUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.IOException

/**
 * Class to handle user authentication with login credentials and retrieves user information.
 */
class LoginDataSource {

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    suspend fun login(username: String, password: String): LoginResult<LoggedInUser> =
        withContext(Dispatchers.IO) {
            var result: LoginResult<LoggedInUser> =
                LoginResult.Error(IOException("Error logging in"))

            try {
                val task = auth.signInWithEmailAndPassword(username, password).await()
                task?.let {
                    if (it.user != null) {
                        Log.d("TAG", "signInWithEmail:success")
                        val user = auth.currentUser
                        user?.let {
                            result =
                                LoginResult.Success(
                                    LoggedInUser(
                                        user.email,
                                        user.displayName
                                    )
                                )
                        }
                    }
                }
            } catch (e: FirebaseAuthException) {
                Log.w("TAG", "signInWithEmail:failure", e)
                result = LoginResult.Error(
                    IOException("Error logging in", e)
                )
            }

            return@withContext result
        }

    /**
     * Logout the user session
     */
    fun logout() {
        auth.signOut()
    }
}

