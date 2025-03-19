package com.fitting.lenzdelivery

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.fitting.lenzdelivery.auth.AuthScreen
import com.fitting.lenzdelivery.navigation.MyApp
import com.fitting.lenzdelivery.network.CloudNotificationService
import com.fitting.lenzdelivery.ui.theme.LenZDeliveryTheme
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.isImmediateUpdateAllowed

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            startOrderNotificationService()
        } else {
            Toast.makeText(this, "Enable Notifications for better Experience", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private lateinit var appUpdateManager: AppUpdateManager
    private val updateType = AppUpdateType.IMMEDIATE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        checkNotificationPermission()

        appUpdateManager = AppUpdateManagerFactory.create(applicationContext)
        checkForUpdates()

        val sharedPref = getSharedPreferences("LenZDeliveryKey", Context.MODE_PRIVATE)
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
                    MyApp(
                        sharedPref = sharedPref,
                        prefEditor = prefEditor
                    )
                } else {
                    AuthScreen(
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
            val isUpdateAllowed = info.isImmediateUpdateAllowed

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

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    startOrderNotificationService()
                }

                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }

                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            startOrderNotificationService()
        }
    }

    private fun startOrderNotificationService() {
        createNotificationChannel()
        val intent = Intent(this, CloudNotificationService::class.java)
        startService(intent)
    }

    private fun createNotificationChannel() {
        val attributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .build()
        val soundUri = "android.resource://$packageName/${R.raw.vivek}".toUri()

        val channelId = "order_notifications"
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            channelId,
            "Order Notifications",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            setSound(soundUri, attributes)
        }
        notificationManager.createNotificationChannel(channel)
    }

}