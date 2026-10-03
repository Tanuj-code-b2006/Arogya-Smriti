package com.smritisetu.app.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.smritisetu.app.data.ConsentSettings
import com.smritisetu.app.data.EncryptedMedicalRecord
import com.smritisetu.app.data.SecurePatientDataManager
import com.smritisetu.app.data.SecurityAuditLog
import com.smritisetu.app.ui.AnimatedGradientBox
import com.smritisetu.app.ui.AnimatedScreen
import com.smritisetu.app.ui.AppGradients
import com.smritisetu.app.ui.SmritiButton

@Composable
fun SecurePatientDataScreen(navController: NavController) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }
    var showAddDialog by remember { mutableStateOf(false) }

    AnimatedScreen {
        AnimatedGradientBox(colors = AppGradients.caregiverColors) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "🛡️ Secure Data Vault",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Patient Data Security & EHR System",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                    TextButton(onClick = { navController.popBackStack() }) {
                        Text("Back", fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Status Banner
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1B5E20).copy(alpha = 0.9f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🔒", style = MaterialTheme.typography.titleLarge)
                            Spacer(Modifier.width(8.dp))
                            Column {
                                Text(
                                    "AES-256 KeyStore Active",
                                    color = Color.White,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "DPDP Act 2023 & HIPAA Compliant",
                                    color = Color(0xFFA5D6A7),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF2E7D32))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                "ABHA LINKED",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                if (!SecurePatientDataManager.isUnlocked) {
                    // Lock Screen
                    PinLockView()
                } else {
                    // Unlocked Content
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            FilterChip(
                                selected = SecurePatientDataManager.isAnonymizedMode,
                                onClick = {
                                    SecurePatientDataManager.toggleAnonymization(!SecurePatientDataManager.isAnonymizedMode)
                                },
                                label = {
                                    Text(
                                        if (SecurePatientDataManager.isAnonymizedMode) "🎭 Anonymized PII"
                                        else "👁️ Normal View"
                                    )
                                }
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { showAddDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text("➕ Add Record", fontSize = 13.sp)
                            }

                            OutlinedButton(
                                onClick = { SecurePatientDataManager.lockVault() },
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text("🔒 Lock", fontSize = 13.sp)
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    // Tab Selector
                    ScrollableTabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.primary,
                        edgePadding = 0.dp
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = { Text("📂 Records (${SecurePatientDataManager.medicalRecords.size})") }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = { Text("⚖️ Privacy & Consent") }
                        )
                        Tab(
                            selected = selectedTab == 2,
                            onClick = { selectedTab = 2 },
                            text = { Text("📜 Audit Logs (${SecurePatientDataManager.auditLogs.size})") }
                        )
                        Tab(
                            selected = selectedTab == 3,
                            onClick = { selectedTab = 3 },
                            text = { Text("📤 E2E Export") }
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    when (selectedTab) {
                        0 -> EncryptedRecordsTab()
                        1 -> PrivacyConsentTab()
                        2 -> SecurityAuditLogsTab()
                        3 -> EncryptedExportTab()
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddRecordDialog(onDismiss = { showAddDialog = false })
    }
}

@Composable
fun PinLockView() {
    var pinInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Card(
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE8F5E9)),
                contentAlignment = Alignment.Center
            ) {
                Text("🛡️", style = MaterialTheme.typography.headlineLarge)
            }

            Spacer(Modifier.height(16.dp))

            Text(
                "Protected Health Information Vault",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Text(
                "Enter Caregiver 4-Digit Security PIN to access encrypted medical records",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(20.dp))

            // PIN Dots Display
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(4) { idx ->
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(
                                if (idx < pinInput.length) MaterialTheme.colorScheme.primary
                                else Color.LightGray.copy(alpha = 0.5f)
                            )
                            .border(1.dp, MaterialTheme.colorScheme.primary, CircleShape)
                    )
                }
            }

            if (errorMessage != null) {
                Spacer(Modifier.height(8.dp))
                Text(errorMessage!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(Modifier.height(24.dp))

            // Custom Keypad
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val keys = listOf(
                    listOf("1", "2", "3"),
                    listOf("4", "5", "6"),
                    listOf("7", "8", "9"),
                    listOf("C", "0", "✓")
                )

                keys.forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        row.forEach { key ->
                            Surface(
                                onClick = {
                                    errorMessage = null
                                    when (key) {
                                        "C" -> if (pinInput.isNotEmpty()) pinInput = pinInput.dropLast(1)
                                        "✓" -> {
                                            if (!SecurePatientDataManager.verifyPin(pinInput)) {
                                                errorMessage = "Incorrect PIN! (Default is 1234)"
                                                pinInput = ""
                                            }
                                        }
                                        else -> {
                                            if (pinInput.length < 4) {
                                                pinInput += key
                                                if (pinInput.length == 4) {
                                                    if (!SecurePatientDataManager.verifyPin(pinInput)) {
                                                        errorMessage = "Incorrect PIN! (Default is 1234)"
                                                        pinInput = ""
                                                    }
                                                }
                                            }
                                        }
                                    }
                                },
                                shape = CircleShape,
                                color = if (key == "✓") MaterialTheme.colorScheme.primary else Color(0xFFF0F0F0),
                                modifier = Modifier.size(60.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        key,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = if (key == "✓") Color.White else Color.Black
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // Biometrics Demo Button
            OutlinedButton(
                onClick = { SecurePatientDataManager.authenticateBiometrics() },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("👆 Authenticate via Biometrics (Fingerprint / Face)")
            }

            Spacer(Modifier.height(8.dp))
            Text(
                "💡 Demo PIN: 1234",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun EncryptedRecordsTab() {
    val records = SecurePatientDataManager.medicalRecords

    if (records.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No medical records in vault.")
        }
    } else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(records, key = { it.id }) { record ->
                RecordCard(record)
            }
        }
    }
}

@Composable
fun RecordCard(record: EncryptedMedicalRecord) {
    var isDecrypted by remember { mutableStateOf(record.isDecrypted) }

    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(3.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            record.category,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    if (record.isConfidential) {
                        Spacer(Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFFFEBEE))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                "🔒 STRICT CONFIDENTIAL",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFC62828)
                            )
                        }
                    }
                }

                Text(record.date, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }

            Spacer(Modifier.height(8.dp))

            Text(
                SecurePatientDataManager.maskPII(record.title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                "Attending: ${SecurePatientDataManager.maskPII(record.doctorName)}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )

            Spacer(Modifier.height(10.dp))

            if (!isDecrypted) {
                // Cipher Text Box
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF263238)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            "🔐 AES-256 CIPHER PAYLOAD:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF80CBC4),
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            record.cipherText,
                            color = Color(0xFFB0BEC5),
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                Button(
                    onClick = {
                        isDecrypted = true
                        SecurePatientDataManager.toggleDecryptRecord(record.id)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("🔓 Decrypt Record Payload")
                }
            } else {
                // Decrypted Payload View
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "✅ DECRYPTED HEALTH RECORD",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E7D32)
                            )
                            Text(
                                "Hardware Verified",
                                fontSize = 10.sp,
                                color = Color(0xFF388E3C),
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(
                            SecurePatientDataManager.maskPII(record.decryptedContent),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF1B5E20)
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                OutlinedButton(
                    onClick = {
                        isDecrypted = false
                        SecurePatientDataManager.toggleDecryptRecord(record.id)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("🔒 Re-Encrypt Record")
                }
            }
        }
    }
}

@Composable
fun PrivacyConsentTab() {
    var consent by remember { mutableStateOf(SecurePatientDataManager.consentSettings) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Patient Identifiers & Masking Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)),
            elevation = CardDefaults.cardElevation(3.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("👤 Patient PII Redaction Status", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Switch(
                        checked = SecurePatientDataManager.isAnonymizedMode,
                        onCheckedChange = { SecurePatientDataManager.toggleAnonymization(it) }
                    )
                }

                Spacer(Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    PiiRow("Patient Name", "Anil Baruah")
                    PiiRow("ABHA Health ID", "14-7284-9102-3841")
                    PiiRow("Primary Phone", "+91 98xxxxxx21")
                    PiiRow("Address", "Sonapur, Kamrup, Assam")
                }
            }
        }

        // Granular DPDP Consent Matrix
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)),
            elevation = CardDefaults.cardElevation(3.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "⚖️ DPDP Act 2023 Consent Matrix",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Granular patient data access authorizations",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )

                Spacer(Modifier.height(16.dp))

                ConsentToggleRow(
                    title = "🏥 Hospital & EHR System Sync",
                    desc = "Allow Ayushman Bharat Digital Mission (ABHA) data exchange with hospital network.",
                    checked = consent.abhaEhrSharing,
                    onCheckedChange = {
                        consent = consent.copy(abhaEhrSharing = it)
                        SecurePatientDataManager.updateConsent(consent)
                    }
                )

                HorizontalDivider(Modifier.padding(vertical = 12.dp))

                ConsentToggleRow(
                    title = "🤖 AI Anonymized Analytics",
                    desc = "Share anonymized cognitive progression trends with research models.",
                    checked = consent.aiAnonymizedAnalytics,
                    onCheckedChange = {
                        consent = consent.copy(aiAnonymizedAnalytics = it)
                        SecurePatientDataManager.updateConsent(consent)
                    }
                )

                HorizontalDivider(Modifier.padding(vertical = 12.dp))

                ConsentToggleRow(
                    title = "🚨 Emergency First-Responder Override",
                    desc = "Grant emergency doctors temporary access without PIN during acute events.",
                    checked = consent.emergencyDoctorOverride,
                    onCheckedChange = {
                        consent = consent.copy(emergencyDoctorOverride = it)
                        SecurePatientDataManager.updateConsent(consent)
                    }
                )

                HorizontalDivider(Modifier.padding(vertical = 12.dp))

                ConsentToggleRow(
                    title = "👆 Enforce Biometric Authentication",
                    desc = "Require Fingerprint/Face ID on every sensitive record decryption.",
                    checked = consent.biometricAuthRequired,
                    onCheckedChange = {
                        consent = consent.copy(biometricAuthRequired = it)
                        SecurePatientDataManager.updateConsent(consent)
                    }
                )
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
fun PiiRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        Text(
            SecurePatientDataManager.maskPII(value),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ConsentToggleRow(
    title: String,
    desc: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
            Text(desc, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
        Spacer(Modifier.width(8.dp))
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
fun SecurityAuditLogsTab() {
    val logs = SecurePatientDataManager.auditLogs

    Column(modifier = Modifier.fillMaxSize()) {
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("📜 Immutable Security Audit Trail", color = Color.White, fontWeight = FontWeight.Bold)
                Text("${logs.size} Events Recorded", color = Color(0xFFA5D6A7), fontSize = 12.sp)
            }
        }

        Spacer(Modifier.height(12.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(logs, key = { it.id }) { log ->
                AuditLogCard(log)
            }
        }
    }
}

@Composable
fun AuditLogCard(log: SecurityAuditLog) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        if (log.securityLevel == "WARNING") Color(0xFFFFEBEE) else Color(0xFFE8F5E9)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(if (log.securityLevel == "WARNING") "⚠️" else "🛡️")
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(log.action, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    Text(log.timestamp, fontSize = 10.sp, color = Color.Gray)
                }

                Text(log.details, style = MaterialTheme.typography.bodySmall, color = Color.DarkGray)
                Text("Actor: ${log.actor}", fontSize = 10.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
fun EncryptedExportTab() {
    val context = LocalContext.current
    var exportedHash by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)),
            elevation = CardDefaults.cardElevation(3.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    "📤 End-to-End Encrypted Data Export",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    "Generate a cryptographically signed, password-protected patient health package for sharing with authorized hospital specialists or consulting doctors.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )

                Spacer(Modifier.height(20.dp))

                SmritiButton(
                    text = "📦 Export Encrypted Package (JSON / PDF)",
                    onClick = {
                        exportedHash = SecurePatientDataManager.generateEncryptedExportHash()
                        Toast.makeText(context, "Encrypted Package Generated Successfully!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                )

                if (exportedHash != null) {
                    Spacer(Modifier.height(16.dp))

                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFECEFF1)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                "🔑 CRYPTOGRAPHIC VERIFICATION CHECKSUM:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF37474F)
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                exportedHash!!,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFF00695C)
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
fun AddRecordDialog(onDismiss: () -> Unit) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Cognitive Assessment") }
    var doctor by remember { mutableStateOf("Dr. Rina Deka") }
    var content by remember { mutableStateOf("") }
    var isConfidential by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("➕ Add Encrypted Medical Record") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Record Title") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Category") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = doctor,
                    onValueChange = { doctor = it },
                    label = { Text("Attending Physician / Clinic") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Clinical Notes / Diagnosis Content") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Strict Confidentiality Flag", style = MaterialTheme.typography.bodyMedium)
                    Switch(checked = isConfidential, onCheckedChange = { isConfidential = it })
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && content.isNotBlank()) {
                        SecurePatientDataManager.addMedicalRecord(category, title, content, doctor, isConfidential)
                        onDismiss()
                    }
                }
            ) {
                Text("Save & Encrypt Record")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
