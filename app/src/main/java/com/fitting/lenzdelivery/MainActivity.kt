package com.fitting.lenzdelivery

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.fitting.lenzdelivery.auth.RiderLogIn
import com.fitting.lenzdelivery.navigation.MyApp
import com.fitting.lenzdelivery.ui.theme.LenZDeliveryTheme
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.isFlexibleUpdateAllowed
import com.google.android.play.core.ktx.isImmediateUpdateAllowed

class MainActivity : ComponentActivity() {

    private lateinit var appUpdateManager: AppUpdateManager
    private val updateType = AppUpdateType.IMMEDIATE

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        appUpdateManager = AppUpdateManagerFactory.create(applicationContext)
        checkForUpdates()

        val sharedPref = getSharedPreferences("LenZDelivery", Context.MODE_PRIVATE)
        val prefEditor = sharedPref.edit()

        if (!sharedPref.contains("isLoggedIn")) {
            prefEditor.putBoolean("isLoggedIn", false)
            prefEditor.apply()
        }

        setContent {
            val isLoggedIn by remember {
                mutableStateOf(
                    sharedPref.getBoolean(
                        "isLoggedIn",
                        false
                    )
                )
            }
            LenZDeliveryTheme(darkTheme = false) {
                if (isLoggedIn) {
                    MyApp(sharedPref = sharedPref)
                } else {
                    RiderLogIn(
                        sharedPref = sharedPref,
                        prefEditor = prefEditor
                    )
                }
            }
        }
    }

    private fun checkForUpdates() {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->
            val isUpdateAvailable = info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
            val isUpdateAllowed = when (updateType) {
                AppUpdateType.FLEXIBLE -> info.isFlexibleUpdateAllowed
                AppUpdateType.IMMEDIATE -> info.isImmediateUpdateAllowed
                else -> false
            }
            if (isUpdateAvailable && isUpdateAllowed) {
                appUpdateManager.startUpdateFlowForResult(
                    info,
                    updateType,
                    this,
                    123
                )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 123) {
            if (resultCode != RESULT_OK) {
                println("Something Went Wrong")
            }
        }
    }
}