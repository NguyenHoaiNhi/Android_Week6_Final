package com.example.week64

import android.content.Context
import android.content.SharedPreferences

/**
 * Created by Huu Hoang on 5/8/19.
 */
/**
 *  this static responsibility to GET / PUT data to SharedPreference
 */
object SharedPreferencesHelper {
    private const val MODE = Context.MODE_PRIVATE
    private lateinit var preferences: SharedPreferences


    fun init(context: Context) {
        preferences = context.getSharedPreferences(PREFERCENCES_NAME, MODE)
    }


    private fun isHasKey(key: String): Boolean {
        return preferences.contains(key)
    }


    fun saveInt(key: String, value: Int) {
        preferences.edit().putInt(key, value).apply()
    }


    fun saveString(key: String, value: String) {
        preferences.edit().putString(key, value).apply()
    }


    fun readString(key: String): String {
        //fixme: define error
        return if (isHasKey(key)) preferences.getString(key, "NO_VALUE")!!
        else "NO_CONTAIN_KEY"
    }

    fun readInt(key: String): Int {
        //fixme: define error
        return if (isHasKey(key)) preferences.getInt(key, -99)
        else -88
    }

    fun readInt(key: String, defaultValue: Int): Int {
        //fixme: define error
        return if (isHasKey(key)) preferences.getInt(key, defaultValue)
        else defaultValue
    }


}