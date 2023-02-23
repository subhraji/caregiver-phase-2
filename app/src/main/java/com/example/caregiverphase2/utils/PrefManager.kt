package com.example.caregiverphase2.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.caregiverphase2.MyApplication

object PrefManager {

    private val KEY_USER_MODEL: String?="MODEL"
    private var sharedPreferences: SharedPreferences = MyApplication.application.applicationContext.getSharedPreferences(
        "",
        Context.MODE_PRIVATE
    )

    // Keys
    const val KEY_AUTH_TOKEN = "Auth_Token"
    const val EMAIL = "email"
    const val FULL_NAME = "full_name"
    const val LAST_NAME = "last_name"
    const val MOBILE = "mobile"
    const val USER_IMG = "user_img"
    const val TYPE = "type"
    const val ID = "id"
    const val USER_ID = "user_id"
    const val IS_LOGIN = "login"
    const val IS_WHATS = "whatsOnTut"
    const val IS_HOW = "howTut"
    const val IS_CIRCLE = "circleTut"
    const val IS_ON_BOARDING = "on_boardingTut"
    const val IS_HOME = "homeTut"
    const val CIRCLE_ID = "circle_id"



    fun getString(key: String): String? {
        return sharedPreferences.getString(key, "")
    }

    fun getInt(key: String): Int? {
        return sharedPreferences.getInt(key,0)
    }

    fun putString(key: String, value: String?) {
        var mEditor: SharedPreferences.Editor
        mEditor = sharedPreferences.edit()
        mEditor.putString(key, value)
        mEditor.commit()
    }

    fun putInt(key: String, value: Int?) {
        //value?.let { sharedPreferences.edit().putInt(key, it).apply() }
        var mEditor: SharedPreferences.Editor
        mEditor = sharedPreferences.edit()
        value?.let {
            mEditor.putInt(key, it)
            mEditor.commit()
        }

    }

    fun putBoolean(key: String, value: Boolean?) {
        //value?.let { sharedPreferences.edit().putBoolean(key, it).apply() }
        var mEditor: SharedPreferences.Editor
        mEditor = sharedPreferences.edit()
        value?.let { mEditor.putBoolean(key, it) }
        mEditor.commit()
    }

//    fun saveUserModel(userModel: User?) {
//        val gson = Gson()
//        val editor = sharedPreferences.edit()
//        editor.putString(KEY_USER_MODEL, gson.toJson(userModel))
//        editor.apply()
//    }
//
//    fun getUserModel(): User? {
//        val gson: Gson = Gson()
//        val strUserModel = sharedPreferences.getString(KEY_USER_MODEL, "")
//        if (strUserModel.isNullOrEmpty())
//            return null
//        else
//            return gson.fromJson(strUserModel, User::class.java)
//    }

    fun getBoolean(key: String): Boolean {
        return sharedPreferences.getBoolean(key, false)
    }

    fun clearPref(){
        val editor = sharedPreferences.edit()
        editor.remove(IS_LOGIN)
        editor.remove(KEY_AUTH_TOKEN)
        editor.apply()
    }


    fun getKeyAuthToken(): String? {
        return getString(KEY_AUTH_TOKEN)
    }
    fun getUserFullName(): String? {
        return getString(FULL_NAME)
    }
    fun getUserLastName(): String? {
        return getString(LAST_NAME)
    }
    fun getUserImg(): String? {
        return getString(USER_IMG)
    }
    fun getLogInStatus(): Boolean? {
        return getBoolean(IS_LOGIN)
    }
    fun getUserId(): Int? {
        return getInt(USER_ID)
    }



    fun setKeyAuthToken(auth_token: String) {
        putString(KEY_AUTH_TOKEN, auth_token)
    }
    fun setUserFullName(name: String) {
        putString(FULL_NAME, name)
    }
    fun setUserLastName(lastName: String) {
        putString(LAST_NAME, lastName)
    }
    fun setUserImg(img: String) {
        putString(USER_IMG, img)
    }
    fun setLogInStatus(loginStatus: Boolean) {
        putBoolean(IS_LOGIN, loginStatus)
    }
    fun setUserId(userId: Int) {
        putInt(USER_ID, userId)
    }
}