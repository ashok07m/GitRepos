package com.gitrepos.android.ui.home.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Class to hold repo details
 */

@Parcelize
data class Repo(
    var name: String,
    var fullName: String,
    var description: String?,
    var homePage: String?,
    var language: String?,
    var starsCount: String,
    var forksCount: String
) : Parcelable