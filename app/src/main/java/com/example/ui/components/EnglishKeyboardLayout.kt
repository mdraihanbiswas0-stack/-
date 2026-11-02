package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.KeyboardLayoutType
import com.example.viewmodel.JonakiUiState
import com.example.viewmodel.JonakiViewModel

@Composable
fun EnglishKeyboardLayout(
    uiState: JonakiUiState,
    viewModel: JonakiViewModel,
    modifier: Modifier = Modifier
) {
    val numberRowEng = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")
    val row1 = listOf("q", "w", "e", "r", "t", "y", "u", "i", "o", "p")
    val row2 = listOf("a", "s", "d", "f", "g", "h", "j", "k", "l")
    val row3 = listOf("z", "x", "c", "v", "b", "n", "m")

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
                numberRowEng.forEach { num ->
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
            row1.forEach { char ->
                val display = if (uiState.isShift) char.uppercase() else char
                KeyButton(
                    text = display,
                    weight = 1.0f,
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
            row2.forEach { char ->
                val display = if (uiState.isShift) char.uppercase() else char
                KeyButton(
                    text = display,
                    weight = 1.0f,
                    bgColor = uiState.selectedTheme.keyBackground,
                    textColor = uiState.selectedTheme.keyTextColor,
                    onClick = { viewModel.insertText(display) }
                )
            }
        }

        // Row 3 (Shift, Z-M, Backspace)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            KeyButton(
                text = "⇧",
                weight = 1.4f,
                bgColor = if (uiState.isShift) uiState.selectedTheme.accentColor else uiState.selectedTheme.keyBackground,
                textColor = if (uiState.isShift) Color(0xFF0F172A) else uiState.selectedTheme.keyTextColor,
                onClick = { viewModel.toggleShift() },
                tag = "eng_shift_key"
            )

            row3.forEach { char ->
                val display = if (uiState.isShift) char.uppercase() else char
                KeyButton(
                    text = display,
                    weight = 1.0f,
                    bgColor = uiState.selectedTheme.keyBackground,
                    textColor = uiState.selectedTheme.keyTextColor,
                    onClick = { viewModel.insertText(display) }
                )
            }

            KeyButton(
                text = "⌫",
                weight = 1.4f,
                bgColor = uiState.selectedTheme.keyBackground,
                textColor = Color(0xFFF87171),
                onClick = { viewModel.backspace() },
                tag = "eng_backspace_key"
            )
        }

        // Row 4 (Switch to Bangla, comma, Spacebar, dot, Enter)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            KeyButton(
                text = "বাংলা",
                weight = 1.3f,
                bgColor = uiState.selectedTheme.keyBackground,
                textColor = uiState.selectedTheme.accentColor,
                onClick = { viewModel.setKeyboardLayout(KeyboardLayoutType.JATIYA) },
                tag = "switch_to_bengali"
            )

            KeyButton(
                text = ",",
                weight = 0.9f,
                bgColor = uiState.selectedTheme.keyBackground,
                textColor = uiState.selectedTheme.keyTextColor,
                onClick = { viewModel.insertText(",") }
            )

            KeyButton(
                text = "English (US) - Jonaki",
                weight = 3.8f,
                bgColor = uiState.selectedTheme.keyBackground,
                textColor = uiState.selectedTheme.keyTextColor.copy(alpha = 0.85f),
                fontSize = 11.sp,
                onClick = { viewModel.insertText(" ") },
                tag = "eng_spacebar_key"
            )

            KeyButton(
                text = ".",
                weight = 0.9f,
                bgColor = uiState.selectedTheme.keyBackground,
                textColor = uiState.selectedTheme.keyTextColor,
                onClick = { viewModel.insertText(".") }
            )

            KeyButton(
                text = "↵",
                weight = 1.4f,
                bgColor = uiState.selectedTheme.accentColor,
                textColor = Color(0xFF0F172A),
                fontWeight = FontWeight.Black,
                onClick = { viewModel.sendMessage() },
                tag = "eng_enter_key"
            )
        }
    }
}
