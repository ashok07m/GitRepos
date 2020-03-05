package com.gitrepos.android.ui.home.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Class to hold repo details
 */

@Parcelize
data class Repo(
    var avatarUrl: String,
    var owner: String,
    var title: String,
    var fullName: String,
    var description: String? = "N.A",
    var language: String? = "N.A",
    var starCount: Int? = 0,
    var lastUpdated: String? = null
) : Parcelable