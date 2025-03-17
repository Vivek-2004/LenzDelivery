package com.fitting.lenzdelivery.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.fitting.lenzdelivery.R

@Composable
fun TopAppBar(
    navController: NavController,
    currentScreenName: String
) {
    val title = when (currentScreenName) {
        "PaymentsHistory" -> "History"
        "PickupDetails/{orderKey}" -> "Details"
        else -> currentScreenName
    }

    var showNavigationIcon by remember { mutableStateOf(false) }
    showNavigationIcon = (
            title != NavigationDestination.Pickups.name &&
                    title != NavigationDestination.Earnings.name &&
                    title != NavigationDestination.Profile.name
            )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .background(Color.Black)
    ) {
        if (showNavigationIcon) {
            IconButton(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(vertical = 6.dp),
                onClick = { navController.popBackStack() }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Back",
                    modifier = Modifier.size(36.dp),
                    tint = Color.White
                )
            }
        }

        Text(
            text = title,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomEnd)
                .padding(vertical = 12.dp, horizontal = if (showNavigationIcon) 64.dp else 24.dp),
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            color = Color.White,
            textAlign = TextAlign.Left,
            fontFamily = FontFamily.Default
        )
    }
}

@Composable
fun BottomNavigationBar(
    navController: NavController,
    onTitleChange: (String) -> Unit
) {
    val currentDestination = navController.currentBackStackEntryAsState().value?.destination
    val items = listOf(
        NavigationDestination.Pickups.name,
        NavigationDestination.Earnings.name,
        NavigationDestination.Profile.name
    )
    Column(modifier = Modifier.wrapContentSize()) {
        HorizontalDivider(thickness = 1.dp, color = Color.DarkGray)
        NavigationBar(
            modifier = Modifier
                .navigationBarsPadding()
                .height(80.dp),
            containerColor = Color.Black
        ) {
            items.forEach { screen ->
                val selected = currentDestination?.route == screen
                val icon = when (screen) {
                    NavigationDestination.Pickups.name -> painterResource(R.drawable.pickup)
                    NavigationDestination.Earnings.name -> painterResource(R.drawable.earnings)
                    NavigationDestination.Profile.name -> painterResource(R.drawable.profile)
                    else -> painterResource(R.drawable.earnings)
                }
                NavigationBarItem(
                    selected = selected,
                    interactionSource = remember { MutableInteractionSource() },
                    icon = {
                        Icon(
                            modifier = if (selected) Modifier.size(30.dp) else Modifier.size(25.dp),  // Slightly reduced size
                            painter = icon,
                            contentDescription = screen,
                            tint = Color.Unspecified
                        )
                    },
                    label = {
                        Text(
                            text = screen,
                            fontSize = if (selected) 16.sp else 13.5.sp,
                            fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Normal,
                            color = if (selected) Color.White else Color.LightGray.copy(alpha = 0.7f)
                        )
                    },
                    onClick = {
                        onTitleChange(screen)
                        navController.navigate(screen) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        unselectedIconColor = Color.LightGray.copy(alpha = 0.7f),
                        selectedTextColor = Color.White,
                        unselectedTextColor = Color.LightGray.copy(alpha = 0.7f),
                        indicatorColor = Color.Gray
                    )
                )
            }
        }
    }
}