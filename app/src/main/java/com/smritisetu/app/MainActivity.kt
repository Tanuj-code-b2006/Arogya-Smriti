package com.smritisetu.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.smritisetu.app.screens.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SmritiSetuTheme {
                AppNavHost()
            }
        }
    }
}

// Elderly-friendly color palette: warm, high-contrast, calm
private val AppColors = lightColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFF2E7D32),      // warm green
    secondary = androidx.compose.ui.graphics.Color(0xFFEF6C00),    // warm orange
    background = androidx.compose.ui.graphics.Color(0xFFFFF8E1),  // warm cream
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

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "welcome") {
        composable("welcome") { WelcomeScreen(navController) }
        composable("patient_home") { PatientHomeScreen(navController) }
        composable("game_menu") { GameMenuScreen(navController) }
        composable("memory_game") { MemoryMatchGameScreen(navController) }
        composable("attention_game") { AttentionGameScreen(navController) }
        composable("reminders") { RemindersScreen(navController) }
        composable("caregiver_dashboard") { CaregiverDashboardScreen(navController) }
    }
}