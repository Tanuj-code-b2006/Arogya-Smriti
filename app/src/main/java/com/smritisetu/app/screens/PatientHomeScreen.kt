package com.smritisetu.app.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
import kotlinx.coroutines.delay

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PatientHomeScreen(navController: NavController) {
    val context = LocalContext.current
    val s = Localization.strings()
    var showCaregiverNotification by remember { mutableStateOf(true) }
    var showContactCaregiverDialog by remember { mutableStateOf(false) }

    // List of upcoming family events, birthdays, and festivals
    val upcomingEvents = s.upcomingEvents

    AnimatedScreen {
        AnimatedGradientBox(colors = AppGradients.patientColors) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(12.dp))

                Text(
                    text = "${s.goodMorning}, ${s.patientName}!",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = s.keepMindActive,
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(Modifier.height(16.dp))

                // Running Text Marquee / Ticker Banner for Upcoming Events & Festivals
                UpcomingEventsTickerBanner(tag = s.upcomingTag, events = upcomingEvents)

                Spacer(Modifier.height(20.dp))

                if (showCaregiverNotification) {
                    NotificationFromCaregiverCard(
                        title = s.caregiverNotification,
                        message = s.missedMedMsg,
                        onRead = { showCaregiverNotification = false }
                    )
                    Spacer(Modifier.height(16.dp))
                }

                HomeCard("📋", s.dailyPlan) { navController.navigate("create_routine") }
                Spacer(Modifier.height(14.dp))
                HomeCard("🎮", s.playGames) { navController.navigate("game_menu") }
                Spacer(Modifier.height(14.dp))
                HomeCard("📝", s.recallDay) { navController.navigate("daily_recall") }
                Spacer(Modifier.height(14.dp))
                HomeCard("👨‍👩‍👧", s.mereApne) { navController.navigate("family_gallery") }
                Spacer(Modifier.height(14.dp))
                HomeCard("📞", s.contactCaregiver) { showContactCaregiverDialog = true }

                Spacer(Modifier.height(20.dp))
            }
        }
    }

    if (showContactCaregiverDialog) {
        AlertDialog(
            onDismissRequest = { showContactCaregiverDialog = false },
            title = { Text(s.contactCaregiver, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Primary Caregiver: ${s.nameSunita} (${s.daughterLabel})", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                    Text("Phone: +91 9800000021", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                    Spacer(Modifier.height(8.dp))
                    Text("Tap 'Call Now' to place a phone call or 'Send SOS Alert' to notify Sunita immediately.", style = MaterialTheme.typography.bodySmall)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:9800000021"))
                        context.startActivity(intent)
                        showContactCaregiverDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("📞 Call Now")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        AlertsRepository.pushNewAlert(
                            AlertItem(
                                id = System.currentTimeMillis().toInt(),
                                icon = "🚨",
                                title = "Patient SOS Call Request",
                                description = "Anil requested urgent assistance from Patient Home Screen.",
                                time = "Just now",
                                severity = AlertSeverity.HIGH
                            )
                        )
                        Toast.makeText(context, "SOS Alert sent to Sunita Baruah!", Toast.LENGTH_LONG).show()
                        showContactCaregiverDialog = false
                    }
                ) {
                    Text("🚨 Send SOS")
                }
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun UpcomingEventsTickerBanner(tag: String, events: List<String>) {
    var currentIndex by remember { mutableIntStateOf(0) }

    // Auto switch to next event every 3.5 seconds
    LaunchedEffect(Unit) {
        while (true) {
            delay(3500)
            currentIndex = (currentIndex + 1) % events.size
        }
    }

    val combinedContinuousText = remember(events) {
        events.joinToString("   •   ")
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
        elevation = CardDefaults.cardElevation(5.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFE65100))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        tag,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.width(10.dp))

                // Smooth Animated Switching Highlight Message
                AnimatedContent(
                    targetState = events[currentIndex],
                    transitionSpec = {
                        (fadeIn(animationSpec = tween(500)) + slideInHorizontally { it / 2 })
                            .togetherWith(fadeOut(animationSpec = tween(500)) + slideOutHorizontally { -it / 2 })
                    },
                    modifier = Modifier.weight(1f),
                    label = "ticker"
                ) { text ->
                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFBF360C),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // Continuous Running Marquee Line underneath
            Text(
                text = "🗓️ $combinedContinuousText",
                modifier = Modifier
                    .fillMaxWidth()
                    .basicMarquee(
                        iterations = Int.MAX_VALUE,
                        velocity = 40.dp
                    ),
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFFE65100),
                fontWeight = FontWeight.Medium,
                maxLines = 1
            )
        }
    }
}

@Composable
fun NotificationFromCaregiverCard(title: String, message: String, onRead: () -> Unit) {
    var showDialog by remember { mutableStateOf(false) }
    val s = Localization.strings()

    val infiniteTransition = rememberInfiniteTransition(label = "dot")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Card(
        onClick = { showDialog = true },
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(contentAlignment = Alignment.TopEnd) {
                Text("📩", style = MaterialTheme.typography.displayMedium)
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .offset(x = 4.dp, y = (-4).dp)
                        .alpha(alpha)
                        .background(Color.Red, CircleShape)
                )
            }
            Spacer(Modifier.width(20.dp))
            Text(
                title,
                style = MaterialTheme.typography.labelLarge,
                color = Color(0xFFC62828),
                fontWeight = FontWeight.Bold
            )
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                // Do nothing on dismiss to force clicking OK
            },
            title = { Text(title, fontWeight = FontWeight.Bold) },
            text = { Text(message, style = MaterialTheme.typography.bodyLarge) },
            confirmButton = {
                Button(onClick = {
                    showDialog = false
                    onRead() // This will trigger the removal of the card from the home screen
                }) {
                    Text(s.ok)
                }
            }
        )
    }
}

@Composable
fun HomeCard(icon: String, label: String, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(icon, style = MaterialTheme.typography.headlineLarge)
            Spacer(Modifier.width(20.dp))
            Text(label, style = MaterialTheme.typography.labelLarge)
        }
    }
}
