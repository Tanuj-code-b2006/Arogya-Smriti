package com.smritisetu.app.ui
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.smritisetu.app.data.AlertsRepository

data class BottomNavItem(val route: String, val icon: String, val label: String)

val caregiverBottomItems = listOf(
    BottomNavItem("caregiver_dashboard", "🏠", "Home"),
    BottomNavItem("patient_profile", "👤", "Profile"),
    BottomNavItem("monitoring", "📊", "Monitor"),
    BottomNavItem("alerts", "🔔", "Alerts"),
)

@Composable
fun CaregiverBottomBar(navController: NavController, currentRoute: String?) {
    NavigationBar(containerColor = Color.White) {
        caregiverBottomItems.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo("caregiver_dashboard") { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                },
                icon = {
                    if (item.route == "alerts" && AlertsRepository.totalUnreadCount > 0) {
                        Box {
                            Text(item.icon, style = MaterialTheme.typography.headlineSmall)
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = 8.dp, y = (-4).dp)
                                    .size(18.dp)
                                    .background(Color(0xFFC62828), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${AlertsRepository.totalUnreadCount}",
                                    color = Color.White,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    } else {
                        Text(item.icon, style = MaterialTheme.typography.headlineSmall)
                    }
                },
                label = { Text(item.label, style = MaterialTheme.typography.bodyMedium) }
            )
        }
    }
}