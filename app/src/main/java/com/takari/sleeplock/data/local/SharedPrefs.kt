package com.takari.sleeplock.data.local

import android.content.SharedPreferences
import androidx.core.content.edit
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Stores and returns data from Shared Preferences.
 */
@Singleton
class SharedPrefs @Inject constructor(
    private val sharedPrefs: SharedPreferences
) {

    /**
     * Saves either an Integer, String, or Boolean value to shared preferences if non null.
     */
    fun saveValueIfNonNull(key: String, value: Any?) {
        value?.also { nonNullValue ->

            when (value.javaClass.simpleName) {
                SimpleJavaClassNames.Integer.name -> sharedPrefs.edit { putInt(key, nonNullValue as Int) }

                SimpleJavaClassNames.String.name -> sharedPrefs.edit { putString(key, nonNullValue as String) }

                SimpleJavaClassNames.Boolean.name -> sharedPrefs.edit { putBoolean(key, nonNullValue as Boolean) }

                else -> throw Exception("Invalid type, the method can only save Integers, Strings and Booleans")
            }
        }
    }


    /**
    Returns a Integer value if it exist, if not it returns the default value.
     */
    fun getInt(key: String, defaultValue: Int): Int =
        sharedPrefs.getInt(key, defaultValue)

    /**
    Returns a String value if it exist, if not it returns the default value.
     */
    fun getString(key: String, defaultValue: String): String =
        sharedPrefs.getString(key, defaultValue)!!

    /**
    Returns a Boolean value if it exist, if not it returns the default value.
     */
    fun getBoolean(key: String, defaultValue: Boolean): Boolean =
        sharedPrefs.getBoolean(key, defaultValue)


    fun resetAllData() = sharedPrefs.edit { clear() }


    private enum class SimpleJavaClassNames{
        Integer, String, Boolean
    }
}