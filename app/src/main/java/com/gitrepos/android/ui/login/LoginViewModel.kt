package com.gitrepos.android.ui.login

import android.content.Context
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gitrepos.android.R
import com.gitrepos.android.data.repositories.LoginRepository
import com.gitrepos.android.data.source.login.LoginResult.Success
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class LoginViewModel(private val context: Context, private val loginRepository: LoginRepository) :
    ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult


    /**
     * Makes Login request to server
     */
    fun login(username: String, password: String) = viewModelScope.launch {
        val result = loginRepository.login(username, password)

        if (result is Success) {
            _loginResult.value =
                LoginResult(
                    success = LoggedInUserView(
                        displayName = result.data.displayName ?: context.getString(R.string.user),
                        email = result.data.email ?: context.getString(R.string.unknown)
                    )
                )
        } else {
            _loginResult.value = LoginResult(error = R.string.login_failed)
        }
    }

    /**
     * login form validations
     */
    fun loginDataChanged(username: String, password: String) {
        if (!isUserNameValid(username)) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return if (username.isNotBlank() && username.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            false
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }

    /**
     * Gets the info of currently loggedIn user
     */
    fun getCurrentLoggedInUser(): LiveData<LoggedInUserView>? {
        val userdata = MutableLiveData<LoggedInUserView>()
        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.let {
            val email = it.email ?: context.getString(R.string.unknown)
            val displayName: String = if (!it.displayName.isNullOrEmpty())
                it.displayName!!
            else
                context.getString(R.string.user)
            userdata.value = LoggedInUserView(email, displayName)
            return userdata
        }

        return null
    }
}
