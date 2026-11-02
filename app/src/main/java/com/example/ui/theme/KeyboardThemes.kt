package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.model.KeyboardTheme

object KeyboardThemeRepository {
    val themes = listOf(
        KeyboardTheme(
            id = "gboard-dark",
            name = "জিবোর্ড ডার্ক",
            backgroundBrush = Brush.verticalGradient(
                listOf(Color(0xFF1E2124), Color(0xFF121416))
            ),
            keyBackground = Color(0xFF2D3035),
            keyTextColor = Color(0xFFF1F3F4),
            accentColor = Color(0xFF8AB4F8),
            toolbarBackground = Color(0xFF181A1D),
            isDark = true
        ),
        KeyboardTheme(
            id = "jonaki-gold",
            name = "জোনাকি গোল্ড",
            backgroundBrush = Brush.verticalGradient(
                listOf(Color(0xFF28180A), Color(0xFF140B04))
            ),
            keyBackground = Color(0xFF3F2714),
            keyTextColor = Color(0xFFFDE68A),
            accentColor = Color(0xFFF59E0B),
            toolbarBackground = Color(0xFF1E1106),
            isDark = true
        ),
        KeyboardTheme(
            id = "cyber-cyan",
            name = "সাইবার সাইয়ান",
            backgroundBrush = Brush.verticalGradient(
                listOf(Color(0xFF0D1E2D), Color(0xFF060F17))
            ),
            keyBackground = Color(0xFF173047),
            keyTextColor = Color(0xFFE0F2FE),
            accentColor = Color(0xFF38BDF8),
            toolbarBackground = Color(0xFF091622),
            isDark = true
        ),
        KeyboardTheme(
            id = "amoled-black",
            name = "অ্যামোলেড ব্ল্যাক",
            backgroundBrush = Brush.verticalGradient(
                listOf(Color(0xFF09090B), Color(0xFF000000))
            ),
            keyBackground = Color(0xFF18181B),
            keyTextColor = Color(0xFFFAFAFA),
            accentColor = Color(0xFF34D399),
            toolbarBackground = Color(0xFF09090B),
            isDark = true
        ),
        KeyboardTheme(
            id = "sunset-rose",
            name = "সানসেট রোজ",
            backgroundBrush = Brush.verticalGradient(
                listOf(Color(0xFF2E1027), Color(0xFF170814))
            ),
            keyBackground = Color(0xFF481C3E),
            keyTextColor = Color(0xFFFCE7F3),
            accentColor = Color(0xFFF472B6),
            toolbarBackground = Color(0xFF230B1E),
            isDark = true
        )
    )

    val defaultTheme = themes[0]
}
