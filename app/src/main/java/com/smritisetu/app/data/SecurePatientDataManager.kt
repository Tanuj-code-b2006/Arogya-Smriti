package com.smritisetu.app.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

data class EncryptedMedicalRecord(
    val id: String = UUID.randomUUID().toString(),
    val category: String, // e.g., "Cognitive Assessment", "Neurological Exam", "Prescription", "Vitals"
    val title: String,
    val cipherText: String, // Simulated AES-256 encrypted payload
    val decryptedContent: String,
    val doctorName: String,
    val date: String,
    val isConfidential: Boolean = false,
    var isDecrypted: Boolean = false
)

data class SecurityAuditLog(
    val id: String = UUID.randomUUID().toString(),
    val timestamp: String,
    val action: String,
    val actor: String, // e.g. "Primary Caregiver (Sunita)", "Dr. Rina Deka", "System AI"
    val details: String,
    val securityLevel: String = "INFO" // INFO, WARNING, CRITICAL
)

data class ConsentSettings(
    var abhaEhrSharing: Boolean = true,
    var aiAnonymizedAnalytics: Boolean = true,
    var emergencyDoctorOverride: Boolean = true,
    var biometricAuthRequired: Boolean = true
)

object SecurePatientDataManager {
    var isUnlocked by mutableStateOf(false)
        private set

    var isAnonymizedMode by mutableStateOf(false)

    var consentSettings by mutableStateOf(ConsentSettings())

    private var masterPin = "1234"

    val auditLogs = mutableStateListOf<SecurityAuditLog>()

    val medicalRecords = mutableStateListOf<EncryptedMedicalRecord>()

    init {
        // Seed initial encrypted medical records
        medicalRecords.addAll(
            listOf(
                EncryptedMedicalRecord(
                    id = "REC-2025-001",
                    category = "Cognitive Assessment",
                    title = "MMSE Cognitive Battery Result",
                    cipherText = "U2FsdGVkX1+9xK1z9L/X2vM8aK7B4Q9zX0mR2w1P4vL8k1=",
                    decryptedContent = "MMSE Score: 22/30 (Mild Cognitive Impairment). Spatial orientation intact. Memory recall shows mild delay. Advised monthly cognitive games.",
                    doctorName = "Dr. Rina Deka (Neurologist)",
                    date = "12 Mar 2025",
                    isConfidential = true
                ),
                EncryptedMedicalRecord(
                    id = "REC-2025-002",
                    category = "Neurological Exam",
                    title = "Brain MRI & Clinical Summary",
                    cipherText = "U2FsdGVkX1/3mO8p2Q+R1vN7wL9xM5k2P3qR0sT1uV4=",
                    decryptedContent = "MRI shows mild hippocampal atrophy consistent with early-stage Alzheimer's disease. No vascular lesions detected.",
                    doctorName = "Dr. B. K. Sharma (NEIGRIHMS)",
                    date = "05 Feb 2025",
                    isConfidential = true
                ),
                EncryptedMedicalRecord(
                    id = "REC-2025-003",
                    category = "Prescription",
                    title = "Active Neuro-Medications Regimen",
                    cipherText = "U2FsdGVkX18a4B1m9x2C3v0N1P7qR5sT3uV1w8=",
                    decryptedContent = "1. Donepezil 5mg QD (Morning after food)\n2. Amlodipine 5mg QD (For BP)\n3. Vitamin D3 60k IU (Weekly)",
                    doctorName = "Dr. Rina Deka",
                    date = "20 Mar 2025",
                    isConfidential = false
                ),
                EncryptedMedicalRecord(
                    id = "REC-2025-004",
                    category = "Vitals & Genetic Marker",
                    title = "APOE Gene Status & Vitals Baseline",
                    cipherText = "U2FsdGVkX1+m1N0P9qR2sT4uV6w8x0z2A4b6C8=",
                    decryptedContent = "APOE-e4 heterozygous carrier detected. Resting HR: 72 bpm, BP: 128/82 mmHg. SpO2: 98%. Patient stable.",
                    doctorName = "Genomics Lab NE",
                    date = "18 Jan 2025",
                    isConfidential = true
                )
            )
        )

        // Seed initial security audit logs
        addAuditLog("SYSTEM_INIT", "System", "AES-256 KeyStore Encryption Engine Initialized", "INFO")
        addAuditLog("ABHA_SYNC", "ABHA Gateway", "Health ID NER-DEM-0042 verified via Ayushman Bharat Digital Mission", "INFO")
        addAuditLog("SECURITY_CHECK", "Security Sentinel", "Hardware Security Module (HSM) compliance check passed", "INFO")
    }

    fun verifyPin(inputPin: String): Boolean {
        return if (inputPin == masterPin) {
            isUnlocked = true
            addAuditLog("PIN_AUTH_SUCCESS", "Primary Caregiver", "Caregiver authenticated via 4-Digit Security PIN", "INFO")
            true
        } else {
            addAuditLog("PIN_AUTH_FAILED", "Unknown User", "Failed PIN authentication attempt detected", "WARNING")
            false
        }
    }

    fun authenticateBiometrics(): Boolean {
        isUnlocked = true
        addAuditLog("BIOMETRIC_AUTH_SUCCESS", "Primary Caregiver", "Authenticated via Android Biometric Prompt (Fingerprint/Face)", "INFO")
        return true
    }

    fun lockVault() {
        isUnlocked = false
        // Re-encrypt all records view state
        medicalRecords.forEach { it.isDecrypted = false }
        addAuditLog("VAULT_LOCKED", "Primary Caregiver", "Patient Data Vault manually locked", "INFO")
    }

    fun toggleDecryptRecord(id: String) {
        val record = medicalRecords.find { it.id == id }
        if (record != null) {
            record.isDecrypted = !record.isDecrypted
            val action = if (record.isDecrypted) "RECORD_DECRYPTED" else "RECORD_RE_ENCRYPTED"
            addAuditLog(action, "Primary Caregiver", "Toggled decryption for ${record.title} (${record.id})", "INFO")
        }
    }

    fun addMedicalRecord(category: String, title: String, content: String, doctor: String, isConfidential: Boolean) {
        val simulatedCipher = "U2FsdGVkX1" + UUID.randomUUID().toString().replace("-", "").take(24) + "="
        val newRecord = EncryptedMedicalRecord(
            category = category,
            title = title,
            cipherText = simulatedCipher,
            decryptedContent = content,
            doctorName = doctor,
            date = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date()),
            isConfidential = isConfidential,
            isDecrypted = true
        )
        medicalRecords.add(0, newRecord)
        addAuditLog("RECORD_ADDED", "Caregiver / Physician", "Added new encrypted medical record: $title", "INFO")
    }

    fun toggleAnonymization(enabled: Boolean) {
        isAnonymizedMode = enabled
        val status = if (enabled) "ENABLED (PII Masked)" else "DISABLED (Unmasked)"
        addAuditLog("ANONYMIZATION_TOGGLE", "Caregiver", "Data Anonymization Mode set to $status", "INFO")
    }

    fun updateConsent(newConsent: ConsentSettings) {
        consentSettings = newConsent
        addAuditLog("CONSENT_UPDATED", "Patient/Caregiver", "Updated DPDP & ABHA Health Data Consent Preferences", "INFO")
    }

    fun addAuditLog(action: String, actor: String, details: String, securityLevel: String) {
        val timeStr = SimpleDateFormat("hh:mm:ss a, dd MMM", Locale.getDefault()).format(Date())
        auditLogs.add(0, SecurityAuditLog(timestamp = timeStr, action = action, actor = actor, details = details, securityLevel = securityLevel))
    }

    fun maskPII(input: String): String {
        if (!isAnonymizedMode || input.isBlank()) return input
        // Simple intelligent PII masker for demo
        val parts = input.split(" ")
        return parts.joinToString(" ") { word ->
            if (word.length <= 2) word
            else word.first() + "*".repeat(word.length - 2) + word.last()
        }
    }

    fun generateEncryptedExportHash(): String {
        addAuditLog("DATA_EXPORTED", "Caregiver", "Generated E2E Encrypted Medical Export Package with SHA-256 Checksum", "INFO")
        return "SHA256: e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855"
    }
}
