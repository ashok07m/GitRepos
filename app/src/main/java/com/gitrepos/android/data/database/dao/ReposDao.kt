package com.gitrepos.android.data.database.dao

import androidx.room.*
import com.gitrepos.android.data.database.entity.ReposEntity
import com.gitrepos.android.data.database.entity.table_repos

/**
 * Class to provide database actions
 */

@Dao
interface ReposDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reposEntity: ReposEntity): Long

    @Update
    suspend fun update(reposEntity: ReposEntity): Int

    @Query("delete from $table_repos where _id=:id")
    suspend fun delete(id: Int): Int

    @Query("delete from $table_repos")
    suspend fun deleteAll(): Int

    @Query("select * from $table_repos order by _id desc")
    fun fetchSavedRepos(): List<ReposEntity>

}