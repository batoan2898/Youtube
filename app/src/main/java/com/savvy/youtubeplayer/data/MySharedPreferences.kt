package com.savvy.youtubeplayer.data

import android.content.Context
import android.content.SharedPreferences

object MySharedPreferences {

    private const val MY_SHARE_PREFERENCES = "MY_SHARE_PREFERENCES"

    private lateinit var preferences: SharedPreferences

    private const val KEY_LOGIN = "KEY_LOGIN"

    private const val Id = ""


    fun init(context: Context) {
        preferences = context.getSharedPreferences(MY_SHARE_PREFERENCES, Context.MODE_PRIVATE)
    }

    /**
     * SharedPreferences extension function, so we won't need to call edit() and apply()
     * ourselves on every SharedPreferences operation.
     */
    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = edit()
        operation(editor)
        editor.apply()
    }

//    var isLogin: Boolean
//        get() = preferences.getBoolean(KEY_LOGIN, false)
//        set(status) = preferences.edit {
//            it.putBoolean(KEY_LOGIN, status)
//        }

    fun checkLogin(): Boolean {
        return preferences.getBoolean(KEY_LOGIN, true)
    }

    fun setLogin(isLogin: Boolean) {
        preferences.edit {
            it.putBoolean(KEY_LOGIN, isLogin)
        }
    }

    fun setId(id: String) {
        preferences.edit {
            it.putString(Id, id)
        }
    }

    fun getId(): String {
        return preferences.getString(Id,"").toString()
    }

}