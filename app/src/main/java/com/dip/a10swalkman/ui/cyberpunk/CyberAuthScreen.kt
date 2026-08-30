package com.dip.a10swalkman.ui.cyberpunk

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MusicNote
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dip.a10swalkman.supabase
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.launch

@Composable
fun CyberAuthScreen(
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
            .background(CyberColors.Void)
            .drawCyberGrid()
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Cyber Emblem
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CyberShapes.ChamferArtwork)
                    .background(CyberColors.SurfaceElevated)
                    .border(1.5.dp, CyberColors.NeonCyan, CyberShapes.ChamferArtwork)
                    .drawCyberBrackets(CyberColors.NeonCyan, bracketLength = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = null,
                    tint = CyberColors.NeonCyan,
                    modifier = Modifier.size(42.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "A10S // WALKMAN",
                color = CyberColors.TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 4.sp
            )

            Text(
                text = "NEURAL DECK AUTHENTICATION PROTOCOL",
                color = CyberColors.NeonCyanDim,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Main Auth Deck Card
            CyberCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = CyberColors.NeonCyan.copy(alpha = 0.4f),
                glowColor = CyberColors.NeonCyan
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isSignUp) ">> REGISTER_USER" else ">> SYSTEM_LOGIN",
                            color = CyberColors.NeonCyan,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 2.sp
                        )

                        CyberBadge(
                            text = if (isSignUp) "INIT_MODE" else "SECURE_LINK",
                            color = if (isSignUp) CyberColors.NeonPink else CyberColors.NeonGreen
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Email Input
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
                                "USER_ID // EMAIL",
                                color = CyberColors.TextSecondary,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp
                            )
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        shape = CyberShapes.ChamferButton,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = CyberColors.TextPrimary,
                            unfocusedTextColor = CyberColors.TextPrimary,
                            focusedBorderColor = CyberColors.NeonCyan,
                            unfocusedBorderColor = CyberColors.CardBorder,
                            focusedContainerColor = CyberColors.Surface,
                            unfocusedContainerColor = CyberColors.Surface,
                            cursorColor = CyberColors.NeonCyan
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Password Input
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
                                "CIPHER // PASSWORD",
                                color = CyberColors.TextSecondary,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp
                            )
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null,
                                    tint = CyberColors.NeonCyan
                                )
                            }
                        },
                        shape = CyberShapes.ChamferButton,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = CyberColors.TextPrimary,
                            unfocusedTextColor = CyberColors.TextPrimary,
                            focusedBorderColor = CyberColors.NeonCyan,
                            unfocusedBorderColor = CyberColors.CardBorder,
                            focusedContainerColor = CyberColors.Surface,
                            unfocusedContainerColor = CyberColors.Surface,
                            cursorColor = CyberColors.NeonCyan
                        )
                    )

                    // Confirm Password if SignUp
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
                                        "CONFIRM_CIPHER",
                                        color = CyberColors.TextSecondary,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp
                                    )
                                },
                                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                trailingIcon = {
                                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                        Icon(
                                            imageVector = if (confirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                            contentDescription = null,
                                            tint = CyberColors.NeonPink
                                        )
                                    }
                                },
                                shape = CyberShapes.ChamferButton,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = CyberColors.TextPrimary,
                                    unfocusedTextColor = CyberColors.TextPrimary,
                                    focusedBorderColor = CyberColors.NeonPink,
                                    unfocusedBorderColor = CyberColors.CardBorder,
                                    focusedContainerColor = CyberColors.Surface,
                                    unfocusedContainerColor = CyberColors.Surface,
                                    cursorColor = CyberColors.NeonPink
                                )
                            )
                        }
                    }

                    if (authMessage.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (authMessage.startsWith("SUCCESS", ignoreCase = true)) {
                                "[OK] $authMessage"
                            } else {
                                "[ERR] $authMessage"
                            },
                            color = if (authMessage.startsWith("SUCCESS", ignoreCase = true)) CyberColors.NeonGreen else CyberColors.NeonPink,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 15.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Submit Action Button
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clip(CyberShapes.ChamferButton)
                            .background(if (loading) CyberColors.SurfaceElevated else CyberColors.NeonCyan)
                            .border(1.dp, CyberColors.NeonCyan, CyberShapes.ChamferButton)
                            .clickable(enabled = !loading) {
                                val cleanEmail = email.trim()
                                if (cleanEmail.isEmpty()) {
                                    authMessage = "ENTER EMAIL IDENTIFIER"
                                    return@clickable
                                }
                                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(cleanEmail).matches()) {
                                    authMessage = "INVALID EMAIL FORMAT"
                                    return@clickable
                                }
                                if (password.isEmpty() || password.length < 6) {
                                    authMessage = "CIPHER MUST BE AT LEAST 6 CHARACTERS"
                                    return@clickable
                                }
                                if (isSignUp && password != confirmPassword) {
                                    authMessage = "CIPHERS DO NOT MATCH"
                                    return@clickable
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
                                            authMessage = "SUCCESS: LINK INITIALIZED. VERIFY EMAIL & LOG IN."
                                        } else {
                                            supabase.auth.signInWith(Email) {
                                                this.email = cleanEmail
                                                this.password = password
                                            }
                                            onLoginSuccess()
                                        }
                                    } catch (e: Exception) {
                                        authMessage = e.message ?: if (isSignUp) "SIGNUP REJECTED" else "LOGIN FAILED"
                                    } finally {
                                        loading = false
                                    }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (loading) {
                            CircularProgressIndicator(
                                color = CyberColors.NeonCyan,
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = if (isSignUp) "INITIALIZE ACCOUNT" else "AUTHENTICATE LINK",
                                color = CyberColors.Void,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 2.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Switch Mode Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isSignUp) "EXISTING USER? " else "NO CREDENTIALS? ",
                            color = CyberColors.TextSecondary,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = if (isSignUp) "[ACCESS LOGIN]" else "[CREATE ACCOUNT]",
                            color = CyberColors.NeonPink,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
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

            Spacer(modifier = Modifier.height(20.dp))

            // Offline Bypass Button
            Text(
                text = ">> BYPASS ONLINE LINK // LOCAL STORAGE DECK <<",
                color = CyberColors.TextMuted,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp,
                modifier = Modifier
                    .clip(CyberShapes.ChamferChip)
                    .clickable { onLoginSuccess() }
                    .padding(8.dp)
            )
        }
    }
}
