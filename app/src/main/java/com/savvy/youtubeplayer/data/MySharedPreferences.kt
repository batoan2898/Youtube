package com.savvy.youtubeplayer.data

import android.content.Context
import android.content.SharedPreferences

object MySharedPreferences {

    private const val MY_SHARE_PREFERENCES = "MY_SHARE_PREFERENCES"

    private lateinit var preferences: SharedPreferences

    private const val KEY_LOGIN = "KEY_LOGIN"

    private const val Id = ""

    private const val keyName = "name"

    private const val keyEmail = "email"


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


    fun checkLogin(): Boolean {
        return preferences.getBoolean(KEY_LOGIN, false)
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

    fun setName(name: String){
        preferences.edit(){
            it.putString(keyName,name)
        }
    }

    fun setEmail(email: String){
        preferences.edit(){
            it.putString(keyEmail,email)
        }
    }


    fun getName():String{
        return preferences.getString(keyName,"").toString()
    }

    fun getEmail():String{
        return preferences.getString(keyEmail,"").toString()
    }


}