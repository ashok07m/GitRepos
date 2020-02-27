package com.gitrepos.android.ui.login

/**
 * User details post authentication that is exposed to the UI
 */
data class LoggedInUserView(
    val email: String,
    val displayName: String
)
