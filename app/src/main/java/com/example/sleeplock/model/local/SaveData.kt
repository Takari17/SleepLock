package com.example.sleeplock.model.local

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.example.sleeplock.model.util.Constants


class SaveData(context: Application) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(Constants.SHARED_PREFS.text, MODE_PRIVATE)
    private val editor = sharedPreferences.edit()

    fun saveBoolean(Tag: String, aBoolean: Boolean) {
        editor.putBoolean(Tag, aBoolean)
        editor.apply()
    }

    fun saveInt(Tag: String, number: Int) {
        editor.putInt(Tag, number)
        editor.apply()
    }

    fun saveString(Tag: String, string: String) {
        editor.putString(Tag, string)
        editor.apply()
    }

    fun saveLong(Tag: String, aLong: Long) {
        editor.putLong(Tag, aLong)
        editor.apply()
    }

    // Tag must match the Tag of the data saved

    fun getSavedLong(Tag: String, aLong: Long): Long = sharedPreferences.getLong(Tag, aLong)

    fun getSavedBoolean(Tag: String, defaultValue: Boolean): Boolean = sharedPreferences.getBoolean(Tag, defaultValue)

    fun getSavedInt(Tag: String, defaultValue: Int): Int = sharedPreferences.getInt(Tag, defaultValue)

    fun getSavedString(Tag: String, defaultValue: String): String? = sharedPreferences.getString(Tag, defaultValue)


}
