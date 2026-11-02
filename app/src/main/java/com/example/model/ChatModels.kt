package com.example.model

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

enum class MessageSender {
    ME, THEM
}

data class ChatMessage(
    val id: Long,
    val text: String,
    val sender: MessageSender,
    val time: String,
    val originalText: String? = null,
    val isTranslated: Boolean = false,
    val translationDirection: String? = null // "EN ➔ BN" or "BN ➔ EN"
)

enum class KeyboardSubPanel {
    NONE, EMOJI, CLIPBOARD, NUMPAD, TEXT_EDITOR, SHORTCUTS, REPEATER, THEMES
}

enum class KeyboardLayoutType {
    JATIYA, ENGLISH
}

data class ShortcutItem(
    val shortcut: String,
    val fullText: String
)

data class ClipboardItem(
    val id: Long,
    val text: String,
    val isPinned: Boolean = false
)

data class KeyboardTheme(
    val id: String,
    val name: String,
    val backgroundBrush: Brush,
    val keyBackground: Color,
    val keyTextColor: Color,
    val accentColor: Color,
    val toolbarBackground: Color,
    val isDark: Boolean = true
)
