package com.dip.a10swalkman.ui.swiss

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dip.a10swalkman.supabase
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.launch

@Composable
fun SwissAuthScreen(
    onLoginSuccess: () -> Unit
) {
    val scope = rememberCoroutineScope()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var isSignUp by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }
    var authMessage by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SwissColors.Black)
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            // Editorial Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "A10S",
                    color = SwissColors.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Text(
                    text = " / ",
                    color = SwissColors.Accent,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "WALKMAN",
                    color = SwissColors.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
            }

            Text(
                text = "SWISS MONOCHROMATIC SOUND SYSTEM — RELEASE 2.0",
                color = SwissColors.GrayMid,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Auth Card
            SwissCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isSignUp) "CREATE ACCOUNT" else "ACCOUNT LOGIN",
                            color = SwissColors.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )

                        SwissBadge(
                            text = if (isSignUp) "REGISTER" else "SECURE",
                            hasAccentDot = true
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Email Field
                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            authMessage = ""
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        label = {
                            Text(
                                "Email Address",
                                color = SwissColors.GrayMid,
                                fontSize = 12.sp
                            )
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        shape = RoundedCornerShape(2.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = SwissColors.White,
                            unfocusedTextColor = SwissColors.White,
                            focusedBorderColor = SwissColors.White,
                            unfocusedBorderColor = SwissColors.HairlineLight,
                            focusedContainerColor = SwissColors.Dark,
                            unfocusedContainerColor = SwissColors.Dark,
                            cursorColor = SwissColors.White
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Password Field
                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            authMessage = ""
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        label = {
                            Text(
                                "Password",
                                color = SwissColors.GrayMid,
                                fontSize = 12.sp
                            )
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null,
                                    tint = SwissColors.GrayLight
                                )
                            }
                        },
                        shape = RoundedCornerShape(2.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = SwissColors.White,
                            unfocusedTextColor = SwissColors.White,
                            focusedBorderColor = SwissColors.White,
                            unfocusedBorderColor = SwissColors.HairlineLight,
                            focusedContainerColor = SwissColors.Dark,
                            unfocusedContainerColor = SwissColors.Dark,
                            cursorColor = SwissColors.White
                        )
                    )

                    // Confirm Password
                    AnimatedVisibility(visible = isSignUp) {
                        Column {
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedTextField(
                                value = confirmPassword,
                                onValueChange = {
                                    confirmPassword = it
                                    authMessage = ""
                                },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                label = {
                                    Text(
                                        "Confirm Password",
                                        color = SwissColors.GrayMid,
                                        fontSize = 12.sp
                                    )
                                },
                                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                trailingIcon = {
                                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                        Icon(
                                            imageVector = if (confirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                            contentDescription = null,
                                            tint = SwissColors.GrayLight
                                        )
                                    }
                                },
                                shape = RoundedCornerShape(2.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = SwissColors.White,
                                    unfocusedTextColor = SwissColors.White,
                                    focusedBorderColor = SwissColors.White,
                                    unfocusedBorderColor = SwissColors.HairlineLight,
                                    focusedContainerColor = SwissColors.Dark,
                                    unfocusedContainerColor = SwissColors.Dark,
                                    cursorColor = SwissColors.White
                                )
                            )
                        }
                    }

                    if (authMessage.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = authMessage,
                            color = if (authMessage.startsWith("SUCCESS", ignoreCase = true)) SwissColors.White else SwissColors.Accent,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Submit Button
                    SwissButton(
                        text = if (isSignUp) "Register Account" else "Authenticate",
                        isPrimary = true,
                        enabled = !loading,
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            val cleanEmail = email.trim()
                            if (cleanEmail.isEmpty()) {
                                authMessage = "Please enter your email."
                                return@SwissButton
                            }
                            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(cleanEmail).matches()) {
                                authMessage = "Please enter a valid email."
                                return@SwissButton
                            }
                            if (password.isEmpty() || password.length < 6) {
                                authMessage = "Password must be at least 6 characters."
                                return@SwissButton
                            }
                            if (isSignUp && password != confirmPassword) {
                                authMessage = "Passwords do not match."
                                return@SwissButton
                            }

                            loading = true
                            scope.launch {
                                try {
                                    if (isSignUp) {
                                        supabase.auth.signUpWith(Email) {
                                            this.email = cleanEmail
                                            this.password = password
                                        }
                                        email = ""
                                        password = ""
                                        confirmPassword = ""
                                        isSignUp = false
                                        authMessage = "SUCCESS: Account created. Verify email and log in."
                                    } else {
                                        supabase.auth.signInWith(Email) {
                                            this.email = cleanEmail
                                            this.password = password
                                        }
                                        onLoginSuccess()
                                    }
                                } catch (e: Exception) {
                                    authMessage = e.message ?: if (isSignUp) "Registration failed" else "Sign in failed"
                                } finally {
                                    loading = false
                                }
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Switch Mode
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isSignUp) "Already registered? " else "Need an account? ",
                            color = SwissColors.GrayMid,
                            fontSize = 11.sp
                        )
                        Text(
                            text = if (isSignUp) "Sign In" else "Register",
                            color = SwissColors.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable {
                                if (!loading) {
                                    isSignUp = !isSignUp
                                    authMessage = ""
                                }
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Offline Bypass
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onLoginSuccess() },
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Continue with Offline Local Archive →",
                    color = SwissColors.GrayMid,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
