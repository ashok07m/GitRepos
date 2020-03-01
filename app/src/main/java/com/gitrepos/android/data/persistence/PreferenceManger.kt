package com.gitrepos.android.data.persistence

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.gitrepos.android.R

/**
 * Manages app preferences
 */
class PreferenceManger(context: Context) {

    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences(
            context.getString(R.string.pref_file_app),
            Context.MODE_PRIVATE
        )
    }

    fun putBooleanValue(key: String, value: Boolean) =
        sharedPreferences.edit { putBoolean(key, value) }

    fun putStringValue(key: String, value: String) =
        sharedPreferences.edit { putString(key, value) }

    fun getBooleanValue(key: String) = with(sharedPreferences) { getBoolean(key, false) }

    fun getStringValue(key: String) = with(sharedPreferences) { getString(key, null) }

}