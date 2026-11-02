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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.KeyboardLayoutType
import com.example.model.KeyboardSubPanel
import com.example.ui.theme.KeyboardThemeRepository
import com.example.viewmodel.JonakiUiState
import com.example.viewmodel.JonakiViewModel

@Composable
fun SubPanelsView(
    uiState: JonakiUiState,
    viewModel: JonakiViewModel,
    modifier: Modifier = Modifier
) {
    if (uiState.activeSubPanel == KeyboardSubPanel.NONE) return

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 2.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(16.dp)),
        color = Color(0xFF090D16).copy(alpha = 0.98f)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            when (uiState.activeSubPanel) {
                KeyboardSubPanel.EMOJI -> EmojiPanel(viewModel)
                KeyboardSubPanel.CLIPBOARD -> ClipboardPanel(uiState, viewModel)
                KeyboardSubPanel.NUMPAD -> NumpadPanel(uiState, viewModel)
                KeyboardSubPanel.TEXT_EDITOR -> TextEditorPanel(uiState, viewModel)
                KeyboardSubPanel.SHORTCUTS -> ShortcutsPanel(uiState, viewModel)
                KeyboardSubPanel.REPEATER -> RepeaterPanel(uiState, viewModel)
                KeyboardSubPanel.THEMES -> ThemesPanel(uiState, viewModel)
                KeyboardSubPanel.NONE -> {}
            }
        }
    }
}

@Composable
private fun PanelHeader(
    title: String,
    titleColor: Color,
    onClose: () -> Unit,
    trailingAction: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = titleColor
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            trailingAction?.invoke()
            Spacer(modifier = Modifier.width(6.dp))
            IconButton(
                onClick = onClose,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color(0xFF94A3B8),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

// 1. Emoji Panel
@Composable
private fun EmojiPanel(viewModel: JonakiViewModel) {
    val emojis = listOf(
        "😀", "😂", "😍", "😊", "🙏", "👍", "🔥", "✨",
        "❤️", "😎", "😭", "🤔", "🌺", "🇧🇩", "🎉", "💯",
        "🤝", "🌟", "💡", "🪄", "🚀", "☕", "📱", "🌸",
        "🥰", "🤩", "✌️", "💪", "🙌", "🎯", "⚡", "💖"
    )

    Column {
        PanelHeader(
            title = "ইমোজি প্যানেল",
            titleColor = Color(0xFFFCD34D),
            onClose = { viewModel.setSubPanel(KeyboardSubPanel.NONE) }
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(8),
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(emojis) { emoji ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF1E293B).copy(alpha = 0.6f))
                        .clickable { viewModel.insertText(emoji) }
                        .padding(6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = emoji, fontSize = 18.sp)
                }
            }
        }
    }
}

// 2. Clipboard Panel
@Composable
private fun ClipboardPanel(uiState: JonakiUiState, viewModel: JonakiViewModel) {
    Column {
        PanelHeader(
            title = "ক্লিপবোর্ড হিস্ট্রি",
            titleColor = Color(0xFF60A5FA),
            onClose = { viewModel.setSubPanel(KeyboardSubPanel.NONE) }
        )
        if (uiState.clipboardList.isEmpty()) {
            Text(
                text = "কোনো কপি করা লেখা নেই",
                fontSize = 11.sp,
                color = Color(0xFF64748B),
                modifier = Modifier.padding(vertical = 12.dp)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(uiState.clipboardList, key = { it.id }) { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF1E293B))
                            .clickable {
                                viewModel.insertText(item.text)
                                viewModel.setSubPanel(KeyboardSubPanel.NONE)
                            }
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = item.text,
                            fontSize = 11.sp,
                            color = Color(0xFFE2E8F0),
                            maxLines = 1,
                            modifier = Modifier.weight(1f)
                        )
                        if (item.isPinned) {
                            Icon(
                                imageVector = Icons.Default.PushPin,
                                contentDescription = "Pinned",
                                tint = Color(0xFFF59E0B),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// 3. Calculator-Style Numpad Panel (Matching reference)
@Composable
private fun NumpadPanel(uiState: JonakiUiState, viewModel: JonakiViewModel) {
    val isBangla = uiState.activeKbLayout == KeyboardLayoutType.JATIYA
    val numbers = if (isBangla) {
        listOf("১", "২", "৩", "৪", "৫", "৬", "৭", "৮", "৯")
    } else {
        listOf("1", "2", "3", "4", "5", "6", "7", "8", "9")
    }
    val zero = if (isBangla) "০" else "0"

    Column {
        PanelHeader(
            title = "ক্যালকুলেটর নম্বর প্যাড",
            titleColor = Color(0xFF34D399),
            onClose = { viewModel.setSubPanel(KeyboardSubPanel.NONE) }
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Col 1: Operators (+, -, *, /) & ABC
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                listOf("+", "-", "*", "/").forEach { op ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(30.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF1E293B))
                            .clickable { viewModel.insertText(op) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = op, color = Color(0xFFE2E8F0), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(30.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFDB2777))
                        .clickable { viewModel.setSubPanel(KeyboardSubPanel.NONE) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "ABC", color = Color.White, fontWeight = FontWeight.Black, fontSize = 10.sp)
                }
            }

            // Col 2-4: 3x3 Grid + (., 0, _)
            Column(
                modifier = Modifier.weight(3f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                val chunks = numbers.chunked(3)
                chunks.forEach { rowNums ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        rowNums.forEach { num ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(30.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF1E293B))
                                    .clickable { viewModel.insertText(num) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = num, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }
                    }
                }
                // Bottom row: ., 0, _
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    listOf(".", zero, "_").forEach { symb ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(30.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF1E293B))
                                .clickable { viewModel.insertText(symb) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = symb, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }

            // Col 5: %, Switch layout, Delete, Enter
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(30.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFDB2777).copy(alpha = 0.8f))
                        .clickable { viewModel.insertText("%") },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "%", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(30.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFDB2777))
                        .clickable {
                            viewModel.setKeyboardLayout(
                                if (isBangla) KeyboardLayoutType.ENGLISH else KeyboardLayoutType.JATIYA
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = if (isBangla) "১২৩" else "123", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(30.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFE11D48))
                        .clickable { viewModel.backspace() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color.White, modifier = Modifier.size(14.dp))
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(30.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFBE185D))
                        .clickable { viewModel.sendMessage() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "➔", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }
}

// 4. Gboard-Style Text Editor Panel
@Composable
private fun TextEditorPanel(uiState: JonakiUiState, viewModel: JonakiViewModel) {
    Column {
        PanelHeader(
            title = "জি-কীবোর্ড টেক্সট এডিটর ও কার্সর প্যাড",
            titleColor = Color(0xFFF472B6),
            onClose = { viewModel.setSubPanel(KeyboardSubPanel.NONE) }
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Directional Pad (Up, Down, Left, Right)
            Column(
                modifier = Modifier
                    .weight(1.2f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF1E293B))
                    .padding(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconButton(
                    onClick = { viewModel.moveCursor(-5) },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(imageVector = Icons.Default.KeyboardArrowUp, contentDescription = "Up", tint = Color.White)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { viewModel.moveCursor(-1) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Left", tint = Color.White)
                    }
                    Text(text = "কার্সর", fontSize = 10.sp, color = Color(0xFFF59E0B), fontWeight = FontWeight.Bold)
                    IconButton(
                        onClick = { viewModel.moveCursor(1) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Right", tint = Color.White)
                    }
                }
                IconButton(
                    onClick = { viewModel.moveCursor(5) },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = "Down", tint = Color.White)
                }
            }

            // Quick actions column
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Button(
                    onClick = { viewModel.copyToClipboard(uiState.editorText) },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(34.dp)
                ) {
                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "কপি", fontSize = 10.sp)
                }

                Button(
                    onClick = { viewModel.translateEditorText() },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B250B)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(34.dp)
                ) {
                    Text(text = "🪄 অনুবাদ", fontSize = 10.sp, color = Color(0xFFFDE68A), fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { viewModel.clearEditor() },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF450A0A)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(34.dp)
                ) {
                    Text(text = "সব মুছুন", fontSize = 10.sp, color = Color(0xFFFCA5A5))
                }
            }
        }
    }
}

// 5. Shortcuts Manager Panel
@Composable
private fun ShortcutsPanel(uiState: JonakiUiState, viewModel: JonakiViewModel) {
    var isAdding by remember { mutableStateOf(false) }
    var newKey by remember { mutableStateOf("") }
    var newFull by remember { mutableStateOf("") }

    Column {
        PanelHeader(
            title = "শর্টকাট ম্যানেজার",
            titleColor = Color(0xFFFBBF24),
            onClose = { viewModel.setSubPanel(KeyboardSubPanel.NONE) },
            trailingAction = {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFF59E0B))
                        .clickable { isAdding = !isAdding }
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add", tint = Color(0xFF0F172A), modifier = Modifier.size(12.dp))
                        Text(text = "নতুন", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                    }
                }
            }
        )

        if (isAdding) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF1E293B))
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                OutlinedTextField(
                    value = newKey,
                    onValueChange = { newKey = it },
                    placeholder = { Text("শর্টকাট কিওয়ার্ড (যেমন: slm)", fontSize = 11.sp, color = Color.Gray) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFF59E0B),
                        unfocusedBorderColor = Color(0xFF475569),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                )

                OutlinedTextField(
                    value = newFull,
                    onValueChange = { newFull = it },
                    placeholder = { Text("সম্পূর্ণ লেখা (যেমন: আসসালামু আলাইকুম)", fontSize = 11.sp, color = Color.Gray) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFF59E0B),
                        unfocusedBorderColor = Color(0xFF475569),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = { isAdding = false },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Text("বাতিল", fontSize = 10.sp)
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Button(
                        onClick = {
                            viewModel.addShortcut(newKey, newFull)
                            newKey = ""
                            newFull = ""
                            isAdding = false
                        },
                        enabled = newKey.isNotBlank() && newFull.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B), contentColor = Color(0xFF0F172A)),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Text("সংরক্ষণ", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(uiState.shortcutsList) { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF1E293B))
                            .clickable {
                                viewModel.insertText(item.fullText)
                                viewModel.setSubPanel(KeyboardSubPanel.NONE)
                            }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = item.shortcut,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFBBF24)
                        )
                        Text(
                            text = item.fullText,
                            fontSize = 11.sp,
                            color = Color(0xFFCBD5E1),
                            maxLines = 1,
                            modifier = Modifier.weight(1f).padding(start = 12.dp)
                        )
                    }
                }
            }
        }
    }
}

// 6. Text Repeater Panel
@Composable
private fun RepeaterPanel(uiState: JonakiUiState, viewModel: JonakiViewModel) {
    Column {
        PanelHeader(
            title = "টেক্সট রিপিটার টুল",
            titleColor = Color(0xFF2DD4BF),
            onClose = { viewModel.setSubPanel(KeyboardSubPanel.NONE) }
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            OutlinedTextField(
                value = uiState.repeatText,
                onValueChange = { viewModel.setRepeatText(it) },
                placeholder = { Text("লেখা লিখুন...", fontSize = 11.sp, color = Color.Gray) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF2DD4BF),
                    unfocusedBorderColor = Color(0xFF334155),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
            )

            OutlinedTextField(
                value = uiState.repeatCount.toString(),
                onValueChange = { str ->
                    str.filter { it.isDigit() }.toIntOrNull()?.let {
                        viewModel.setRepeatCount(it)
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF2DD4BF),
                    unfocusedBorderColor = Color(0xFF334155),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                modifier = Modifier
                    .width(60.dp)
                    .height(44.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { viewModel.executeTextRepeater() },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D9488), contentColor = Color.White),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(36.dp)
        ) {
            Text(text = "রিপিট করে ইনসার্ট করুন", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}

// 7. Themes Panel
@Composable
private fun ThemesPanel(uiState: JonakiUiState, viewModel: JonakiViewModel) {
    Column {
        PanelHeader(
            title = "থিম স্টোর ও কাস্টমাইজেশন",
            titleColor = Color(0xFFA78BFA),
            onClose = { viewModel.setSubPanel(KeyboardSubPanel.NONE) }
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(KeyboardThemeRepository.themes) { theme ->
                val isSelected = uiState.selectedTheme.id == theme.id
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(theme.backgroundBrush)
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) Color(0xFFF59E0B) else Color(0xFF334155),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .clickable { viewModel.setTheme(theme) }
                        .padding(8.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(theme.keyBackground),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "অ", fontSize = 11.sp, color = theme.keyTextColor)
                            }
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(theme.keyBackground),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "A", fontSize = 11.sp, color = theme.keyTextColor)
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = theme.name,
                            fontSize = 10.sp,
                            color = if (isSelected) Color(0xFFFDE68A) else Color(0xFFE2E8F0),
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
