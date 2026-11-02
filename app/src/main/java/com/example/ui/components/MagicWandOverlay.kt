package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun MagicWandOverlay(
    isDragging: Boolean,
    dragPosition: Offset,
    isHoveringTarget: Boolean,
    isInspectMode: Boolean,
    onCancelInspect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "wand_effects")

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val wandRotation by infiniteTransition.animateFloat(
        initialValue = -15f,
        targetValue = 15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wand_rotation"
    )

    val sparkleAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sparkle_alpha"
    )

    Box(modifier = modifier.fillMaxSize()) {
        // Floating instruction banner at top when magic wand is active
        AnimatedVisibility(
            visible = isInspectMode || isDragging,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 8.dp, start = 12.dp, end = 12.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF1E1405).copy(alpha = 0.95f),
                modifier = Modifier
                    .border(1.5.dp, Color(0xFFF59E0B), RoundedCornerShape(16.dp))
                    .shadow(12.dp, RoundedCornerShape(16.dp), spotColor = Color(0xFFF59E0B))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Magic Wand",
                        tint = Color(0xFFFBBF24),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isHoveringTarget) "🎯 অনুবাদ করতে এখানে ছাড়ুন বা ট্যাপ করুন!" else "🪄 জাদুকাঠি: লেখার ওপর ধরুন",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isHoveringTarget) Color(0xFFFDE68A) else Color(0xFFFBBF24)
                        )
                        Text(
                            text = "ইংরেজি লেখা বাংলা হবে, বাংলা লেখা ইংরেজি হবে",
                            fontSize = 10.sp,
                            color = Color(0xFFCBD5E1)
                        )
                    }
                    if (isInspectMode && !isDragging) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color(0xFF334155))
                                .clickable { onCancelInspect() }
                                .padding(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }

        // Draggable Wand Reticle & Particle Trail
        if (isDragging) {
            val density = LocalDensity.current
            val xOffset = with(density) { (dragPosition.x - 40.dp.toPx()).roundToInt() }
            val yOffset = with(density) { (dragPosition.y - 70.dp.toPx()).roundToInt() }

            Box(
                modifier = Modifier
                    .offset { IntOffset(xOffset, yOffset) }
                    .size(80.dp),
                contentAlignment = Alignment.Center
            ) {
                // Outer glowing aura rings
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val center = Offset(size.width / 2, size.height / 2)
                    val radius = (size.minDimension / 2.5f) * pulseScale

                    // Outer pulse circle
                    drawCircle(
                        color = if (isHoveringTarget) Color(0xFF10B981).copy(alpha = 0.35f) else Color(0xFFF59E0B).copy(alpha = 0.35f),
                        radius = radius,
                        center = center
                    )

                    // Target ring stroke
                    drawCircle(
                        color = if (isHoveringTarget) Color(0xFF34D399) else Color(0xFFFBBF24),
                        radius = radius * 0.85f,
                        center = center,
                        style = Stroke(width = 2.5f * density.density)
                    )

                    // Crosshair tick marks
                    val tickLen = 8f * density.density
                    drawLine(
                        color = Color(0xFFFDE68A),
                        start = Offset(center.x - radius * 0.85f, center.y),
                        end = Offset(center.x - radius * 0.85f + tickLen, center.y),
                        strokeWidth = 2f * density.density
                    )
                    drawLine(
                        color = Color(0xFFFDE68A),
                        start = Offset(center.x + radius * 0.85f - tickLen, center.y),
                        end = Offset(center.x + radius * 0.85f, center.y),
                        strokeWidth = 2f * density.density
                    )
                }

                // 3D-styled Glowing Wand Icon
                Box(
                    modifier = Modifier
                        .rotate(wandRotation)
                        .scale(if (isHoveringTarget) 1.25f else 1.05f)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    Color(0xFFFDE68A),
                                    Color(0xFFF59E0B),
                                    Color(0xFFB45309)
                                )
                            )
                        )
                        .border(1.5.dp, Color.White, CircleShape)
                        .padding(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Magic Wand",
                        tint = Color(0xFF0F172A),
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Floating Sparkles around the wand tip
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .scale(pulseScale)
                ) {
                    val angles = listOf(30, 75, 120, 210, 290, 340)
                    for (deg in angles) {
                        val rad = Math.toRadians(deg.toDouble())
                        val dist = 32f * density.density
                        val x = (size.width / 2) + (cos(rad) * dist).toFloat()
                        val y = (size.height / 2) + (sin(rad) * dist).toFloat()
                        drawCircle(
                            color = Color(0xFFFDE68A).copy(alpha = sparkleAlpha),
                            radius = 3.5f * density.density,
                            center = Offset(x, y)
                        )
                    }
                }
            }
        }
    }
}
