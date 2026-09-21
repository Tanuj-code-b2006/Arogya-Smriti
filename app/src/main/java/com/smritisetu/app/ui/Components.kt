package com.smritisetu.app.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

@Composable
fun AnimatedGradientBox(
    modifier: Modifier = Modifier,
    colors: List<Color> = listOf(
        Color(0xFFFFB74D), // Strong Orange
        Color(0xFF81C784), // Strong Green
        Color(0xFF64B5F6), // Strong Blue
        Color(0xFFFFF176)  // Strong Yellow
    ),
    content: @Composable BoxScope.() -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "background")
    
    // Animate the gradient angle/position
    val xOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "xOffset"
    )
    
    val yOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "yOffset"
    )

    val brush = Brush.linearGradient(
        colors = colors,
        start = Offset(xOffset, yOffset),
        end = Offset(xOffset + 800f, yOffset + 800f)
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(brush),
        content = content
    )
}

object AppGradients {
    val patientColors = listOf(
        Color(0xFFFFB74D), // Orange
        Color(0xFFFFF176), // Yellow
        Color(0xFF81C784), // Green
        Color(0xFFFFE0B2)  // Soft Orange
    )

    val caregiverColors = listOf(
        Color(0xFFE0F7FA), // Very Light Cyan
        Color(0xFFB2EBF2), // Light Cyan
        Color(0xFFE1F5FE), // Very Light Blue
        Color(0xFFF5F5F5)  // Near White
    )
    
    val welcomeColors = listOf(
        Color(0xFFFFF8E1), // Warm Cream
        Color(0xFFE8F5E9), // Very Light Green
        Color(0xFFE3F2FD), // Very Light Blue
        Color(0xFFFFF3E0)  // Very Light Orange
    )

    val nightColors = listOf(
        Color(0xFF0F2027), // Dark Navy
        Color(0xFF203A43), // Medium Navy
        Color(0xFF2C5364), // Blue-Grey
        Color(0xFF1B1B2F)  // Deep Dark
    )
}

@Composable
fun AnimatedScreen(content: @Composable () -> Unit) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }
    
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(600)) + slideInVertically(initialOffsetY = { 50 }),
        exit = fadeOut(animationSpec = tween(600))
    ) {
        content()
    }
}

@Composable
fun SmritiButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = Color.White,
    icon: (@Composable () -> Unit)? = null,
    enabled: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "border")
    val borderAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Button(
        onClick = onClick,
        modifier = modifier.padding(4.dp),
        enabled = enabled,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(2.dp, Brush.linearGradient(
            colors = listOf(
                Color.White.copy(alpha = borderAlpha),
                Color.Gray.copy(alpha = borderAlpha / 2)
            )
        )),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = containerColor.copy(alpha = 0.5f),
            disabledContentColor = contentColor.copy(alpha = 0.5f)
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                icon()
                Spacer(Modifier.width(12.dp))
            }
            Text(text, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
fun PulsingMicAnimation(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 0.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Box(contentAlignment = Alignment.Center, modifier = modifier) {
        // Outer pulsing circle
        Box(
            modifier = Modifier
                .size(100.dp)
                .graphicsLayer(scaleX = scale, scaleY = scale)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = alpha), CircleShape)
        )
        // Inner mic icon circle
        Surface(
            modifier = Modifier.size(70.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary,
            shadowElevation = 8.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }
        }
    }
}
