package com.smritisetu.app.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
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
                            }
                        }
                    }
                }

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