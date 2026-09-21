package com.smritisetu.app.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.smritisetu.app.data.PatientViewModel

@Composable
fun AddPatientScreen(
    navController: NavController,
    viewModel: PatientViewModel = viewModel()
) {
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Male") }
    var address by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var genderMenuExpanded by remember { mutableStateOf(false) }

            Column(
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = { navController.popBackStack() }) { Text("Back") }
                }
                Spacer(Modifier.height(24.dp))

                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Patient Name") },
                        )
                        Spacer(Modifier.height(16.dp))

                        OutlinedTextField(
                            value = age,
                            onValueChange = { if (it.all { ch -> ch.isDigit() }) age = it },
                            label = { Text("Age") },
                        )
                        Spacer(Modifier.height(16.dp))

                        // Gender dropdown
                        Box {
                            OutlinedTextField(
                                value = gender,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Gender") },
                                modifier = Modifier.fillMaxWidth(),
                                trailingIcon = {
                                    TextButton(onClick = { genderMenuExpanded = true }) { Text("▼") }
                                }
                            )
                                listOf("Male", "Female", "Other").forEach { option ->
                                    DropdownMenuItem(text = { Text(option) }, onClick = {
                                        gender = option
                                        genderMenuExpanded = false
                                    })
                                }
                            }
                        }
                        Spacer(Modifier.height(16.dp))

                        OutlinedTextField(
                            value = address,
                            onValueChange = { address = it },
                            label = { Text("Address") },
                        )
                        Spacer(Modifier.height(16.dp))

                        OutlinedTextField(
                            value = phone,
                            onValueChange = { if (it.all { ch -> ch.isDigit() } && it.length <= 10) phone = it },
                            label = { Text("Phone Number") },
                        )
                Spacer(Modifier.height(32.dp))

                    onClick = {
                        if (name.isBlank() || age.isBlank() || address.isBlank() || phone.isBlank()) {
                            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                        } else {
                            viewModel.addPatient(
                                name = name,
                                age = age.toIntOrNull() ?: 0,
                                gender = gender,
                                address = address,
                                phone = phone
                            )
                            Toast.makeText(context, "Patient saved successfully!", Toast.LENGTH_SHORT).show()
                            navController.navigate("patient_list") {
                                popUpTo("caregiver_section")
                            }
                        }
                    },
            }
        }
    }