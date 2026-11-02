package com.example

import com.example.service.TranslatorEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testBengaliToEnglish() {
        val result = TranslatorEngine.translate("কেমন আছেন?")
        assertEquals("BN ➔ EN", result.direction)
        assertEquals("How are you?", result.translatedText)
    }

    @Test
    fun testEnglishToBengali() {
        val result = TranslatorEngine.translate("How are you?")
        assertEquals("EN ➔ BN", result.direction)
        assertEquals("আপনি কেমন আছেন?", result.translatedText)
    }

    @Test
    fun testWorkChatTranslation() {
        val enResult = TranslatorEngine.translate("Can we meet at 4 PM for the project discussion?")
        assertEquals("EN ➔ BN", enResult.direction)
        assertEquals("আমরা কি প্রকল্প আলোচনার জন্য বিকেল ৪টায় দেখা করতে পারি?", enResult.translatedText)

        val bnResult = TranslatorEngine.translate("আমি আসছি")
        assertEquals("BN ➔ EN", bnResult.direction)
        assertEquals("I am on my way", bnResult.translatedText)
    }

    @Test
    fun testLanguageDetection() {
        assertTrue(TranslatorEngine.isBengali("জোনাকি কীবোর্ড"))
        assertTrue(!TranslatorEngine.isBengali("Jonaki Keyboard"))
    }
}
