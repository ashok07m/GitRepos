package com.gitrepos.android.ui.settings

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.gitrepos.android.R
import com.gitrepos.android.internal.showToast
import com.gitrepos.android.ui.MainActivity
import org.koin.android.ext.android.inject

class SettingFragment : PreferenceFragmentCompat(), Preference.OnPreferenceClickListener {

    private val settingsViewModel: SettingsViewModel by inject()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_preferences, rootKey)

        initLogoutSummary()
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
            getString(R.string.pref_key_fp_login) -> {
                true
            }
            else -> {
                false
            }
        }
    }
}