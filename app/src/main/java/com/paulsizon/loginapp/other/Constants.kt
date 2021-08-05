package com.paulsizon.loginapp.other

object Constants {
    val IGNORE_AUTH_URLS = listOf("/login", "/register")
    const val DATABASE_NAME = "notes_db"

    //todo insert my ip instead of 10.0.2.2
    const val BASE_URL = "http://10.0.2.2:8001"

    const val KEY_LOGGED_IN_EMAIL = "KEY_LOGGED_IN_EMAIL"
    const val KEY_PASSWORD = "KEY_PASSWORD"

    const val NO_EMAIL = "NO_EMAIL"
    const val NO_PASSWORD = "NO_PASSWORD"

    const val DEFAULT_NOTE_COLOR = "FF1EB619"


    const val ENCRYPTED_SHARED_PREF_NAME = "enc_shared_pref"
}