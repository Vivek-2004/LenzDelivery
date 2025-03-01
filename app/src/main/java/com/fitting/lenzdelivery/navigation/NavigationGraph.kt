package com.fitting.lenzdelivery.navigation

import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.composable
import com.fitting.lenzdelivery.DeliveryViewModel
import com.fitting.lenzdelivery.DeliveryViewModelFactory
import com.fitting.lenzdelivery.screens.EarningsScreen
import com.fitting.lenzdelivery.screens.component_holders.PickupDetails
import com.fitting.lenzdelivery.screens.PickupScreen
import com.fitting.lenzdelivery.screens.ProfileScreen
import com.fitting.lenzdelivery.screens.component_holders.PaymentsHistory

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MyApp(sharedPref: SharedPreferences) {
    val riderId: String? = sharedPref.getString("riderId", "")

    val deliveryViewModelInstance: DeliveryViewModel = viewModel(
        factory = riderId?.let { id -> DeliveryViewModelFactory(id) }
    )

    val riderState by deliveryViewModelInstance.riderDetails.collectAsState()

    if (riderState == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(100.dp),
                strokeWidth = 11.dp,
                color = Color.Black.copy(alpha = 0.8f)
            )
        }
    } else {
        val navController = rememberNavController()
        val currentBackStackEntry by navController.currentBackStackEntryAsState()
        var currentScreen = currentBackStackEntry?.destination?.route

        var showBottomBar by remember { mutableStateOf(false) }
        showBottomBar = (currentScreen == NavigationDestination.Pickups.name ||
                currentScreen == NavigationDestination.Earnings.name ||
                currentScreen == NavigationDestination.Profile.name)

        Scaffold(
            topBar = {
                Column {
                    TopAppBar(
                        navController = navController,
                        currentScreenName = currentScreen ?: ""
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    HorizontalDivider(color = Color.Black.copy(alpha = 0.2f))
                }
            },
            bottomBar = {
                AnimatedVisibility(
                    visible = showBottomBar,
                    enter = expandVertically(
                        animationSpec = tween(durationMillis = 500),
                        expandFrom = Alignment.Top
                    ) + fadeIn(animationSpec = tween(durationMillis = 500)),
                    exit = shrinkVertically(
                        animationSpec = tween(durationMillis = 500),
                        shrinkTowards = Alignment.Top
                    ) + fadeOut(animationSpec = tween(durationMillis = 500))
                ) {
                    BottomNavigationBar(
                        navController = navController,
                        onTitleChange = {
                            currentScreen = it
                        }
                    )
                }
            }
        ) { innerPadding ->
            NavHost(
                modifier = Modifier.padding(innerPadding),
                navController = navController,
                startDestination = NavigationDestination.Pickups.name
            ) {
                composable(route = NavigationDestination.Pickups.name) {
                    PickupScreen(
                        deliveryViewModel = deliveryViewModelInstance,
                        navController = navController
                    )
                }

                composable(route = NavigationDestination.Earnings.name) {
                    EarningsScreen(
                        deliveryViewModel = deliveryViewModelInstance,
                        navController = navController
                    )
                }
                composable(route = NavigationDestination.Profile.name) {
                    ProfileScreen(
                        deliveryViewModel = deliveryViewModelInstance
                    )
                }
                composable(route = NavigationDestination.PaymentsHistory.name) {
                    PaymentsHistory(
                        deliveryViewModel = deliveryViewModelInstance
                    )
                }
                composable(route = NavigationDestination.PickupDetails.name + "/{orderKey}") { backStackEntry ->
                    PickupDetails(
                        deliveryViewModel = deliveryViewModelInstance,
                        orderKey = backStackEntry.arguments?.getString("orderKey") ?: "",
                    )
                }
            }
        }
    }
}