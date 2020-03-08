package com.gitrepos.android.data.database.entity

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Database table entity class
 */
const val table_repos = "repos_table"

@Entity(tableName = table_repos, indices = [Index(value = ["name"], unique = true)])
data class ReposEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "_id") val id: Int = 0,
    @NonNull @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "fullName") var fullName: String,
    @ColumnInfo(name = "description") var description: String? = null,
    @ColumnInfo(name = "language") var language: String? = null,
    @NonNull @ColumnInfo(name = "stars_count") var starsCount: String,
    @NonNull @ColumnInfo(name = "forks_count") var forksCount: String,
    @ColumnInfo(name = "homepage") var homepage: String
)