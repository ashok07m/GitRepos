package com.gitrepos.android.ui

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
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
import com.gitrepos.android.core.dialog.BaseDialogFragment
import com.gitrepos.android.core.dialog.NativeAlertDialogFragment
import com.gitrepos.android.data.auth.AuthDialogBuilder
import com.gitrepos.android.data.auth.BioAuthCallBacks
import com.gitrepos.android.data.auth.BioAuthManager
import com.gitrepos.android.data.persistence.PreferenceManger
import com.gitrepos.android.ui.details.DetailsFragment
import com.gitrepos.android.ui.login.LoginFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity(), NavController.OnDestinationChangedListener,
    BioAuthCallBacks {

    private val preferenceManager: PreferenceManger by inject()
    private val commonViewModel: CommonViewModel by viewModel()
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
            authDialogBuilder,
            preferenceManager
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

    }

    override fun onStart() {
        super.onStart()
        // set auth manager in shared viewmodel
        commonViewModel.setBioAuthManager(bioAuthManager)
    }

    override fun onResume() {
        super.onResume()
        // check bio auth status
        if (bioAuthManager.canAuthenticate()) {
            doBioAUth()
        }
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
        commonViewModel.setBioAuthMessage(errString.toString())
    }

    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
        commonViewModel.setBioAuthMessage("")
        commonViewModel.setBioAuthenticated(true)
        val currentFragment = getCurrentFragment()
        if (currentFragment is LoginFragment) {
            commonViewModel.getLoggedInUser()?.let {
                currentFragment.navigateToHomeScreen(it)
            }
        }
    }

    override fun onAuthNegativeButtonClicked() {
        // finish the activity
        finish()
    }

    override fun onKeyInvalidated() {
        commonViewModel.setBioAuthMessage(getString(R.string.msg_new_fingerprint_enrolled))
        clearAndLogoutUser()
        // show logged out dialog
        showLoggedOutDialog()
    }

    override fun onFingerPrintsNotEnrolled() {
        commonViewModel.setBioAuthMessage(getString(R.string.msg_register_fingerprint))
        val key = preferenceManager.getStringValue(getString(R.string.pref_key_enc_data))
        if (!key.isNullOrEmpty()) {
            clearAndLogoutUser()
            // show logged out dialog
            showLoggedOutDialog(message = R.string.message_no_fp_enrolled)
        } else {
            commonViewModel.setFingerPrintEnrolled(false)
        }
    }

    override fun onFingerPrintsEnrolled() {
        commonViewModel.setFingerPrintEnrolled(true)
    }

    override fun onFingerPrintHardwareUnavailable() {
        commonViewModel.setBioAuthMessage(getString(R.string.msg_fingerprint_hardware_unavailable))
    }

    override fun onNoFingerPrintSensorOnDevice() {
        commonViewModel.setBioAuthMessage(getString(R.string.msg_no_fp_sensor_on_device))
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
        val status = commonViewModel.isFingerPrintEnrolled() &&
                commonViewModel.isBioAuthSettingEnabled() &&
                !commonViewModel.isBioAuthenticated()
        if (status && (getCurrentFragment() is LoginFragment || getCurrentFragment() is DetailsFragment)) {
            bioAuthManager.authenticate()
        }
    }

    /**
     * Shows logged out dialog
     */
    private fun showLoggedOutDialog(message: Int = R.string.msg_new_fingerprint_enrolled) {
        val title = getString(R.string.title_biometric_altered)
        val msg = getString(message)
        val dialog =
            commonViewModel.createDialogBuilder(
                dialogCallbackListener,
                title,
                msg
            ).build()

        supportFragmentManager.let { dialog.show(it, NativeAlertDialogFragment.TAG) }
    }

    /**
     * Callbacks from dialog
     */
    private val dialogCallbackListener = object : BaseDialogFragment.DialogEventListener {

        override fun onPositiveButtonClicked(Object: Any) {

        }

        override fun onNegativeButtonClicked(Object: Any) {

        }

        override fun onCancelled(dialog: DialogInterface) {
            dialog.dismiss()
        }
    }

    /**
     * Clears user data and log him out
     */
    private fun clearAndLogoutUser() {
        // delete app data
        commonViewModel.clearAppData()
        // navigate to login screen
        navController.popBackStack()
        navController.navigate(R.id.navigation_login)
    }

}
