package com.gitrepos.android.ui.home.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Class to hold repo details
 */

@Parcelize
data class Repo(
    val avatarUrl: String,
    val owner: String,
    val title: String,
    val fullName: String,
    val description: String? = "N.A",
    val language: String? = "N.A",
    val starCount: Int? = 0,
    val lastUpdated: String? = null
) : Parcelable