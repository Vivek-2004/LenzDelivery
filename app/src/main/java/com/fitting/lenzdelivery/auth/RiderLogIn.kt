package com.fitting.lenzdelivery.auth

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fitting.lenzdelivery.R
import com.fitting.lenzdelivery.models.LogInRider
import com.fitting.lenzdelivery.navigation.MyApp
import com.fitting.lenzdelivery.network.deliveryService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RiderLogIn(
    sharedPref: SharedPreferences,
    prefEditor: SharedPreferences.Editor
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var riderMail by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var loginConfirmation by remember { mutableStateOf(false) }
    val MIN_LOADING_DISPLAY_TIME = 1300L
    var loginMessage by remember { mutableStateOf("") }
    var confirmation = (loginMessage == "Login Successful")
    var riderId by remember { mutableStateOf("") }

    if(confirmation) {
            prefEditor.putString("riderId", riderId).apply()
    }

    LaunchedEffect(confirmation) {
        if (!confirmation) return@LaunchedEffect
            prefEditor.putBoolean("isLoggedIn", true).apply()
            loginConfirmation = sharedPref.getBoolean("isLoggedIn", false)
            confirmation = false
    }

    LaunchedEffect(loginMessage) {
        when {
            (loginMessage == "Login Successful") -> { }
            loginMessage.isNotEmpty() -> {
                Toast.makeText(context, loginMessage, Toast.LENGTH_SHORT).show()
                loginMessage = ""
            }
        }
    }

    if (loginConfirmation) {
        MyApp(
            sharedPref = sharedPref
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(35.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                modifier = Modifier.size(200.dp),
                painter = painterResource(id = R.drawable.app_logo),
                tint = Color.Black,
                contentDescription = "App Logo"
            )

            Text(
                text = "Sign In to Continue",
                fontSize = 17.sp,
                modifier = Modifier.padding(bottom = 4.dp),
                color = Color.Black
            )

            OutlinedTextField(
                value = riderMail,
                onValueChange = { riderMail = it },
                label = { Text("Enter Mail", color = Color.Black) },
                placeholder = { Text("Enter Rider Mail", color = Color.Black) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password", color = Color.Black) },
                placeholder = { Text("Enter password", color = Color.Black) },
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                trailingIcon = {
                    PasswordVisibilityToggle(
                        isVisible = isPasswordVisible,
                        onToggle = { isPasswordVisible = !isPasswordVisible }
                    )
                }
            )

            if (riderMail.isNotEmpty() && password.isNotEmpty()) {
                LoginButton(
                    isLoading = isLoading,
                    onClick = {
                        if(riderMail.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$".toRegex())) {
                            scope.launch {
                                val startTime = System.currentTimeMillis()
                                isLoading = true
                                try {
                                    withContext(Dispatchers.IO) {
                                        val response = deliveryService.logInRider(
                                            loginBody = LogInRider(
                                                riderEmail = riderMail,
                                                password = password
                                            )
                                        )
                                        riderId = response.riderId
                                        loginMessage = response.message
                                    }
                                } catch (e: Exception) {
                                    loginMessage = "Invalid ID or Password"
                                } finally {
                                    val elapsedTime = System.currentTimeMillis() - startTime
                                    if (elapsedTime < MIN_LOADING_DISPLAY_TIME) {
                                        delay(MIN_LOADING_DISPLAY_TIME - elapsedTime)
                                    }
                                    isLoading = false
                                }
                            }
                        } else {
                            Toast.makeText(context, "Enter Valid Email", Toast.LENGTH_SHORT).show()
                            riderMail = ""
                        }
                    }
                )
            }
            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}

@Composable
private fun PasswordVisibilityToggle(
    isVisible: Boolean,
    onToggle: () -> Unit
) {
    IconButton(onClick = onToggle) {
        Icon(
            painter = if (isVisible) painterResource(R.drawable.show_password)
            else painterResource(R.drawable.hide_password),
            contentDescription = if (isVisible) "Hide password" else "Show password",
            tint = Color.Black,
            modifier = Modifier.size(28.dp)
        )
    }
}

@Composable
private fun LoginButton(
    isLoading: Boolean,
    onClick: () -> Unit
) {
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .padding(top = 12.dp),
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Black.copy(alpha = 0.9f),
            contentColor = Color.White
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(32.dp),
                strokeWidth = 4.5.dp,
                color = Color.White
            )
        } else {
            Text(
                text = "SIGN IN",
                fontWeight = FontWeight.Black,
                fontSize = 22.sp,
                letterSpacing = 1.2.sp
            )
        }
    }
}