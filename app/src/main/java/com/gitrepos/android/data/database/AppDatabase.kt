package com.gitrepos.android.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.gitrepos.android.data.database.dao.ReposDao
import com.gitrepos.android.data.database.entity.ReposEntity

/**
 * App Database class
 */

@Database(entities = [ReposEntity::class], version = 1, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {

    abstract fun reposDao(): ReposDao

    companion object {
        private var INSTANCE: AppDatabase? = null
        private const val APP_DB_NAME = "git_database"

        /**
         * Provide singleton instance of app database
         */
        fun getAppDatabaseInstance(
            context: Context
        ): AppDatabase {

            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java, APP_DB_NAME
                ).build()

                INSTANCE = instance
                instance
            }
        }

    }
}