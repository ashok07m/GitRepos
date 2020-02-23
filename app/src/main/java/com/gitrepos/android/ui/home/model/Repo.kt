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
    val description: String,
    val languageUrl: String,
    val starCount: Int?,
    val lastUpdated: String?
) : Parcelable