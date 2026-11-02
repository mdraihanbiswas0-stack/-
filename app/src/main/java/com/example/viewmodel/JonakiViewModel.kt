package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.ChatMessage
import com.example.model.ClipboardItem
import com.example.model.KeyboardLayoutType
import com.example.model.KeyboardSubPanel
import com.example.model.KeyboardTheme
import com.example.model.MessageSender
import com.example.model.ShortcutItem
import com.example.service.FeedbackHelper
import com.example.service.TranslatorEngine
import com.example.ui.theme.KeyboardThemeRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class JonakiUiState(
    val chatMessages: List<ChatMessage> = listOf(
        ChatMessage(
            id = 1L,
            text = "Hello! How is Jonaki keyboard working?",
            sender = MessageSender.THEM,
            time = "১০:৩০ AM"
        ),
        ChatMessage(
            id = 2L,
            text = "ওয়ালাইকুম আসসালাম! এটি দারুণ কাজ করছে।",
            sender = MessageSender.ME,
            time = "১০:৩১ AM"
        ),
        ChatMessage(
            id = 3L,
            text = "Can we meet at 4 PM for the project discussion?",
            sender = MessageSender.THEM,
            time = "১০:৩২ AM"
        )
    ),
    val editorText: String = "আমি বিকেলে আসছি, ইনশাআল্লাহ",
    val cursorPosition: Int = 29,
    val activeKbLayout: KeyboardLayoutType = KeyboardLayoutType.JATIYA,
    val isShift: Boolean = false,
    val showNumberRow: Boolean = true,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val selectedTheme: KeyboardTheme = KeyboardThemeRepository.defaultTheme,
    val activeSubPanel: KeyboardSubPanel = KeyboardSubPanel.NONE,
    val shortcutsList: List<ShortcutItem> = listOf(
        ShortcutItem("slm", "আসসালামু আলাইকুম ওয়া রাহমাতুল্লাহ"),
        ShortcutItem("tq", "ধন্যবাদ আপনাকে!"),
        ShortcutItem("al", "আলহামদুলিল্লাহ, ভালো আছি।"),
        ShortcutItem("meeting", "Can we meet at 4 PM today?")
    ),
    val clipboardList: List<ClipboardItem> = listOf(
        ClipboardItem(1L, "📌 জোনাকি কীবোর্ড প্রো - জাদুকাঠি অনুবাদক", true),
        ClipboardItem(2L, "ঢাকা, বাংলাদেশ", false),
        ClipboardItem(3L, "https://github.com", false)
    ),
    val isVoiceListening: Boolean = false,
    val repeatText: String = "জোনাকি কীবোর্ড ",
    val repeatCount: Int = 3,
    val isMagicWandMode: Boolean = false,
    val magicHoverTarget: String? = null,
    val toastMessage: String? = null
)

class JonakiViewModel(application: Application) : AndroidViewModel(application) {

    private val feedback = FeedbackHelper(application)

    private val _uiState = MutableStateFlow(JonakiUiState())
    val uiState: StateFlow<JonakiUiState> = _uiState.asStateFlow()

    private val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())

    fun insertText(str: String) {
        feedback.playKeyClick(_uiState.value.soundEnabled, _uiState.value.vibrationEnabled)
        _uiState.update { state ->
            val cur = state.cursorPosition.coerceIn(0, state.editorText.length)
            val before = state.editorText.substring(0, cur)
            val after = state.editorText.substring(cur)
            val newText = before + str + after
            state.copy(
                editorText = newText,
                cursorPosition = cur + str.length
            )
        }
    }

    fun backspace() {
        feedback.playKeyClick(_uiState.value.soundEnabled, _uiState.value.vibrationEnabled)
        _uiState.update { state ->
            val cur = state.cursorPosition.coerceIn(0, state.editorText.length)
            if (cur > 0) {
                val before = state.editorText.substring(0, cur - 1)
                val after = state.editorText.substring(cur)
                state.copy(
                    editorText = before + after,
                    cursorPosition = cur - 1
                )
            } else {
                state
            }
        }
    }

    fun clearEditor() {
        _uiState.update { it.copy(editorText = "", cursorPosition = 0) }
    }

    fun setCursorPosition(pos: Int) {
        _uiState.update { state ->
            val bounded = pos.coerceIn(0, state.editorText.length)
            state.copy(cursorPosition = bounded)
        }
    }

    fun moveCursor(delta: Int) {
        feedback.playKeyClick(_uiState.value.soundEnabled, _uiState.value.vibrationEnabled)
        _uiState.update { state ->
            val newPos = (state.cursorPosition + delta).coerceIn(0, state.editorText.length)
            state.copy(cursorPosition = newPos)
        }
    }

    fun updateEditorText(text: String, newCursor: Int? = null) {
        _uiState.update { state ->
            state.copy(
                editorText = text,
                cursorPosition = newCursor?.coerceIn(0, text.length) ?: text.length
            )
        }
    }

    fun sendMessage() {
        val currentText = _uiState.value.editorText.trim()
        if (currentText.isEmpty()) return

        feedback.playKeyClick(_uiState.value.soundEnabled, _uiState.value.vibrationEnabled)
        val now = timeFormat.format(Date())
        val newMsg = ChatMessage(
            id = System.currentTimeMillis(),
            text = currentText,
            sender = MessageSender.ME,
            time = now
        )

        _uiState.update { state ->
            state.copy(
                chatMessages = state.chatMessages + newMsg,
                editorText = "",
                cursorPosition = 0
            )
        }
    }

    // --- Magic Wand Translation Handlers ---

    /**
     * Translates a message in the chat thread.
     * If already translated, toggles back to the original text.
     */
    fun translateMessage(messageId: Long) {
        feedback.playMagicWandChime(_uiState.value.soundEnabled, _uiState.value.vibrationEnabled)
        _uiState.update { state ->
            val updated = state.chatMessages.map { msg ->
                if (msg.id == messageId) {
                    if (msg.isTranslated && msg.originalText != null) {
                        // Revert to original
                        msg.copy(
                            text = msg.originalText,
                            isTranslated = false,
                            translationDirection = null
                        )
                    } else {
                        val result = TranslatorEngine.translate(msg.text)
                        msg.copy(
                            originalText = msg.text,
                            text = result.translatedText,
                            isTranslated = true,
                            translationDirection = result.direction
                        )
                    }
                } else {
                    msg
                }
            }
            state.copy(
                chatMessages = updated,
                toastMessage = "🪄 জাদুকাঠি দিয়ে মেসেজ অনুবাদ করা হয়েছে!"
            )
        }
        autoClearToast()
    }

    /**
     * Translates the active text typed in the keyboard input editor.
     * Bengali becomes English; English becomes Bengali.
     */
    fun translateEditorText() {
        val current = _uiState.value.editorText.trim()
        if (current.isEmpty()) return

        feedback.playMagicWandChime(_uiState.value.soundEnabled, _uiState.value.vibrationEnabled)
        val result = TranslatorEngine.translate(current)
        _uiState.update { state ->
            state.copy(
                editorText = result.translatedText,
                cursorPosition = result.translatedText.length,
                toastMessage = "🪄 জাদুকাঠি অনুবাদ সম্পন্ন (${result.direction})"
            )
        }
        autoClearToast()
    }

    /**
     * Handles dropping or clicking the Magic Wand onto a target element.
     * targetKey can be "msg_{id}" or "editor_input"
     */
    fun onMagicWandDrop(targetKey: String) {
        when {
            targetKey.startsWith("msg_") -> {
                val idStr = targetKey.removePrefix("msg_")
                idStr.toLongOrNull()?.let { msgId ->
                    translateMessage(msgId)
                }
            }
            targetKey == "editor_input" -> {
                translateEditorText()
            }
        }
        setMagicWandMode(false)
        setMagicHoverTarget(null)
    }

    fun setMagicWandMode(active: Boolean) {
        feedback.playKeyClick(_uiState.value.soundEnabled, _uiState.value.vibrationEnabled)
        _uiState.update { it.copy(isMagicWandMode = active) }
    }

    fun setMagicHoverTarget(target: String?) {
        _uiState.update { it.copy(magicHoverTarget = target) }
    }

    // --- Keyboard Controls ---

    fun toggleShift() {
        feedback.playKeyClick(_uiState.value.soundEnabled, _uiState.value.vibrationEnabled)
        _uiState.update { it.copy(isShift = !it.isShift) }
    }

    fun setKeyboardLayout(layout: KeyboardLayoutType) {
        feedback.playKeyClick(_uiState.value.soundEnabled, _uiState.value.vibrationEnabled)
        _uiState.update { it.copy(activeKbLayout = layout, isShift = false) }
    }

    fun setSubPanel(panel: KeyboardSubPanel) {
        feedback.playKeyClick(_uiState.value.soundEnabled, _uiState.value.vibrationEnabled)
        _uiState.update {
            it.copy(activeSubPanel = if (it.activeSubPanel == panel) KeyboardSubPanel.NONE else panel)
        }
    }

    fun setTheme(theme: KeyboardTheme) {
        _uiState.update { it.copy(selectedTheme = theme, activeSubPanel = KeyboardSubPanel.NONE) }
    }

    fun toggleNumberRow() {
        _uiState.update { it.copy(showNumberRow = !it.showNumberRow) }
    }

    fun toggleSound() {
        _uiState.update { it.copy(soundEnabled = !it.soundEnabled) }
    }

    fun toggleVibration() {
        _uiState.update { it.copy(vibrationEnabled = !it.vibrationEnabled) }
    }

    // --- Shortcuts & Clipboard ---

    fun addShortcut(shortcut: String, fullText: String) {
        if (shortcut.isBlank() || fullText.isBlank()) return
        _uiState.update { state ->
            state.copy(
                shortcutsList = state.shortcutsList + ShortcutItem(shortcut.trim(), fullText.trim())
            )
        }
    }

    fun deleteShortcut(shortcut: String) {
        _uiState.update { state ->
            state.copy(shortcutsList = state.shortcutsList.filterNot { it.shortcut == shortcut })
        }
    }

    fun copyToClipboard(text: String) {
        if (text.isBlank()) return
        val newItem = ClipboardItem(System.currentTimeMillis(), text, false)
        _uiState.update { state ->
            state.copy(
                clipboardList = listOf(newItem) + state.clipboardList,
                toastMessage = "ক্লিপবোর্ডে কপি হয়েছে"
            )
        }
        autoClearToast()
    }

    fun toggleVoiceTyping() {
        feedback.playKeyClick(_uiState.value.soundEnabled, _uiState.value.vibrationEnabled)
        val currentlyListening = _uiState.value.isVoiceListening
        _uiState.update { it.copy(isVoiceListening = !currentlyListening) }

        if (!currentlyListening) {
            // Simulate voice recognition sample after short delay
            viewModelScope.launch {
                delay(1200)
                if (_uiState.value.isVoiceListening) {
                    val voiceSample = if (_uiState.value.activeKbLayout == KeyboardLayoutType.JATIYA) {
                        "আসসালামু আলাইকুম ভাইয়া"
                    } else {
                        "Hello my friend"
                    }
                    insertText(if (_uiState.value.editorText.isEmpty()) voiceSample else " $voiceSample")
                    _uiState.update { it.copy(isVoiceListening = false) }
                }
            }
        }
    }

    fun setRepeatText(text: String) {
        _uiState.update { it.copy(repeatText = text) }
    }

    fun setRepeatCount(count: Int) {
        _uiState.update { it.copy(repeatCount = count.coerceIn(1, 100)) }
    }

    fun executeTextRepeater() {
        val base = _uiState.value.repeatText
        val count = _uiState.value.repeatCount
        if (base.isNotBlank() && count > 0) {
            val repeated = (1..count).joinToString(" ") { base.trim() }
            insertText(repeated)
            setSubPanel(KeyboardSubPanel.NONE)
        }
    }

    fun addSampleMessage(isEnglish: Boolean) {
        val sample = if (isEnglish) {
            listOf(
                "Good morning! Are you available for a quick call?",
                "What is your plan for tonight?",
                "Jonaki keyboard is amazing!",
                "Can you help me please?"
            ).random()
        } else {
            listOf(
                "শুভ সকাল! কেমন আছেন সবাই?",
                "আমি এখন অফিসের কাজে ব্যস্ত আছি।",
                "পরে আপনার সাথে কথা বলব।",
                "ধন্যবাদ আপনাকে অনেক!"
            ).random()
        }
        val now = timeFormat.format(Date())
        val msg = ChatMessage(
            id = System.currentTimeMillis(),
            text = sample,
            sender = MessageSender.THEM,
            time = now
        )
        _uiState.update { it.copy(chatMessages = it.chatMessages + msg) }
    }

    private fun autoClearToast() {
        viewModelScope.launch {
            delay(2800)
            _uiState.update { it.copy(toastMessage = null) }
        }
    }
}
