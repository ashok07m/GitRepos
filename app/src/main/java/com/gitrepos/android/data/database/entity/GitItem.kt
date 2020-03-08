package com.gitrepos.android.data.database.entity


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

const val table_git_repos = "gitReposCache_table"

@Entity(tableName = table_git_repos)
data class GitItem(
    @field:SerializedName("created_at")
    val createdAt: String,
    @field:SerializedName("description")
    val description: String?,
    @field:SerializedName("downloads_url")
    val downloadsUrl: String,
    @field:SerializedName("forks_count")
    val forksCount: Int,
    @field:SerializedName("full_name")
    val fullName: String,
    @field:SerializedName("homepage")
    val homepage: String?,
    @field:SerializedName("html_url")
    val htmlUrl: String,
    @PrimaryKey @field:SerializedName("id")
    val id: Int,
    @field:SerializedName("language")
    val language: String?,
    @field:SerializedName("name")
    val name: String,
    @field:SerializedName("stargazers_count")
    val stars: Int,
    @field:SerializedName("updated_at")
    val updatedAt: String
)