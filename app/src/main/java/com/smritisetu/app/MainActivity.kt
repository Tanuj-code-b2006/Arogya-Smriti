package com.smritisetu.app
import androidx.compose.foundation.layout.padding
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.smritisetu.app.data.AlertItem
import com.smritisetu.app.data.AlertSeverity
import com.smritisetu.app.data.AlertsRepository
import com.smritisetu.app.screens.*
import com.smritisetu.app.ui.CaregiverBottomBar
import com.smritisetu.app.ui.NotificationBanner
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SmritiSetuTheme {
                Surface {
                    AppRoot()
                }
            }
        }
    }
}

// Screens that should show the caregiver bottom nav bar
private val caregiverScreensWithBottomBar = setOf(
    "caregiver_dashboard", "patient_profile", "monitoring", "alerts"
)

@Composable
fun AppRoot() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    // 🔴 DEMO: simulate a real-time incoming alert ~10s after entering the app,
    // to show the live banner + badge working during the pitch.
    LaunchedEffect(Unit) {
        delay(10000)
        AlertsRepository.pushNewAlert(
            AlertItem(
                id = 100,
                icon = "🚨",
                title = "Hydration Reminder Missed",
                description = "Anil has not confirmed drinking water for the 11:00 AM reminder.",
                time = "Just now",
                severity = AlertSeverity.HIGH
            )
        )
    }

    Scaffold(
        bottomBar = {
            if (currentRoute in caregiverScreensWithBottomBar) {
                CaregiverBottomBar(navController, currentRoute)
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            NavHost(
                navController = navController,
                startDestination = "welcome",
                modifier = Modifier.padding(padding)
            ) {
                composable("welcome") { WelcomeScreen(navController) }
                composable("patient_home") { PatientHomeScreen(navController) }
                composable("game_menu") { GameMenuScreen(navController) }
                composable("memory_game") { MemoryMatchGameScreen(navController) }
                composable("attention_game") { AttentionGameScreen(navController) }
                composable("routine_sequencer") { RoutineSequencerScreen(navController) }
                composable("ai_chat_companion") { AIChatCompanionScreen(navController) }
                composable("reminders") { RemindersScreen(navController) }
                composable("caregiver_dashboard") { CaregiverDashboardScreen(navController) }
                composable("patient_profile") { PatientProfileScreen(navController) }
                composable("monitoring") { MonitoringScreen(navController) }
                composable("alerts") { AlertsScreen(navController) }
                composable("caregiver_section") { CaregiverSectionScreen(navController) }
                composable("patient_list") { PatientListScreen(navController) }
                composable("add_patient") { AddPatientScreen(navController) }
            }

            // Live push-style banner, shown above everything, only on caregiver screens
            if (currentRoute in caregiverScreensWithBottomBar) {
                Box(modifier = Modifier.align(Alignment.TopCenter)) {
                    NotificationBanner(
                        alert = AlertsRepository.latestIncomingAlert.value,
                        onDismiss = { AlertsRepository.latestIncomingAlert.value = null },
                        onClick = { navController.navigate("alerts") }
                    )
                }
            }
        }
    }
}

private val AppColors = androidx.compose.material3.lightColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFF2E7D32),
    secondary = androidx.compose.ui.graphics.Color(0xFFEF6C00),
    background = androidx.compose.ui.graphics.Color(0xFFFFF8E1),
    surface = androidx.compose.ui.graphics.Color(0xFFFFFFFF),
    error = androidx.compose.ui.graphics.Color(0xFFC62828)
)

private val AppTypography = Typography(
    headlineLarge = TextStyle(fontSize = 34.sp, fontWeight = FontWeight.Bold),
    headlineMedium = TextStyle(fontSize = 26.sp, fontWeight = FontWeight.Bold),
    bodyLarge = TextStyle(fontSize = 20.sp),
    bodyMedium = TextStyle(fontSize = 18.sp),
    labelLarge = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
)

@Composable
fun SmritiSetuTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = AppColors, typography = AppTypography, content = content)
}