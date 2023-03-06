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
    const val LAT = "current_lat"
    const val LONG = "current_long"
    const val SHORT_ADDRESS = "short_address"
    const val FULL_ADDRESS = "full_address"

    const val TYPE = "type"
    const val ID = "id"
    const val USER_ID = "user_id"
    const val IS_LOGIN = "login"
    const val IS_LOCATION = "location"
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

    fun getLatitude(): String? {
        return getString(LAT)
    }
    fun getLongitude(): String? {
        return getString(LONG)
    }
    fun getShortAddress(): String? {
        return getString(SHORT_ADDRESS)
    }
    fun getFullAddress(): String? {
        return getString(FULL_ADDRESS)
    }

    fun getLogInStatus(): Boolean? {
        return getBoolean(IS_LOGIN)
    }
    fun getLocationStatus(): Boolean? {
        return getBoolean(IS_LOCATION)
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

    fun setLatitude(latitude: String?) {
        putString(LAT, latitude)
    }
    fun setLongitude(longitude: String?) {
        putString(LONG, longitude)
    }
    fun setShortAddress(address: String?) {
        putString(SHORT_ADDRESS, address)
    }
    fun setFullAddress(address: String?) {
        putString(FULL_ADDRESS, address)
    }

    fun setLogInStatus(loginStatus: Boolean) {
        putBoolean(IS_LOGIN, loginStatus)
    }
    fun setLocationStatus(locationStatus: Boolean) {
        putBoolean(IS_LOCATION, locationStatus)
    }
}