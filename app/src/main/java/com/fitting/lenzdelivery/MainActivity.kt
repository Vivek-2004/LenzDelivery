package com.fitting.lenzdelivery

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fitting.lenzdelivery.auth.RiderLogIn
import com.fitting.lenzdelivery.navigation.MyApp
import com.fitting.lenzdelivery.ui.theme.LenZDeliveryTheme

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val sharedPref = getSharedPreferences("LenzDelivery", Context.MODE_PRIVATE)
        val prefEditor = sharedPref.edit()

        if (!sharedPref.contains("isLoggedIn")) {
            prefEditor.putBoolean("isLoggedIn", false)
            prefEditor.apply()
        }

        setContent {
            val deliveryViewModelInstance: DeliveryViewModel = viewModel()
            val isLoggedIn by remember { mutableStateOf(sharedPref.getBoolean("isLoggedIn", false)) }
            LenZDeliveryTheme(darkTheme = false) {
                if (isLoggedIn) {
                    MyApp(
                        deliveryViewModelInstance = deliveryViewModelInstance
                    )
                } else {
                    RiderLogIn(
                        deliveryViewModel = deliveryViewModelInstance,
                        sharedPref = sharedPref,
                        prefEditor = prefEditor
                    )
                }
            }
        }
    }
}