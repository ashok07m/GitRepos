package com.gitrepos.android.data.network.model.git


import com.gitrepos.android.data.database.entity.GitItem
import com.google.gson.annotations.SerializedName

data class GitSearchResponse(
    @SerializedName("items")
    val gitItems: List<GitItem> = emptyList(),
    @SerializedName("total_count")
    val total: Int = 0,
    val nextPage: Int? = null
)