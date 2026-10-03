package com.smritisetu.app.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.smritisetu.app.data.AlertItem
import com.smritisetu.app.data.AlertSeverity
import com.smritisetu.app.data.AlertsRepository
import com.smritisetu.app.data.Localization
import com.smritisetu.app.ui.AnimatedGradientBox
import com.smritisetu.app.ui.AnimatedScreen
import com.smritisetu.app.ui.AppGradients
import com.smritisetu.app.ui.SmritiButton

@Composable
fun PatientLoginScreen(navController: NavController) {
    val context = LocalContext.current
    val s = Localization.strings()

    var isLoginTab by remember { mutableStateOf(true) }
    var nameInput by remember { mutableStateOf(s.patientName) } // Default pre-filled
    var passwordInput by remember { mutableStateOf("12345") } // Default pre-filled
    var passwordVisible by remember { mutableStateOf(false) }
    var showForgotPasswordDialog by remember { mutableStateOf(false) }

    AnimatedScreen {
        AnimatedGradientBox(colors = AppGradients.patientColors) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = { navController.popBackStack() }) {
                        Text("← ${s.back}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Patient Avatar
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text("👴", style = MaterialTheme.typography.displayMedium)
                }

                Spacer(Modifier.height(16.dp))

                Text(
                    text = if (isLoginTab) s.patientLoginTitle else s.patientSignUpTitle,
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "${s.appName} • ${s.welcomeMsg}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )

                Spacer(Modifier.height(24.dp))

                Card(
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(6.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        // Toggle Login / Signup
                        TabRow(
                            selectedTabIndex = if (isLoginTab) 0 else 1,
                            containerColor = Color.Transparent
                        ) {
                            Tab(
                                selected = isLoginTab,
                                onClick = { isLoginTab = true },
                                text = { Text(s.loginTab, fontWeight = FontWeight.Bold) }
                            )
                            Tab(
                                selected = !isLoginTab,
                                onClick = { isLoginTab = false },
                                text = { Text(s.signUpTab, fontWeight = FontWeight.Bold) }
                            )
                        }

                        Spacer(Modifier.height(20.dp))

                        // Field 1: Patient Name
                        OutlinedTextField(
                            value = nameInput,
                            onValueChange = { nameInput = it },
                            label = { Text(s.patientNameLabel) },
                            placeholder = { Text(s.patientName) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        Spacer(Modifier.height(16.dp))

                        // Field 2: Password
                        OutlinedTextField(
                            value = passwordInput,
                            onValueChange = { passwordInput = it },
                            label = { Text(s.passwordLabel) },
                            placeholder = { Text("12345") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                        contentDescription = "Toggle Password"
                                    )
                                }
                            }
                        )

                        Spacer(Modifier.height(12.dp))

                        // Forgot Password Link
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = { showForgotPasswordDialog = true }) {
                                Text(
                                    s.forgotPassword,
                                    color = MaterialTheme.colorScheme.secondary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        // Login / Signup Button
                        SmritiButton(
                            text = if (isLoginTab) s.enterApp else s.registerPatient,
                            onClick = {
                                if (nameInput.isBlank() || passwordInput.isBlank()) {
                                    Toast.makeText(context, s.fillDetails, Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "${s.goodMorning}, $nameInput! 🙏", Toast.LENGTH_SHORT).show()
                                    navController.navigate("patient_home") {
                                        popUpTo("welcome") { inclusive = true }
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp),
                            containerColor = MaterialTheme.colorScheme.secondary
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        s.demoCredentials,
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE65100)
                    )
                }
            }
        }
    }

    if (showForgotPasswordDialog) {
        AlertDialog(
            onDismissRequest = { showForgotPasswordDialog = false },
            title = { Text(s.forgotPassword) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(s.helpYouRemember)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Caregiver: ${s.nameSunita}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        AlertsRepository.pushNewAlert(
                            AlertItem(
                                id = System.currentTimeMillis().toInt(),
                                icon = "🔑",
                                title = "Password Reset Request",
                                description = "$nameInput requested a password reset from the login screen.",
                                time = "Just now",
                                severity = AlertSeverity.MEDIUM
                            )
                        )
                        Toast.makeText(context, "Request sent!", Toast.LENGTH_LONG).show()
                        showForgotPasswordDialog = false
                    }
                ) {
                    Text(s.askCaregiver)
                }
            },
            dismissButton = {
                TextButton(onClick = { showForgotPasswordDialog = false }) {
                    Text(s.exit)
                }
            }
        )
    }
}

@Composable
fun CaregiverLoginScreen(navController: NavController) {
    val context = LocalContext.current
    val s = Localization.strings()

    var isLoginTab by remember { mutableStateOf(true) }
    var caregiverIdInput by remember { mutableStateOf("123456") } // Default pre-filled: 123456
    var passwordInput by remember { mutableStateOf("123456") } // Default pre-filled: 123456
    var passwordVisible by remember { mutableStateOf(false) }
    var showForgotIdDialog by remember { mutableStateOf(false) }

    AnimatedScreen {
        AnimatedGradientBox(colors = AppGradients.caregiverColors) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = { navController.popBackStack() }) {
                        Text("← ${s.back}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                }

                Spacer(Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text("👨‍👩‍👧", style = MaterialTheme.typography.displayMedium)
                }

                Spacer(Modifier.height(16.dp))

                Text(
                    text = if (isLoginTab) s.caregiverLoginTitle else s.caregiverSignUpTitle,
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "${s.appName} • Caregiver Portal",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )

                Spacer(Modifier.height(24.dp))

                Card(
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(6.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        TabRow(
                            selectedTabIndex = if (isLoginTab) 0 else 1,
                            containerColor = Color.Transparent
                        ) {
                            Tab(
                                selected = isLoginTab,
                                onClick = { isLoginTab = true },
                                text = { Text(s.loginTab, fontWeight = FontWeight.Bold) }
                            )
                            Tab(
                                selected = !isLoginTab,
                                onClick = { isLoginTab = false },
                                text = { Text(s.signUpTab, fontWeight = FontWeight.Bold) }
                            )
                        }

                        Spacer(Modifier.height(20.dp))

                        // Field 1: Caregiver ID
                        OutlinedTextField(
                            value = caregiverIdInput,
                            onValueChange = { caregiverIdInput = it },
                            label = { Text(s.caregiverIdLabel) },
                            placeholder = { Text("123456") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )

                        Spacer(Modifier.height(16.dp))

                        // Field 2: Password
                        OutlinedTextField(
                            value = passwordInput,
                            onValueChange = { passwordInput = it },
                            label = { Text(s.passwordLabel) },
                            placeholder = { Text("123456") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                        contentDescription = "Toggle Password"
                                    )
                                }
                            }
                        )

                        Spacer(Modifier.height(12.dp))

                        // Forgot ID Link
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = { showForgotIdDialog = true }) {
                                Text(
                                    s.forgotCaregiverId,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        // Login / Register Button
                        SmritiButton(
                            text = if (isLoginTab) s.enterDashboard else s.registerCaregiver,
                            onClick = {
                                if (caregiverIdInput.isBlank() || passwordInput.isBlank()) {
                                    Toast.makeText(context, s.fillDetails, Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Authenticated as ${s.nameSunita}!", Toast.LENGTH_SHORT).show()
                                    navController.navigate("caregiver_section") {
                                        popUpTo("welcome") { inclusive = true }
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp),
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        s.caregiverDemoCredentials,
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32)
                    )
                }
            }
        }
    }

    if (showForgotIdDialog) {
        AlertDialog(
            onDismissRequest = { showForgotIdDialog = false },
            title = { Text(s.forgotCaregiverId) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("${s.daughterLabel}: ${s.nameSunita}")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        Toast.makeText(context, "OTP Sent!", Toast.LENGTH_LONG).show()
                        showForgotIdDialog = false
                    }
                ) {
                    Text(s.sendSmsOtp)
                }
            },
            dismissButton = {
                TextButton(onClick = { showForgotIdDialog = false }) {
                    Text(s.exit)
                }
            }
        )
    }
}
