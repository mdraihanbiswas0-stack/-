package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.SentimentSatisfiedAlt
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.KeyboardSubPanel
import com.example.viewmodel.JonakiUiState
import com.example.viewmodel.JonakiViewModel

@Composable
fun KeyboardToolbar(
    uiState: JonakiUiState,
    viewModel: JonakiViewModel,
    onStartMagicDrag: (Offset) -> Unit,
    onMagicDrag: (Offset) -> Unit,
    onEndMagicDrag: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "wand_glow")
    val glowScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_scale"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 2.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(12.dp)),
        color = uiState.selectedTheme.toolbarBackground
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Scrollable Tools Row on Left
            Row(
                modifier = Modifier
                    .weight(1f)
                    .horizontalScroll(rememberScrollState()),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // HERO FEATURE: Magic Wand (জাদুকাঠি)
                // Supports both single tap and hold-drag!
                var wandButtonPosition = Offset.Zero

                Box(
                    modifier = Modifier
                        .scale(if (uiState.isMagicWandMode) glowScale else 1.0f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            Brush.horizontalGradient(
                                if (uiState.isMagicWandMode) {
                                    listOf(Color(0xFFF59E0B), Color(0xFFD97706))
                                } else {
                                    listOf(Color(0xFF3B250B), Color(0xFF52330E))
                                }
                            )
                        )
                        .border(
                            width = 1.5.dp,
                            color = if (uiState.isMagicWandMode) Color(0xFFFDE68A) else Color(0xFFF59E0B),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .onGloballyPositioned { coords ->
                            val bounds = coords.boundsInWindow()
                            wandButtonPosition = bounds.center
                        }
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDragStart = { localOffset ->
                                    onStartMagicDrag(wandButtonPosition + localOffset)
                                },
                                onDrag = { change, dragAmount ->
                                    change.consume()
                                    onMagicDrag(dragAmount)
                                },
                                onDragEnd = {
                                    onEndMagicDrag()
                                },
                                onDragCancel = {
                                    onEndMagicDrag()
                                }
                            )
                        }
                        .clickable {
                            viewModel.setMagicWandMode(!uiState.isMagicWandMode)
                        }
                        .padding(horizontal = 8.dp, vertical = 5.dp)
                        .testTag("magic_wand_tool_button")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Magic Wand",
                            tint = if (uiState.isMagicWandMode) Color(0xFF0F172A) else Color(0xFFFDE68A),
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (uiState.isMagicWandMode) "জাদু সক্রিয়" else "জাদুকাঠি",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (uiState.isMagicWandMode) Color(0xFF0F172A) else Color(0xFFFDE68A)
                        )
                    }
                }

                // 2. Emoji Button
                ToolbarIconButton(
                    icon = Icons.Default.SentimentSatisfiedAlt,
                    label = "ইমোজি",
                    tint = Color(0xFFFCD34D),
                    isActive = uiState.activeSubPanel == KeyboardSubPanel.EMOJI,
                    onClick = { viewModel.setSubPanel(KeyboardSubPanel.EMOJI) }
                )

                // 3. Clipboard Button
                ToolbarIconButton(
                    icon = Icons.Default.ContentPaste,
                    label = "ক্লিপবোর্ড",
                    tint = Color(0xFF60A5FA),
                    isActive = uiState.activeSubPanel == KeyboardSubPanel.CLIPBOARD,
                    onClick = { viewModel.setSubPanel(KeyboardSubPanel.CLIPBOARD) }
                )

                // 4. Numpad Button
                ToolbarIconButton(
                    icon = Icons.Default.Calculate,
                    label = "নম্বর",
                    tint = Color(0xFF34D399),
                    isActive = uiState.activeSubPanel == KeyboardSubPanel.NUMPAD,
                    onClick = { viewModel.setSubPanel(KeyboardSubPanel.NUMPAD) }
                )

                // 5. Text Editor (Gboard-style)
                ToolbarIconButton(
                    icon = Icons.Default.EditNote,
                    label = "এডিটর",
                    tint = Color(0xFFF472B6),
                    isActive = uiState.activeSubPanel == KeyboardSubPanel.TEXT_EDITOR,
                    onClick = { viewModel.setSubPanel(KeyboardSubPanel.TEXT_EDITOR) }
                )

                // 6. Shortcuts Manager
                ToolbarIconButton(
                    icon = Icons.Default.Bolt,
                    label = "শর্টকাট",
                    tint = Color(0xFFFBBF24),
                    isActive = uiState.activeSubPanel == KeyboardSubPanel.SHORTCUTS,
                    onClick = { viewModel.setSubPanel(KeyboardSubPanel.SHORTCUTS) }
                )

                // 7. Text Repeater
                ToolbarIconButton(
                    icon = Icons.Default.Repeat,
                    label = "রিপিটার",
                    tint = Color(0xFF2DD4BF),
                    isActive = uiState.activeSubPanel == KeyboardSubPanel.REPEATER,
                    onClick = { viewModel.setSubPanel(KeyboardSubPanel.REPEATER) }
                )

                // 8. Theme Store
                ToolbarIconButton(
                    icon = Icons.Default.Palette,
                    label = "থিম",
                    tint = Color(0xFFA78BFA),
                    isActive = uiState.activeSubPanel == KeyboardSubPanel.THEMES,
                    onClick = { viewModel.setSubPanel(KeyboardSubPanel.THEMES) }
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Fixed Microphone Button on the right
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (uiState.isVoiceListening) {
                            Color(0xFFE11D48).copy(alpha = 0.25f)
                        } else {
                            Color(0xFF1E293B)
                        }
                    )
                    .border(
                        width = 1.dp,
                        color = if (uiState.isVoiceListening) Color(0xFFF43F5E) else Color(0xFF334155),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .clickable { viewModel.toggleVoiceTyping() }
                    .padding(horizontal = 8.dp, vertical = 6.dp)
                    .testTag("voice_typing_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Voice Typing",
                    tint = if (uiState.isVoiceListening) Color(0xFFF43F5E) else Color(0xFF93C5FD),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun ToolbarIconButton(
    icon: ImageVector,
    label: String,
    tint: Color,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isActive) Color(0xFF334155) else Color.Transparent)
            .clickable { onClick() }
            .padding(horizontal = 7.dp, vertical = 5.dp)
            .testTag("toolbar_$label")
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isActive) Color.White else tint,
            modifier = Modifier.size(18.dp)
        )
    }
}
