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
import com.gitrepos.android.internal.afterTextChanged
import com.gitrepos.android.internal.hideKeyboard
import com.gitrepos.android.internal.showToast
import com.gitrepos.android.ui.MainActivity
import com.gitrepos.android.ui.login.model.LoggedInUserView
import kotlinx.android.synthetic.main.fragment_login.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginFragment : Fragment() {

    private val loginViewModel: LoginViewModel by viewModel()

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
                    updateUiWithUser(it)
                }
            })


        loginViewModel.loginFormState.observe(viewLifecycleOwner, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            login.isEnabled = loginState.isDataValid

            if (loginState.usernameError != null) {
                username.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
                password.error = getString(loginState.passwordError)
            }
        })

        loginViewModel.loginResult.observe(viewLifecycleOwner, Observer {
            val loginResult = it ?: return@Observer

            loading.visibility = View.GONE
            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
            }
            if (loginResult.success != null) {
                updateUiWithUser(loginResult.success)
            }

        })

        username.afterTextChanged {
            loginViewModel.loginDataChanged(
                username.text.toString(),
                password.text.toString()
            )
        }

        password.apply {
            afterTextChanged {
                loginViewModel.loginDataChanged(
                    username.text.toString(),
                    password.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        if (login.isEnabled) {
                            login.callOnClick()
                        }
                }
                false
            }

            login.setOnClickListener {
                loading.visibility = View.VISIBLE
                loginViewModel.login(username.text.toString(), password.text.toString())
            }
        }
    }

    private fun updateUiWithUser(model: LoggedInUserView) {
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
        (activity as MainActivity).setLoggedInUser(model)

        val welcome = getString(R.string.welcome)
        val displayName = model.displayName
        showToast("$welcome $displayName")
    }
}
