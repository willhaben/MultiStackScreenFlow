package at.willhaben.multiscreenflow

import at.willhaben.multiscreenflow.App.Companion.CONTEXT
import org.jetbrains.anko.defaultSharedPreferences

fun isLoggedIn() : Boolean {
    val prefs = CONTEXT.defaultSharedPreferences
    return prefs.getBoolean(PREF_LOGGIN, false)
}

fun logIn() {
    val prefs = CONTEXT.defaultSharedPreferences
    prefs.edit().putBoolean(PREF_LOGGIN, true).commit()
}

fun logOut() {
    val prefs = CONTEXT.defaultSharedPreferences
    prefs.edit().putBoolean(PREF_LOGGIN, false).commit()
}

private const val PREF_LOGGIN = "PREF_LOGGIN"