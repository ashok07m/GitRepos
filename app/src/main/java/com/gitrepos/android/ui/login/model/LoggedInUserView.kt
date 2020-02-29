package com.gitrepos.android.ui.login.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * User details post authentication that is exposed to the UI
 */
@Parcelize
data class LoggedInUserView(
    val email: String,
    val displayName: String
) : Parcelable
