package com.gitrepos.android.ui

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.gitrepos.android.R
import com.gitrepos.android.data.auth.AuthDialogBuilder
import com.gitrepos.android.data.auth.BioAuthCallBacks
import com.gitrepos.android.data.auth.BioAuthManager
import com.gitrepos.android.data.persistence.PreferenceManger
import com.gitrepos.android.ui.details.DetailsFragment
import com.gitrepos.android.ui.login.LoginFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_login.*
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity(), NavController.OnDestinationChangedListener,
    BioAuthCallBacks {

    private val preferenceManager: PreferenceManger by inject()
    private val sharedViewModel: SharedViewModel by viewModels()
    private lateinit var navController: NavController

    // Auth dialog information
    private val authDialogBuilder: AuthDialogBuilder by lazy {
        AuthDialogBuilder(
            title = R.string.label_authenticate,
            subTitle = R.string.label_app_requires_bio_auth,
            description = R.string.label_app_security_requires_auth,
            negativeButtonText = R.string.label_cancel
        )
    }

    // Auth manager instance
    private val bioAuthManager: BioAuthManager by lazy {
        BioAuthManager(
            this,
            this,
            authDialogBuilder
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        navController = findNavController(R.id.nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_repos,
                R.id.navigation_settings
            )
        )
        findViewById<Toolbar>(R.id.toolbar)
            .setupWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener(this)

        // set auth manager in shared viewmodel
        sharedViewModel.setBioAuthManager(bioAuthManager)
    }

    override fun onResume() {
        super.onResume()
        // check bio auth status
        bioAuthManager.canAuthenticate()

        doBioAUth()
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        when (destination.id) {
            R.id.navigation_login -> {
                toolbarContainer.visibility = View.GONE
                nav_view.visibility = View.GONE
            }
            else -> {
                toolbarContainer.visibility = View.VISIBLE
                nav_view.visibility = View.VISIBLE
            }
        }
    }

    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
        txtMessage?.text = errString
    }

    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
        txtMessage?.text = ""
        val currentFragment = getCurrentFragment()
        if (currentFragment is LoginFragment) {
            sharedViewModel.getLoggedInUser()?.let {
                currentFragment.updateUiWithUser(it)
            }
        }
    }

    override fun onAuthNegativeButtonClicked() {
        // finish the activity
        finish()
    }

    override fun onKeyInvalidated() {
        txtMessage?.text = getString(R.string.msg_new_fingerprint_enrolled)
    }

    override fun onFingerPrintsNotEnrolled() {
        txtMessage?.text = getString(R.string.msg_register_fingerprint)
    }

    override fun onFingerPrintHardwareUnavailable() {
        txtMessage?.text = getString(R.string.msg_fingerprint_hardware_unavailable)
    }

    override fun onNoFingerPrintSensorOnDevice() {
        txtMessage?.text = getString(R.string.msg_no_fp_sensor_on_device)
    }

    /**
     * Gets current visible fragment
     */
    private fun getCurrentFragment(): Fragment? {
        return supportFragmentManager.primaryNavigationFragment?.childFragmentManager?.fragments?.get(
            0
        )
    }

    /**
     * Performs bio authentication
     */
    fun doBioAUth() {
        val isEnabledFPLogin =
            preferenceManager.getBooleanValue(getString(R.string.pref_key_fp_login))
        if (isEnabledFPLogin && (getCurrentFragment() is LoginFragment || getCurrentFragment() is DetailsFragment)) {
            group?.visibility = View.INVISIBLE
            bioAuthManager.authenticate()
        }
    }
}
