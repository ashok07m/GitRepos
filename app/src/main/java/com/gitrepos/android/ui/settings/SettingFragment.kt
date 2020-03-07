package com.gitrepos.android.ui.settings

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.gitrepos.android.R
import com.gitrepos.android.data.persistence.PreferenceManger
import com.gitrepos.android.internal.showToast
import com.gitrepos.android.ui.CommonViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class SettingFragment : PreferenceFragmentCompat(), Preference.OnPreferenceClickListener,
    Preference.OnPreferenceChangeListener {

    private val settingsViewModel: SettingsViewModel by inject()
    private val preferenceManager: PreferenceManger by inject()
    private val commonViewModel: CommonViewModel by sharedViewModel()


    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_preferences, rootKey)

        initLogoutSummary()
        initFPLoginStatus()
    }

    /**
     * Updates fp login switch status
     */
    private fun initFPLoginStatus() {
        val status = preferenceManager.getBooleanValue(getString(R.string.pref_key_fp_login))
        val fpLoginPref: SwitchPreferenceCompat? =
            findPreference(getString(R.string.pref_key_fp_login))
        fpLoginPref?.apply {
            setDefaultValue(status)
            isChecked = status
            onPreferenceChangeListener = this@SettingFragment
        }
    }

    /**
     * Updates logout summary with loggedIn user
     */
    private fun initLogoutSummary() {
        val logoutSummary: Preference? = findPreference(getString(R.string.pref_key_logout))
        val loggedInUser = commonViewModel.getLoggedInUser()
        var summaryText = logoutSummary?.summary
        loggedInUser?.let {
            summaryText = if (it.email.isNotEmpty()) {
                it.email
            } else {
                getString(R.string.unknown)
            }
        }

        logoutSummary?.apply {
            summary = summaryText
            onPreferenceClickListener = this@SettingFragment
        }
    }

    override fun onPreferenceClick(preference: Preference?): Boolean {
        return when (preference?.key) {
            getString(R.string.pref_key_logout) -> {
                // disabled as user should not be logged out

                showToast(R.string.message_logout_restricted)

                /*settingsViewModel.logOut()
                showToast(R.string.message_logout_success)
                view?.let {
                    val directions = SettingFragmentDirections.actionNavigationSettingsToNavigationLogin()
                    it.findNavController().navigate(directions)
                }*/
                true
            }
            else -> {
                false
            }
        }
    }

    override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
        return when (preference?.key) {
            getString(R.string.pref_key_fp_login) -> {
                val isChecked = newValue as Boolean
                preferenceManager.putBooleanValue(getString(R.string.pref_key_fp_login), isChecked)
                true
            }
            else -> {
                false
            }
        }
    }
}