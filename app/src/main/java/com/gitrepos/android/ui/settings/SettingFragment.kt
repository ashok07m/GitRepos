package com.gitrepos.android.ui.settings

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.gitrepos.android.R
import com.gitrepos.android.data.persistence.PreferenceManger
import com.gitrepos.android.internal.showToast
import com.gitrepos.android.ui.MainActivity
import org.koin.android.ext.android.inject

class SettingFragment : PreferenceFragmentCompat(), Preference.OnPreferenceClickListener,
    Preference.OnPreferenceChangeListener {

    private val settingsViewModel: SettingsViewModel by inject()
    private val preferenceManager: PreferenceManger by inject()


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
        val loggedInUser = (activity as MainActivity).getLoggedInUser()
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
                settingsViewModel.logOut()
                showToast(R.string.message_logout_success)
                findNavController().navigate(R.id.navigation_login)
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