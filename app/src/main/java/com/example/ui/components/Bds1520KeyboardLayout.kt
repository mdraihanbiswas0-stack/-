package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.KeyboardLayoutType
import com.example.viewmodel.JonakiUiState
import com.example.viewmodel.JonakiViewModel

data class KeyData(
    val normal: String,
    val shift: String = normal,
    val keyType: KeyType = KeyType.CHARACTER,
    val weight: Float = 1.0f
)

enum class KeyType {
    CHARACTER, SHIFT, BACKSPACE, ENTER, SWITCH_LANG, GLOBE, SPACE
}

@Composable
fun Bds1520KeyboardLayout(
    uiState: JonakiUiState,
    viewModel: JonakiViewModel,
    modifier: Modifier = Modifier
) {
    val numberRowBengali = listOf("১", "২", "৩", "৪", "৫", "৬", "৭", "৮", "৯", "০")

    val row1 = listOf(
        KeyData("ঢ", "ঢ়"), KeyData("ড়", "ঢ়"), KeyData("ী", "ঐ"),
        KeyData("ূ", "ঊ"), KeyData("ূ", "্ত"), KeyData("্য", "্"),
        KeyData("ু", "উ"), KeyData("ি", "ই"), KeyData("ো", "ও"),
        KeyData("ৌ", "ঔ")
    )

    val row2 = listOf(
        KeyData("ও", "অ"), KeyData("আ", "আ"), KeyData("ই", "ই"),
        KeyData("া", "অ"), KeyData("ক", "খ"), KeyData("ব", "ভ"),
        KeyData("হ", "ঙ"), KeyData("গ", "ঘ"), KeyData("দ", "ধ"),
        KeyData("জ", "ঝ")
    )

    val row3 = listOf(
        KeyData("⇧", "⇧", KeyType.SHIFT, 1.4f),
        KeyData("ং", "ঁ"), KeyData("ো", "ৌ"), KeyData("চ", "ছ"),
        KeyData("র", "ড়"), KeyData("ন", "ণ"), KeyData("ম", "শ"),
        KeyData("ল", "ষ"), KeyData("স", "স"),
        KeyData("⌫", "⌫", KeyType.BACKSPACE, 1.4f)
    )

    val row4 = listOf(
        KeyData("ENG", "ENG", KeyType.SWITCH_LANG, 1.3f),
        KeyData("🌐", "🌐", KeyType.GLOBE, 1.0f),
        KeyData("জোনাকি কীবোর্ড (BDS 1520)", " ", KeyType.SPACE, 4.0f),
        KeyData("।", "।", KeyType.CHARACTER, 1.0f),
        KeyData("↵", "↵", KeyType.ENTER, 1.4f)
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 2.dp, vertical = 2.dp),
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        // Optional Number Row
        if (uiState.showNumberRow) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                numberRowBengali.forEach { num ->
                    KeyButton(
                        text = num,
                        weight = 1.0f,
                        bgColor = uiState.selectedTheme.keyBackground,
                        textColor = uiState.selectedTheme.keyTextColor,
                        onClick = { viewModel.insertText(num) }
                    )
                }
            }
        }

        // Row 1
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            row1.forEach { k ->
                val display = if (uiState.isShift) k.shift else k.normal
                KeyButton(
                    text = display,
                    weight = k.weight,
                    bgColor = uiState.selectedTheme.keyBackground,
                    textColor = uiState.selectedTheme.keyTextColor,
                    onClick = { viewModel.insertText(display) }
                )
            }
        }

        // Row 2
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            row2.forEach { k ->
                val display = if (uiState.isShift) k.shift else k.normal
                KeyButton(
                    text = display,
                    weight = k.weight,
                    bgColor = uiState.selectedTheme.keyBackground,
                    textColor = uiState.selectedTheme.keyTextColor,
                    onClick = { viewModel.insertText(display) }
                )
            }
        }

        // Row 3
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            row3.forEach { k ->
                when (k.keyType) {
                    KeyType.SHIFT -> {
                        KeyButton(
                            text = "⇧",
                            weight = k.weight,
                            bgColor = if (uiState.isShift) uiState.selectedTheme.accentColor else uiState.selectedTheme.keyBackground,
                            textColor = if (uiState.isShift) Color(0xFF0F172A) else uiState.selectedTheme.keyTextColor,
                            onClick = { viewModel.toggleShift() },
                            tag = "shift_key"
                        )
                    }
                    KeyType.BACKSPACE -> {
                        KeyButton(
                            text = "⌫",
                            weight = k.weight,
                            bgColor = uiState.selectedTheme.keyBackground,
                            textColor = Color(0xFFF87171),
                            onClick = { viewModel.backspace() },
                            tag = "backspace_key"
                        )
                    }
                    else -> {
                        val display = if (uiState.isShift) k.shift else k.normal
                        KeyButton(
                            text = display,
                            weight = k.weight,
                            bgColor = uiState.selectedTheme.keyBackground,
                            textColor = uiState.selectedTheme.keyTextColor,
                            onClick = { viewModel.insertText(display) }
                        )
                    }
                }
            }
        }

        // Row 4
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            row4.forEach { k ->
                when (k.keyType) {
                    KeyType.SWITCH_LANG -> {
                        KeyButton(
                            text = "ENG",
                            weight = k.weight,
                            bgColor = uiState.selectedTheme.keyBackground,
                            textColor = uiState.selectedTheme.accentColor,
                            onClick = { viewModel.setKeyboardLayout(KeyboardLayoutType.ENGLISH) },
                            tag = "switch_to_eng"
                        )
                    }
                    KeyType.GLOBE -> {
                        KeyButton(
                            text = "🌐",
                            weight = k.weight,
                            bgColor = uiState.selectedTheme.keyBackground,
                            textColor = uiState.selectedTheme.keyTextColor,
                            onClick = { viewModel.setKeyboardLayout(KeyboardLayoutType.ENGLISH) }
                        )
                    }
                    KeyType.SPACE -> {
                        KeyButton(
                            text = "জোনাকি কীবোর্ড (BDS 1520)",
                            weight = k.weight,
                            bgColor = uiState.selectedTheme.keyBackground,
                            textColor = uiState.selectedTheme.keyTextColor.copy(alpha = 0.85f),
                            fontSize = 11.sp,
                            onClick = { viewModel.insertText(" ") },
                            tag = "spacebar_key"
                        )
                    }
                    KeyType.ENTER -> {
                        KeyButton(
                            text = "↵",
                            weight = k.weight,
                            bgColor = uiState.selectedTheme.accentColor,
                            textColor = Color(0xFF0F172A),
                            fontWeight = FontWeight.Black,
                            onClick = { viewModel.sendMessage() },
                            tag = "enter_key"
                        )
                    }
                    else -> {
                        KeyButton(
                            text = k.normal,
                            weight = k.weight,
                            bgColor = uiState.selectedTheme.keyBackground,
                            textColor = uiState.selectedTheme.keyTextColor,
                            onClick = { viewModel.insertText(k.normal) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RowScope.KeyButton(
    text: String,
    weight: Float,
    bgColor: Color,
    textColor: Color,
    onClick: () -> Unit,
    fontSize: androidx.compose.ui.unit.TextUnit = 14.sp,
    fontWeight: FontWeight = FontWeight.SemiBold,
    tag: String = "key_$text"
) {
    Box(
        modifier = Modifier
            .weight(weight)
            .height(42.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .border(0.5.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(6.dp))
            .clickable { onClick() }
            .testTag(tag),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = fontSize,
            fontWeight = fontWeight,
            color = textColor,
            maxLines = 1
        )
    }
}
