package com.gitrepos.android.ui.settings

import androidx.lifecycle.ViewModel
import com.gitrepos.android.data.repositories.DatabaseRepository
import com.google.firebase.auth.FirebaseAuth

class SettingsViewModel(
    private val dbRepository: DatabaseRepository
) : ViewModel() {

    /**
     * Logout user
     */
    fun logOut() {
        FirebaseAuth.getInstance().signOut()
    }
}