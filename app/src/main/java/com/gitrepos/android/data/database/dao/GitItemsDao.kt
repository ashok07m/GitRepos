package com.gitrepos.android.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gitrepos.android.data.database.entity.GitItem
import com.gitrepos.android.data.database.entity.table_git_cache

/**
 * Room data access object for accessing the [Repo] table.
 */
@Dao
interface GitItemsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(posts: List<GitItem>)

    @Query(
        "SELECT * FROM $table_git_cache WHERE (name LIKE :queryString) OR (description LIKE " +
                ":queryString) ORDER BY stars DESC, name ASC"
    )
    suspend fun fetchReposByName(queryString: String): List<GitItem>


    @Query("delete from $table_git_cache")
    suspend fun deleteAll(): Int

    @Query("DELETE FROM $table_git_cache where _id NOT IN (SELECT _id from $table_git_cache ORDER BY _id DESC LIMIT 50)")
    suspend fun keepOnlyLatest50Rows()
}
