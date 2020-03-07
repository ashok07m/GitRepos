package com.gitrepos.android.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.gitrepos.android.R
import com.gitrepos.android.data.persistence.PreferenceManger
import com.gitrepos.android.internal.afterTextChanged
import com.gitrepos.android.internal.hideKeyboard
import com.gitrepos.android.internal.showToast
import com.gitrepos.android.ui.CommonViewModel
import com.gitrepos.android.ui.login.model.LoggedInUserView
import com.gitrepos.android.ui.login.model.LoginFormState
import com.gitrepos.android.ui.login.model.LoginResult
import kotlinx.android.synthetic.main.fragment_login.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginFragment : Fragment() {

    private val loginViewModel: LoginViewModel by viewModel()
    private val commonViewModel: CommonViewModel by sharedViewModel()
    private val preferenceManager: PreferenceManger by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val isEnabledFPLogin =
            preferenceManager.getBooleanValue(getString(R.string.pref_key_fp_login))
        if (isEnabledFPLogin) {
            group?.visibility = View.INVISIBLE
        }

        val status = preferenceManager.getBooleanValue(getString(R.string.pref_key_fp_login))
        swFpEnable.isChecked = status

        etUsername.afterTextChanged {
            loginViewModel.loginDataChanged(
                etUsername.text.toString(),
                etPassword.text.toString()
            )
        }

        etPassword.apply {
            afterTextChanged {
                loginViewModel.loginDataChanged(
                    etUsername.text.toString(),
                    etPassword.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        if (btnLogin.isEnabled) {
                            btnLogin.callOnClick()
                        }
                }
                false
            }
        }

        btnLogin.setOnClickListener {
            loading.visibility = View.VISIBLE
            loginViewModel.login(etUsername.text.toString(), etPassword.text.toString())
        }


        loginViewModel.getCurrentLoggedInUser()?.observe(viewLifecycleOwner, loggedInUserObserver)
        loginViewModel.loginFormState.observe(viewLifecycleOwner, loginFormStateObserver)
        loginViewModel.loginResult.observe(viewLifecycleOwner, loginResultObserver)
        commonViewModel.bioAuthMsgLiveData.observe(viewLifecycleOwner, bioAuthMessageObserver)

    }

    /**
     * Updates logged in user info and navigates to home screen
     */
    fun navigateToHomeScreen(model: LoggedInUserView) {
        view?.let {
            val isEnabledFPLogin = swFpEnable.isChecked
            preferenceManager.putBooleanValue(
                getString(R.string.pref_key_fp_login),
                isEnabledFPLogin
            )
            updateLoggedInUser(model)
            activity?.hideKeyboard(it)
            val directions = LoginFragmentDirections.actionLoginFragmentToNavigationHome()
            it.findNavController().navigate(directions)
        }
    }

    /**
     * Shows login fails error
     */
    private fun showLoginFailed(@StringRes errorString: Int) {
        showToast(errorString)
    }

    /**
     *  Set loggedIn info in MainActivity and display loggedIn user
     */
    private fun updateLoggedInUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome)
        val displayName = model.displayName
        showToast("$welcome $displayName")
    }

    /**
     * Observes login form validations
     */
    private val loginFormStateObserver: Observer<LoginFormState> = Observer {
        val loginState = it ?: return@Observer

        // disable login button unless both username / password is valid
        btnLogin.isEnabled = loginState.isDataValid

        if (loginState.usernameError != null) {
            etUsername.error = getString(loginState.usernameError)
        }
        if (loginState.passwordError != null) {
            etPassword.error = getString(loginState.passwordError)
        }
    }

    /**
     * Observes login action result
     */
    private val loginResultObserver: Observer<LoginResult> = Observer {
        val loginResult = it ?: return@Observer

        loading.visibility = View.GONE
        if (loginResult.error != null) {
            showLoginFailed(loginResult.error)
        }
        if (loginResult.success != null) {
            commonViewModel.setLoggedInUser(loginResult.success)
            navigateToHomeScreen(loginResult.success)
        }

    }

    /**
     * Observes if user is already loggedIn and navigates if Bio Auth is not enabled
     */
    private val loggedInUserObserver: Observer<LoggedInUserView> = Observer {
        it?.let {
            val isEnabledFPLogin =
                preferenceManager.getBooleanValue(getString(R.string.pref_key_fp_login))
            commonViewModel.setLoggedInUser(it)
            if (!isEnabledFPLogin) {
                navigateToHomeScreen(it)
            }
        }
    }

    /**
     * Observes bio authentication messages
     */
    private val bioAuthMessageObserver: Observer<String> = Observer {
        txtMessage.text = it
    }
}
