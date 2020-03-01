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
import com.gitrepos.android.ui.MainActivity
import com.gitrepos.android.ui.login.model.LoggedInUserView
import kotlinx.android.synthetic.main.fragment_login.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginFragment : Fragment() {

    private val loginViewModel: LoginViewModel by viewModel()
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

        // check user's current logged status
        loginViewModel.getCurrentLoggedInUser()?.observe(viewLifecycleOwner,
            Observer {
                it?.let {
                    val isEnabledFPLogin =
                        preferenceManager.getBooleanValue(getString(R.string.pref_key_fp_login))
                    (activity as MainActivity).setLoggedInUser(it)
                    if (!isEnabledFPLogin) {
                        updateUiWithUser(it)
                    }
                }
            })


        loginViewModel.loginFormState.observe(viewLifecycleOwner, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            btnLogin.isEnabled = loginState.isDataValid

            if (loginState.usernameError != null) {
                etUsername.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
                etPassword.error = getString(loginState.passwordError)
            }
        })

        loginViewModel.loginResult.observe(viewLifecycleOwner, Observer {
            val loginResult = it ?: return@Observer

            loading.visibility = View.GONE
            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
            }
            if (loginResult.success != null) {
                (activity as MainActivity).setLoggedInUser(loginResult.success)
                updateUiWithUser(loginResult.success)
            }

        })

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
            val isEnabledFPLogin = swFpEnable.isChecked

            preferenceManager.putBooleanValue(
                getString(R.string.pref_key_fp_login),
                isEnabledFPLogin
            )

            loading.visibility = View.VISIBLE
            loginViewModel.login(etUsername.text.toString(), etPassword.text.toString())
        }

        val status = preferenceManager.getBooleanValue(getString(R.string.pref_key_fp_login))
        swFpEnable.isChecked = status

    }

    fun updateUiWithUser(model: LoggedInUserView) {
        view?.let {
            updateLoggedInUser(model)
            activity?.hideKeyboard(it)
            it.findNavController().navigate(R.id.navigation_home)
        }
    }

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
}
