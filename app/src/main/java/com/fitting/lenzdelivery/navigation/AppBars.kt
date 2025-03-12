package com.fitting.lenzdelivery.navigation

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.fitting.lenzdelivery.R

@OptIn(ExperimentalMaterial3Api::class)
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

    TopAppBar(
        title = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        end = if (showNavigationIcon) 50.dp else 20.dp
                    ),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 40.sp,
                    color = Color.LightGray
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.DarkGray
        ),
        navigationIcon = {
            if (showNavigationIcon) {
                IconButton(
                    modifier = Modifier.padding(start = 8.dp),
                    onClick = { navController.popBackStack() }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back Button",
                        modifier = Modifier.size(30.dp),
                        tint = Color.White
                    )
                }
            }
        }
    )
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
        HorizontalDivider(thickness = 2.dp, color = Color.Gray)

        NavigationBar(
            modifier = Modifier
                .navigationBarsPadding()
                .height(80.dp),
            containerColor = Color.DarkGray
        ) {
            items.forEach { screen ->
                val selected = currentDestination?.route == screen

                val icon = when (screen) {
                    NavigationDestination.Pickups.name -> ImageVector.vectorResource(R.drawable.pickup)
                    NavigationDestination.Earnings.name -> ImageVector.vectorResource(R.drawable.earnings)
                    NavigationDestination.Profile.name -> ImageVector.vectorResource(R.drawable.profile)
                    else -> ImageVector.vectorResource(R.drawable.earnings)
                }

                NavigationBarItem(
                    selected = selected,
                    interactionSource = remember { MutableInteractionSource() },
                    icon = {
                        Icon(
                            modifier = if (selected) Modifier.size(30.dp) else Modifier.size(25.dp),
                            imageVector = icon,
                            contentDescription = screen,
                            tint = Color.Unspecified
                        )
                    },
                    label = {
                        Text(
                            text = screen,
                            fontSize = if (selected) 16.sp else 13.5.sp,
                            fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Bold,
                            color = if (selected) Color.White else Color.LightGray
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
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        indicatorColor = Color.LightGray.copy(alpha = 0.7f),
                    )
                )
            }
        }
    }
}