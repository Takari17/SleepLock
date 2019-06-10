package com.takari.sleeplock.data.local

import android.content.SharedPreferences
import androidx.core.content.edit
import javax.inject.Inject
import javax.inject.Singleton

/*
 * Simply stores and returns data from Shared Preferences.
 */
@Singleton
class SharedPrefs @Inject constructor(
    private val sharedPrefs: SharedPreferences
) {

    fun saveIntIfNonNull(key: String, int: Int?) = int?.also { nonNullInt ->
        sharedPrefs.edit { putInt(key, nonNullInt) }
    }

    fun saveStringIfNonNull(key: String, string: String?) = string?.also { nonNullString ->
        sharedPrefs.edit { putString(key, nonNullString) }
    }

    fun saveBooleanIfNonNull(key: String, boolean: Boolean?) = boolean?.also { nonNullBoolean ->
        sharedPrefs.edit { putBoolean(key, nonNullBoolean) }
    }

    fun getInt(key: String): Int? {
        val int = sharedPrefs.getInt(key, 10)
        return if (int == 10) null
        else int
    }

    fun getString(key: String): String? {
        val string = sharedPrefs.getString(key, "")
        return if (string == "") null
        else string
    }

    fun getBoolean(key: String, defaultValue: Boolean): Boolean? =
        sharedPrefs.getBoolean(key, defaultValue)


    fun resetAllData() = sharedPrefs.edit { clear() }
}