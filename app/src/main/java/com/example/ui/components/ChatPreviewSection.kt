package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Badge
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ChatMessage
import com.example.model.MessageSender
import com.example.viewmodel.JonakiUiState
import com.example.viewmodel.JonakiViewModel

@Composable
fun ChatPreviewSection(
    uiState: JonakiUiState,
    viewModel: JonakiViewModel,
    onRegisterTargetBounds: (targetId: String, bounds: androidx.compose.ui.geometry.Rect) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    LaunchedEffect(uiState.chatMessages.size) {
        if (uiState.chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.chatMessages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        // Chat Header with Quick Test Action Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.ChatBubbleOutline,
                    contentDescription = "Chat Preview",
                    tint = Color(0xFFF59E0B),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "চ্যাট প্রিভিউ ও জাদুকাঠি টেস্ট",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE2E8F0)
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                OutlinedButton(
                    onClick = { viewModel.addSampleMessage(isEnglish = true) },
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    modifier = Modifier.height(28.dp).testTag("add_english_msg_button")
                ) {
                    Text("+ EN মেসেজ", fontSize = 10.sp, color = Color(0xFF93C5FD))
                }

                OutlinedButton(
                    onClick = { viewModel.addSampleMessage(isEnglish = false) },
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    modifier = Modifier.height(28.dp).testTag("add_bengali_msg_button")
                ) {
                    Text("+ বাংলা", fontSize = 10.sp, color = Color(0xFF86EFAC))
                }
            }
        }

        // Chat Messages Window
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false)
                .height(180.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(16.dp)),
            color = Color(0xFF090D16).copy(alpha = 0.92f)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.chatMessages, key = { it.id }) { msg ->
                    val targetKey = "msg_${msg.id}"
                    val isHovered = uiState.magicHoverTarget == targetKey

                    ChatMessageBubble(
                        message = msg,
                        isTargetHovered = isHovered,
                        isMagicWandMode = uiState.isMagicWandMode,
                        onMessageClick = {
                            if (uiState.isMagicWandMode) {
                                viewModel.onMagicWandDrop(targetKey)
                            } else {
                                viewModel.translateMessage(msg.id)
                            }
                        },
                        onGloballyPositioned = { bounds ->
                            onRegisterTargetBounds(targetKey, bounds)
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Input Editor Box (Draft Text Area)
        val editorTargetKey = "editor_input"
        val isEditorHovered = uiState.magicHoverTarget == editorTargetKey

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .onGloballyPositioned { coords ->
                    onRegisterTargetBounds(editorTargetKey, coords.boundsInWindow())
                }
                .border(
                    width = if (isEditorHovered) 2.dp else 1.dp,
                    color = if (isEditorHovered) Color(0xFFF59E0B) else Color(0xFF1E293B),
                    shape = RoundedCornerShape(16.dp)
                )
                .clickable {
                    if (uiState.isMagicWandMode) {
                        viewModel.onMagicWandDrop(editorTargetKey)
                    }
                }
                .testTag("editor_input_surface"),
            color = if (isEditorHovered) Color(0xFF1F1A10) else Color(0xFF0D121F)
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isEditorHovered) "🎯 জাদুকাঠি দিয়ে অনুবাদ করতে এখানে ছাড়ুন" else "ইনপুট বক্স (টাইপ বা অনুবাদ করুন):",
                        fontSize = 11.sp,
                        fontWeight = if (isEditorHovered) FontWeight.Bold else FontWeight.Medium,
                        color = if (isEditorHovered) Color(0xFFFBBF24) else Color(0xFF94A3B8)
                    )

                    if (uiState.editorText.isNotEmpty()) {
                        IconButton(
                            onClick = { viewModel.clearEditor() },
                            modifier = Modifier.size(20.dp).testTag("clear_editor_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = Color(0xFF64748B),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Text Display with Simulated Cursor
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (uiState.editorText.isEmpty()) {
                        Text(
                            text = "কীবোর্ড দিয়ে লিখুন অথবা জাদুকাঠি ব্যবহার করুন...",
                            fontSize = 13.sp,
                            color = Color(0xFF475569)
                        )
                    } else {
                        val before = uiState.editorText.substring(0, uiState.cursorPosition.coerceIn(0, uiState.editorText.length))
                        val after = uiState.editorText.substring(uiState.cursorPosition.coerceIn(0, uiState.editorText.length))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = before,
                                fontSize = 13.sp,
                                color = Color(0xFFF1F5F9),
                                fontWeight = FontWeight.Normal
                            )
                            // Animated blinking cursor
                            Box(
                                modifier = Modifier
                                    .width(2.dp)
                                    .height(16.dp)
                                    .background(Color(0xFFF59E0B))
                            )
                            Text(
                                text = after,
                                fontSize = 13.sp,
                                color = Color(0xFFF1F5F9),
                                fontWeight = FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Bottom actions row: Quick Magic Translate Chip & Send Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Quick Magic Wand button for editor
                    ElevatedButton(
                        onClick = { viewModel.translateEditorText() },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = Color(0xFF281C08),
                            contentColor = Color(0xFFFBBF24)
                        ),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(30.dp).testTag("editor_magic_translate_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Magic Translate",
                            tint = Color(0xFFFBBF24),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "🪄 জাদু অনুবাদ",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Send Message Button
                    ElevatedButton(
                        onClick = { viewModel.sendMessage() },
                        enabled = uiState.editorText.isNotBlank(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = Color(0xFFF59E0B),
                            contentColor = Color(0xFF0F172A),
                            disabledContainerColor = Color(0xFF334155),
                            disabledContentColor = Color(0xFF64748B)
                        ),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.height(30.dp).testTag("send_message_button")
                    ) {
                        Text(
                            text = "পাঠান",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatMessageBubble(
    message: ChatMessage,
    isTargetHovered: Boolean,
    isMagicWandMode: Boolean,
    onMessageClick: () -> Unit,
    onGloballyPositioned: (bounds: androidx.compose.ui.geometry.Rect) -> Unit
) {
    val isMe = message.sender == MessageSender.ME

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .onGloballyPositioned { coords ->
                onGloballyPositioned(coords.boundsInWindow())
            },
        horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
    ) {
        val bubbleShape = if (isMe) {
            RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 4.dp)
        } else {
            RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 4.dp, bottomEnd = 16.dp)
        }

        Box(
            modifier = Modifier
                .clip(bubbleShape)
                .clickable { onMessageClick() }
                .then(
                    if (isTargetHovered) {
                        Modifier
                            .border(2.dp, Color(0xFFF59E0B), bubbleShape)
                            .shadow(8.dp, bubbleShape, spotColor = Color(0xFFF59E0B))
                    } else if (isMagicWandMode) {
                        Modifier.border(1.dp, Color(0xFFF59E0B).copy(alpha = 0.5f), bubbleShape)
                    } else {
                        Modifier
                    }
                )
                .background(
                    if (isTargetHovered) {
                        Brush.horizontalGradient(listOf(Color(0xFF452205), Color(0xFF66370B)))
                    } else if (isMe) {
                        Brush.horizontalGradient(listOf(Color(0xFFD97706), Color(0xFFB45309)))
                    } else {
                        Brush.horizontalGradient(listOf(Color(0xFF1E293B), Color(0xFF161E2E)))
                    }
                )
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .testTag("chat_bubble_${message.id}")
        ) {
            Column {
                Text(
                    text = message.text,
                    fontSize = 13.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Normal
                )

                Spacer(modifier = Modifier.height(3.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (message.isTranslated) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Magic Translated",
                                tint = Color(0xFFFDE68A),
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "অনূদিত (${message.translationDirection ?: "🪄"}) • ট্যাপ করে আসল দেখুন",
                                fontSize = 9.sp,
                                color = Color(0xFFFDE68A),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    Text(
                        text = message.time,
                        fontSize = 9.sp,
                        color = if (isMe) Color(0xFFFDE68A).copy(alpha = 0.8f) else Color(0xFF94A3B8)
                    )
                }
            }
        }
    }
}
