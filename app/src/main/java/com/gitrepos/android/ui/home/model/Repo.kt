package com.gitrepos.android.ui.home.model

/**
 * @author Created by kuashok on 2020-02-16
 */


data class Repo(
    val title: String,
    val description: String,
    val language: String,
    val starCount: Int,
    val lastUpdated: String
)