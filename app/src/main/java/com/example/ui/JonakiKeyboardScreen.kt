package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.KeyboardLayoutType
import com.example.model.KeyboardSubPanel
import com.example.ui.components.Bds1520KeyboardLayout
import com.example.ui.components.ChatPreviewSection
import com.example.ui.components.EnglishKeyboardLayout
import com.example.ui.components.KeyboardToolbar
import com.example.ui.components.MagicWandOverlay
import com.example.ui.components.SubPanelsView
import com.example.viewmodel.JonakiViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JonakiKeyboardScreen(
    viewModel: JonakiViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    // Target coordinates map for drag-and-drop detection
    val targetBounds = remember { mutableStateMapOf<String, Rect>() }

    // Floating Wand drag state
    var isDraggingWand by remember { mutableStateOf(false) }
    var wandDragPosition by remember { mutableStateOf(Offset.Zero) }

    // Settings menu state
    var showMenu by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF070A12))
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // TOP APP HEADER
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                color = Color.Transparent
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Logo & App Name
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0xFFF59E0B), Color(0xFFD97706), Color(0xFFE11D48))
                                    )
                                )
                                .shadow(6.dp, RoundedCornerShape(10.dp), spotColor = Color(0xFFF59E0B)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "জ",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "জোনাকি কীবোর্ড",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFFFBBF24)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFF3B250B))
                                        .border(0.5.dp, Color(0xFFF59E0B), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 5.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "BDS 1520",
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFFDE68A)
                                    )
                                }
                            }
                            Text(
                                text = "জাদুকাঠি স্বয়ংক্রিয় অনুবাদক (বাংলা ⇄ English)",
                                fontSize = 9.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }

                    // Header Action Buttons
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Quick Magic Wand indicator / toggle
                        IconButton(
                            onClick = { viewModel.setMagicWandMode(!uiState.isMagicWandMode) },
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(if (uiState.isMagicWandMode) Color(0xFFF59E0B) else Color(0xFF1E293B))
                                .testTag("header_magic_wand_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Magic Wand",
                                tint = if (uiState.isMagicWandMode) Color(0xFF0F172A) else Color(0xFFFBBF24),
                                modifier = Modifier.size(17.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        // Theme Store Shortcut
                        IconButton(
                            onClick = { viewModel.setSubPanel(KeyboardSubPanel.THEMES) },
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Palette,
                                contentDescription = "Themes",
                                tint = Color(0xFFA78BFA),
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // More Settings Menu
                        Box {
                            IconButton(
                                onClick = { showMenu = true },
                                modifier = Modifier.size(34.dp).testTag("more_settings_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = "Settings",
                                    tint = Color(0xFFCBD5E1),
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false },
                                modifier = Modifier.background(Color(0xFF0F172A))
                            ) {
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = if (uiState.showNumberRow) "✓ নম্বর সারি লুকান" else "নম্বর সারি দেখান",
                                            fontSize = 12.sp,
                                            color = Color.White
                                        )
                                    },
                                    onClick = {
                                        viewModel.toggleNumberRow()
                                        showMenu = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = if (uiState.soundEnabled) "✓ ক্লিক সাউন্ড (চালু)" else "ক্লিক সাউন্ড (বন্ধ)",
                                            fontSize = 12.sp,
                                            color = Color.White
                                        )
                                    },
                                    onClick = {
                                        viewModel.toggleSound()
                                        showMenu = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = if (uiState.vibrationEnabled) "✓ ভাইব্রেশন (চালু)" else "ভাইব্রেশন (বন্ধ)",
                                            fontSize = 12.sp,
                                            color = Color.White
                                        )
                                    },
                                    onClick = {
                                        viewModel.toggleVibration()
                                        showMenu = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = if (uiState.activeKbLayout == KeyboardLayoutType.JATIYA) "লেআউট: ইংরেজি তে পরিবর্তন" else "লেআউট: জাতীয় (BDS 1520)",
                                            fontSize = 12.sp,
                                            color = Color(0xFFFBBF24)
                                        )
                                    },
                                    onClick = {
                                        viewModel.setKeyboardLayout(
                                            if (uiState.activeKbLayout == KeyboardLayoutType.JATIYA) KeyboardLayoutType.ENGLISH else KeyboardLayoutType.JATIYA
                                        )
                                        showMenu = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // CHAT PREVIEW & DRAFT EDITOR (MIDDLE SECTION)
            ChatPreviewSection(
                uiState = uiState,
                viewModel = viewModel,
                onRegisterTargetBounds = { targetId, bounds ->
                    targetBounds[targetId] = bounds
                },
                modifier = Modifier.weight(1f)
            )

            // BOTTOM KEYBOARD CONTAINER
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
                color = Color.Transparent
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(uiState.selectedTheme.backgroundBrush)
                        .padding(horizontal = 4.dp, vertical = 4.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Keyboard Shortcut / Toolbar (Wand, Emojis, Numpad, Clipboard, etc.)
                        KeyboardToolbar(
                            uiState = uiState,
                            viewModel = viewModel,
                            onStartMagicDrag = { startPos ->
                                isDraggingWand = true
                                wandDragPosition = startPos
                            },
                            onMagicDrag = { dragDelta ->
                                wandDragPosition += dragDelta
                                // Test if wand is currently hovering over any registered target
                                val hoveredKey = targetBounds.entries.find { entry ->
                                    entry.value.contains(wandDragPosition)
                                }?.key
                                viewModel.setMagicHoverTarget(hoveredKey)
                            },
                            onEndMagicDrag = {
                                isDraggingWand = false
                                val target = targetBounds.entries.find { entry ->
                                    entry.value.contains(wandDragPosition)
                                }?.key

                                if (target != null) {
                                    viewModel.onMagicWandDrop(target)
                                } else {
                                    viewModel.setMagicHoverTarget(null)
                                }
                            }
                        )

                        // Subpanel (Emoji, Clipboard, Numpad, Editor, Shortcuts, Repeater, Themes)
                        if (uiState.activeSubPanel != KeyboardSubPanel.NONE) {
                            SubPanelsView(
                                uiState = uiState,
                                viewModel = viewModel
                            )
                        } else {
                            // Active Keyboard Layout (BDS 1520 Jatiya or English)
                            when (uiState.activeKbLayout) {
                                KeyboardLayoutType.JATIYA -> {
                                    Bds1520KeyboardLayout(
                                        uiState = uiState,
                                        viewModel = viewModel
                                    )
                                }
                                KeyboardLayoutType.ENGLISH -> {
                                    EnglishKeyboardLayout(
                                        uiState = uiState,
                                        viewModel = viewModel
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // FULLSCREEN MAGIC WAND OVERLAY (FOR DRAG-AND-DROP & HOVER RETICLE)
        MagicWandOverlay(
            isDragging = isDraggingWand,
            dragPosition = wandDragPosition,
            isHoveringTarget = uiState.magicHoverTarget != null,
            isInspectMode = uiState.isMagicWandMode,
            onCancelInspect = { viewModel.setMagicWandMode(false) }
        )

        // FLOATING TOAST NOTIFICATION
        AnimatedVisibility(
            visible = uiState.toastMessage != null,
            enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 54.dp)
        ) {
            uiState.toastMessage?.let { msg ->
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFF1E293B).copy(alpha = 0.95f),
                    modifier = Modifier
                        .border(1.dp, Color(0xFFF59E0B), RoundedCornerShape(20.dp))
                        .shadow(8.dp, RoundedCornerShape(20.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = msg,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFFDE68A)
                        )
                    }
                }
            }
        }
    }
}
