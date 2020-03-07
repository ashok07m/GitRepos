package com.gitrepos.android.ui

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gitrepos.android.R
import com.gitrepos.android.core.dialog.BaseDialogFragment
import com.gitrepos.android.core.dialog.NativeAlertDialogFragment
import com.gitrepos.android.data.auth.BioAuthManager
import com.gitrepos.android.data.persistence.PreferenceManger
import com.gitrepos.android.data.repositories.DatabaseRepository
import com.gitrepos.android.data.repositories.LoginRepository
import com.gitrepos.android.ui.login.model.LoggedInUserView
import kotlinx.coroutines.launch

class CommonViewModel(
    private val appContext: Context,
    private val dbRepository: DatabaseRepository,
    private val loginRepository: LoginRepository,
    private val preferenceManager: PreferenceManger
) : ViewModel() {

    private lateinit var bioAuthManager: BioAuthManager
    private var loggedInUserView: LoggedInUserView? = null
    private val bioAuthMsgMutableLiveData = MutableLiveData<String>().apply { "" }
    val bioAuthMsgLiveData = bioAuthMsgMutableLiveData
    private var isBiAuthenticated = false

    /**
     * Sets auth manager instance
     */
    fun setBioAuthManager(bioAuthManager: BioAuthManager) {
        this.bioAuthManager = bioAuthManager
    }

    /**
     * Gets auth manager instance
     */
    fun getBioAuthManager(): BioAuthManager {
        return bioAuthManager
    }


    /**
     * Sets loggedIn user info in activity
     */
    fun setLoggedInUser(loggedInUserView: LoggedInUserView) {
        this.loggedInUserView = loggedInUserView
    }

    /**
     * Gets logged in user info
     */
    fun getLoggedInUser(): LoggedInUserView? {
        return loggedInUserView
    }

    /**
     * Sets the messages received from bio authentication
     */
    fun setBioAuthMessage(message: String) {
        bioAuthMsgMutableLiveData.value = message
    }

    /**
     * Set status of bio auth
     */
    fun setBioAuthenticated(status: Boolean) {
        isBiAuthenticated = status
    }

    /**
     * Gets status of bio auth
     */
    fun isBioAuthenticated(): Boolean {
        return isBiAuthenticated
    }

    /**
     * Creates dialog
     */
    fun createDialogBuilder(
        dialogCallbackListener: BaseDialogFragment.DialogEventListener,
        title: String,
        message: String
    ): NativeAlertDialogFragment.NativeAlertDialogBuilder {
        return NativeAlertDialogFragment.NativeAlertDialogBuilder(dialogCallbackListener).apply {

            this.title = title
            this.message = message
            positiveButtonText = appContext.getString(R.string.label_ok)
            isCancellable = false
            isNegativeButton = false
            isPositiveButton = true
        }
    }

    /**
     * Clears app data
     */
    fun clearAppData() = viewModelScope.launch {
        loginRepository.logout()
        preferenceManager.clearAllPreferences()
        dbRepository.deleteAllData()
    }

    companion object {
        const val TAG = "SharedViewModel"
    }
}