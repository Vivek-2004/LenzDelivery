package com.fitting.lenzdelivery.auth

import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AuthScreen(
    sharedPref: SharedPreferences,
    prefEditor: SharedPreferences.Editor
) {
    var showLoginScreen by remember { mutableStateOf(true) }

    if (showLoginScreen) {
        RiderLogIn(
            sharedPref = sharedPref,
            prefEditor = prefEditor,
            onNavigateToSignUp = { showLoginScreen = false }
        )
    } else {
        RiderSignUp(
            onNavigateToLogin = { showLoginScreen = true }
        )
    }
}