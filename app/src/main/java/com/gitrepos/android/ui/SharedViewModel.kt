package com.gitrepos.android.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gitrepos.android.data.auth.BioAuthManager
import com.gitrepos.android.ui.login.model.LoggedInUserView

class SharedViewModel : ViewModel() {

    private lateinit var bioAuthManager: BioAuthManager
    private var loggedInUserView: LoggedInUserView? = null
    private val bioAuthMsgMutableLiveData = MutableLiveData<String>().apply { "" }
    val biAuthMsgLiveData = bioAuthMsgMutableLiveData

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

    companion object {
        const val TAG = "SharedViewModel"
    }
}