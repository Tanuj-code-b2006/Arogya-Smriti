package com.smritisetu.app.screens

<<<<<<< HEAD
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
=======
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.*
>>>>>>> Arogya-Smriti/master
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
<<<<<<< HEAD
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.smritisetu.app.data.Localization
import com.smritisetu.app.ui.AnimatedGradientBox
import com.smritisetu.app.ui.AnimatedScreen
import com.smritisetu.app.ui.AppGradients
import com.smritisetu.app.ui.SmritiButton
import com.smritisetu.app.utils.rememberVoiceNarrator

data class RoutineItem(val id: Int, val task: String, val icon: String)

@Composable
fun RoutineSequencerScreen(navController: NavController) {
    val s = Localization.strings()
    val narrator = rememberVoiceNarrator()
    LaunchedEffect(Unit) { narrator.say(s.routineOrderIntro) }
    DisposableEffect(Unit) { onDispose { narrator.stop() } }

    var items by remember {
        mutableStateOf(
            listOf(
                RoutineItem(1, "Wake Up", "🌅"),
                RoutineItem(2, "Brush Teeth", "🪥"),
                RoutineItem(3, "Have Breakfast", "🥣"),
                RoutineItem(4, "Take Medicine", "💊")
            ).shuffled()
        )
    }

    var showResult by remember { mutableStateOf(false) }

    AnimatedScreen {
        AnimatedGradientBox(colors = AppGradients.patientColors) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
=======
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.smritisetu.app.ui.AnimatedGradientBox
import com.smritisetu.app.ui.AnimatedScreen
import com.smritisetu.app.ui.SmritiButton
import kotlin.math.roundToInt

data class RoutineTask(val id: Int, val icon: String, val label: String, val correctOrder: Int)

private val correctSequence = listOf(
    RoutineTask(1, "🪥", "Brush Teeth", 0),
    RoutineTask(2, "🍳", "Eat Breakfast", 1),
    RoutineTask(3, "💊", "Take Morning Medicine", 2),
    RoutineTask(4, "🚶", "Morning Walk", 3),
    RoutineTask(5, "🛁", "Bathe", 4),
    RoutineTask(6, "🌙", "Rest / Nap", 5),
)

@Composable
fun RoutineSequencerScreen(navController: NavController) {
    var items by remember { mutableStateOf(correctSequence.shuffled()) }
    var checked by remember { mutableStateOf(false) }
    var isCorrect by remember { mutableStateOf(false) }
    var draggingIndex by remember { mutableStateOf<Int?>(null) }
    var dragOffsetY by remember { mutableFloatStateOf(0f) }

    val itemHeightPx = with(androidx.compose.ui.platform.LocalDensity.current) { 84.dp.toPx() }

    AnimatedScreen {
        AnimatedGradientBox {
            Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
>>>>>>> Arogya-Smriti/master
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
<<<<<<< HEAD
                        s.gameRoutineOrder,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = { navController.popBackStack() }) { Text(s.exit) }
                }
                Text("Order your morning routine!", style = MaterialTheme.typography.bodyLarge)
                Spacer(Modifier.height(24.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(items, key = { it.id }) { item ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(4.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(item.icon, style = MaterialTheme.typography.headlineMedium)
                                Spacer(Modifier.width(16.dp))
                                Text(item.task, style = MaterialTheme.typography.titleMedium)
=======
                        "🗓️ Daily Routine Sequencer",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    TextButton(onClick = { navController.popBackStack() }) { Text("Exit") }
                }
                Text(
                    "Long-press and drag each task into the correct morning order",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(16.dp))

                Box(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        items.forEachIndexed { index, task ->
                            val isDragging = draggingIndex == index
                            val offsetY by animateFloatAsState(
                                targetValue = if (isDragging) dragOffsetY else 0f, label = "offset"
                            )
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                                    .graphicsLayer {
                                        translationY = offsetY
                                        shadowElevation = if (isDragging) 16f else 4f
                                    }
                                    .pointerInput(index) {
                                        detectDragGesturesAfterLongPress(
                                            onDragStart = {
                                                draggingIndex = index
                                                dragOffsetY = 0f
                                            },
                                            onDrag = { change, dragAmount ->
                                                change.consume()
                                                dragOffsetY += dragAmount.y
                                                val moveBy = (dragOffsetY / itemHeightPx).roundToInt()
                                                val targetIndex = (index + moveBy).coerceIn(0, items.lastIndex)
                                                if (targetIndex != index) {
                                                    items = items.toMutableList().apply {
                                                        add(targetIndex, removeAt(index))
                                                    }
                                                    draggingIndex = targetIndex
                                                    dragOffsetY -= moveBy * itemHeightPx
                                                }
                                            },
                                            onDragEnd = {
                                                draggingIndex = null
                                                dragOffsetY = 0f
                                                checked = false
                                            },
                                            onDragCancel = {
                                                draggingIndex = null
                                                dragOffsetY = 0f
                                            }
                                        )
                                    },
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = when {
                                        !checked -> Color.White.copy(alpha = 0.9f)
                                        task.correctOrder == index -> Color(0xFFC8E6C9).copy(alpha = 0.9f)
                                        else -> Color(0xFFFFCDD2).copy(alpha = 0.9f)
                                    }
                                )
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().height(72.dp).padding(horizontal = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("${index + 1}.", style = MaterialTheme.typography.bodyLarge)
                                    Spacer(Modifier.width(12.dp))
                                    Text(task.icon, style = MaterialTheme.typography.headlineMedium)
                                    Spacer(Modifier.width(16.dp))
                                    Text(task.label, style = MaterialTheme.typography.bodyLarge)
                                    Spacer(Modifier.weight(1f))
                                    Text("⠿", style = MaterialTheme.typography.headlineMedium, color = Color.Gray)
                                }
>>>>>>> Arogya-Smriti/master
                            }
                        }
                    }
                }

<<<<<<< HEAD
                Spacer(Modifier.height(24.dp))
                SmritiButton(
                    text = "Check Order ✅",
                    onClick = { showResult = true },
                    modifier = Modifier.fillMaxWidth().height(64.dp)
                )
            }
        }
    }

    if (showResult) {
        val isCorrect = items.map { it.id } == listOf(1, 2, 3, 4)
        AlertDialog(
            onDismissRequest = { showResult = false },
            title = { Text(if (isCorrect) "Perfect! 🌟" else "Almost there! 💪") },
            text = { Text(if (isCorrect) "You remembered the order perfectly!" else "The order was a bit mixed up. Keep practicing!") },
            confirmButton = {
                Button(onClick = {
                    if (isCorrect) navController.popBackStack() else {
                        items = items.shuffled()
                        showResult = false
                    }
                }) {
                    Text(if (isCorrect) s.backToGames else "Try Again")
                }
            }
        )
    }
}
=======
                Spacer(Modifier.height(20.dp))
                SmritiButton(
                    text = "✅ Check My Order",
                    onClick = {
                        checked = true
                        isCorrect = items.mapIndexed { idx, t -> t.correctOrder == idx }.all { it }
                    },
                    modifier = Modifier.fillMaxWidth().height(64.dp)
                )

                if (checked) {
                    Spacer(Modifier.height(12.dp))
                    Text(
                        if (isCorrect) "🎉 Perfect! That's the correct daily routine order!"
                        else "Almost! Green = correct position, Red = needs reordering. Try again.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (isCorrect) Color(0xFF2E7D32) else Color(0xFFC62828)
                    )
                }
            }
        }
    }
}
>>>>>>> Arogya-Smriti/master
