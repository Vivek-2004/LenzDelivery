package com.fitting.lenzdelivery.auth

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fitting.lenzdelivery.R
import com.fitting.lenzdelivery.models.SignUpRider
import com.fitting.lenzdelivery.network.deliveryService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

@Composable
fun RiderSignUp(
    onNavigateToLogin: () -> Unit
) {
    BackHandler {
        onNavigateToLogin()
    }

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = Color(0xFFE53935),
        unfocusedBorderColor = Color.Gray,
        focusedLabelColor = Color(0xFFE53935),
        cursorColor = Color(0xFFE53935)
    )
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var vehicleNumber by remember { mutableStateOf("") }
    var adminId by remember { mutableStateOf("") }
    var adminAuth by remember { mutableStateOf("") }
    var responseMessage by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    if (responseMessage.isNotEmpty()) {
        isLoading = false
        Toast.makeText(context, responseMessage, Toast.LENGTH_SHORT).show()
        if (responseMessage == "Rider Registered Successfully") {
            onNavigateToLogin()
        }
        responseMessage = ""
    }

    fun signUp() {
        isLoading = true
        scope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    deliveryService.riderSignUp(
                        signupBody = SignUpRider(
                            name = name,
                            phone = phone,
                            email = email,
                            password = password,
                            vehicleNumber = vehicleNumber,
                            adminId = adminId,
                            adminAuth = adminAuth
                        )
                    )
                }

                responseMessage = when (response.code()) {
                    201 -> "Rider Registered Successfully"
                    400 -> "Missing Required Fields"
                    401 -> "Incorrect Auth Key"
                    409 -> "Rider Already Exists"
                    404 -> "Admin Not Found"
                    else -> "Server Error"
                }
            } catch (e: Exception) {
                responseMessage = "Please Try again Later!"
            }
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(35.dp))

            Image(
                painter = painterResource(id = R.drawable.app_logo),
                contentDescription = "App Logo",
                modifier = Modifier.size(140.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Rider Sign Up",
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Name Field
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Enter Name") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = textFieldColors
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Phone Field
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it }, keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.NumberPassword
                ),
                label = { Text("Enter Phone") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = textFieldColors
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Email Field
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Enter Mail") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = textFieldColors,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = textFieldColors,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    val image =
                        if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = image,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password"
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = vehicleNumber,
                onValueChange = { vehicleNumber = it.uppercase(Locale.ROOT) },
                label = { Text("Enter Vehicle Number") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = textFieldColors
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                OutlinedTextField(
                    value = adminId,
                    onValueChange = { adminId = it },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.NumberPassword
                    ),
                    label = { Text("Admin ID") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = textFieldColors
                )

                Spacer(modifier = Modifier.width(13.dp))

                OutlinedTextField(
                    value = adminAuth,
                    onValueChange = { adminAuth = it },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.NumberPassword
                    ),
                    label = { Text("Auth Key") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = textFieldColors
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                enabled = !isLoading,
                onClick = {
                    if (name.isEmpty() || phone.isEmpty() || email.isEmpty() || password.isEmpty() || vehicleNumber.isEmpty() || adminId.isEmpty() || adminAuth.isEmpty()) {
                        responseMessage = "All Fields are Required"
                    } else if (name.contains(Regex("\\d"))) {
                        responseMessage = "Enter a Valid Name"
                    } else if (!phone.matches(Regex("^(0?[6-9]\\d{9})$"))) {
                        responseMessage = "Enter a Valid Mobile Number"
                    } else if (!email.matches(Regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"))) {
                        responseMessage = "Enter Valid Email"
                    } else if (adminAuth.length != 6) {
                        responseMessage = "SignUp Unauthorized"
                    } else if (!vehicleNumber.matches(Regex("^[A-Z]{2}\\d{2}[A-Z]{1,2}\\d{4}$"))) {
                        responseMessage = "Invalid Vehicle Number"
                    } else {
                        signUp()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.Black,
                        strokeWidth = 5.dp
                    )
                } else {
                    Text(
                        text = "SIGN UP",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(25.dp))

            Text(
                text = "Have an account? Sign In",
                modifier = Modifier.clickable {
                    onNavigateToLogin()
                },
                color = Color.Blue
            )

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}