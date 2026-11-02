package com.example.service

/**
 * Intelligent Bidirectional Bengali <-> English Translation Engine for Jonaki Keyboard.
 * Provides instant translation without requiring internet or external API keys,
 * supporting colloquial chat, greetings, questions, daily conversation, and word-level fallback.
 */
object TranslatorEngine {

    fun isBengali(text: String): Boolean {
        return text.any { it in '\u0980'..'\u09FF' }
    }

    data class TranslationResult(
        val originalText: String,
        val translatedText: String,
        val direction: String // "EN ➔ BN" or "BN ➔ EN"
    )

    fun translate(text: String): TranslationResult {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) {
            return TranslationResult(text, text, "Auto")
        }

        return if (isBengali(trimmed)) {
            val trans = translateBengaliToEnglish(trimmed)
            TranslationResult(text, trans, "BN ➔ EN")
        } else {
            val trans = translateEnglishToBengali(trimmed)
            TranslationResult(text, trans, "EN ➔ BN")
        }
    }

    // --- Phrase Dictionaries ---
    private val bnToEnPhrases = mapOf(
        // Greetings & Social
        "আসসালামু আলাইকুম" to "Assalamu Alaikum",
        "আসসালামু আলাইকুম ওয়া রাহমাতুল্লাহ" to "Assalamu Alaikum wa Rahmatullah",
        "ওয়ালাইকুম আসসালাম" to "Wa Alaikum Assalam",
        "কেমন আছেন?" to "How are you?",
        "কেমন আছেন" to "How are you",
        "কেমন আছো?" to "How are you?",
        "কেমন আছো" to "How are you",
        "কেমন চলল?" to "How did it go?",
        "কেমন চলছে?" to "How is it going?",
        "কেমন চলছে" to "How is it going",
        "আমি ভালো আছি" to "I am fine",
        "আমি ভালো আছি, ধন্যবাদ" to "I am good, thank you",
        "আলহামদুলিল্লাহ, ভালো আছি" to "Alhamdulillah, I am doing well",
        "ধন্যবাদ" to "Thank you",
        "আপনাকে ধন্যবাদ" to "Thank you",
        "ধন্যবাদ আপনাকে!" to "Thank you very much!",
        "অনেক ধন্যবাদ" to "Thanks a lot",
        "স্বাগতম" to "You are welcome",
        "শুভ সকাল" to "Good morning",
        "শুভ দুপুর" to "Good afternoon",
        "শুভ সন্ধ্যা" to "Good evening",
        "শুভ রাত্রি" to "Good night",
        "বিদায়" to "Goodbye",
        "পরে কথা হবে" to "Talk to you later",
        "শীঘ্রই দেখা হবে" to "See you soon",
        "দেখা হবে" to "See you",

        // Jonaki Keyboard & Tech
        "জোনাকি কীবোর্ড কেমন কাজ করছে?" to "How is Jonaki keyboard working?",
        "জোনাকি কীবোর্ড কেমন কাজ করছে" to "How is Jonaki keyboard working",
        "এটি দারুণ কাজ করছে" to "It is working great",
        "এটি দারুণ কাজ করছে।" to "It is working great.",
        "জোনাকি কীবোর্ড দিয়ে স্বাচ্ছন্দ্যে টাইপ করুন" to "Type smoothly with Jonaki Keyboard",
        "জোনাকি কীবোর্ড চমৎকার" to "Jonaki keyboard is amazing",
        "জাদুকাঠি দিয়ে অনুবাদ করুন" to "Translate with the magic wand",

        // Chat & Everyday Talk
        "আপনি কি করছেন?" to "What are you doing?",
        "তুমি কি করছ?" to "What are you doing?",
        "কি করছ?" to "What are you doing?",
        "কি খবর?" to "What's up?",
        "কি খবর" to "What's up",
        "সব কিছু ঠিক আছে?" to "Is everything okay?",
        "হ্যাঁ, সব ঠিক আছে" to "Yes, everything is fine",
        "আমি এখন ব্যস্ত আছি" to "I am busy right now",
        "আমি এখন ফ্রি আছি" to "I am free right now",
        "কখন আসবেন?" to "When will you come?",
        "কখন আসবে?" to "When will you come?",
        "আমি আসছি" to "I am on my way",
        "আমি এখনই আসছি" to "I am coming right now",
        "আমি সেখানে যাব" to "I will go there",
        "কোথায় আছো?" to "Where are you?",
        "কোথায় আছেন?" to "Where are you?",
        "আমি বাসায় আছি" to "I am at home",
        "আমি অফিসে আছি" to "I am at the office",
        "আমাকে একটু সাহায্য করবেন?" to "Can you help me please?",
        "আমাকে সাহায্য করো" to "Please help me",
        "চলো একসাথে কাজ করি" to "Let's work together",
        "আমি তোমাকে মিস করছি" to "I miss you",
        "আমি তোমাকে ভালোবাসি" to "I love you",
        "খাবার খেয়েছ?" to "Have you eaten?",
        "দেরি হয়ে গেছে" to "It's getting late",
        "সাবধানে থেকো" to "Take care",
        "ভালো থেকো" to "Stay well",
        "ইনশাআল্লাহ" to "Inshallah (God willing)",
        "মাশাআল্লাহ" to "MashaAllah",
        "অবশ্যই" to "Of course",
        "একদম ঠিক" to "Exactly right"
    )

    private val enToBnPhrases = mapOf(
        // Greetings & Social
        "assalamu alaikum" to "আসসালামু আলাইকুম",
        "wa alaikum assalam" to "ওয়ালাইকুম আসসালাম",
        "hello" to "হ্যালো",
        "hi" to "হাই",
        "how are you?" to "আপনি কেমন আছেন?",
        "how are you" to "কেমন আছেন?",
        "how are you doing today?" to "আজ আপনার দিন কেমন কাটছে?",
        "how is it going?" to "কেমন চলছে?",
        "how is it going" to "কেমন চলছে?",
        "what's up?" to "কি খবর?",
        "what's up" to "কি খবর?",
        "i am fine" to "আমি ভালো আছি",
        "i am doing well" to "আমি ভালো আছি",
        "i am fine, thank you" to "আমি ভালো আছি, ধন্যবাদ",
        "thank you" to "ধন্যবাদ",
        "thank you very much" to "আপনাকে অনেক ধন্যবাদ",
        "thanks" to "ধন্যবাদ",
        "thanks a lot" to "অনেক ধন্যবাদ",
        "you are welcome" to "আপনাকে স্বাগতম",
        "good morning" to "শুভ সকাল",
        "good afternoon" to "শুভ দুপুর",
        "good evening" to "শুভ সন্ধ্যা",
        "good night" to "শুভ রাত্রি",
        "goodbye" to "বিদায়",
        "bye" to "বিদায়",
        "see you soon" to "শীঘ্রই দেখা হবে",
        "talk to you later" to "পরে কথা হবে",

        // Jonaki & Tech
        "how is jonaki keyboard working?" to "জোনাকি কীবোর্ড কেমন কাজ করছে?",
        "jonaki keyboard is amazing!" to "জোনাকি কীবোর্ড অসাধারণ!",
        "jonaki keyboard is great" to "জোনাকি কীবোর্ড দারুণ",
        "it is working great." to "এটি দারুণ কাজ করছে।",
        "it is working great" to "এটি দারুণ কাজ করছে",
        "type smoothly with jonaki keyboard" to "জোনাকি কীবোর্ড দিয়ে স্বাচ্ছন্দ্যে টাইপ করুন",
        "magic wand translation" to "জাদুকাঠি অনুবাদ",

        // Everyday & Meeting
        "can we meet at 4 pm for the project discussion?" to "আমরা কি প্রকল্প আলোচনার জন্য বিকেল ৪টায় দেখা করতে পারি?",
        "can we meet today?" to "আমরা কি আজ দেখা করতে পারি?",
        "what are you doing?" to "আপনি কি করছেন?",
        "what are you doing" to "কি করছেন?",
        "where are you?" to "আপনি কোথায় আছেন?",
        "where are you going?" to "আপনি কোথায় যাচ্ছেন?",
        "when will you come?" to "কখন আসবেন?",
        "i am on my way" to "আমি আসছি",
        "i am coming now" to "আমি এখনই আসছি",
        "i am free at 4 pm. see you there." to "আমি বিকেল ৪টায় ফ্রি আছি। সেখানে দেখা হবে।",
        "i am busy right now" to "আমি এখন ব্যস্ত আছি",
        "i am at home" to "আমি বাসায় আছি",
        "i am at office" to "আমি অফিসে আছি",
        "can you help me please?" to "আমাকে একটু সাহায্য করবেন?",
        "can you help me?" to "আমাকে সাহায্য করতে পারবেন?",
        "please help me" to "দয়া করে আমাকে সাহায্য করুন",
        "let's work together" to "চলো একসাথে কাজ করি",
        "take care" to "নিজের যত্ন নিও",
        "stay safe" to "নিরাপদে থেকো",
        "i love you" to "আমি তোমাকে ভালোবাসি",
        "have you eaten?" to "খাবার খেয়েছেন?",
        "yes" to "হ্যাঁ",
        "no" to "না",
        "of course" to "অবশ্যই",
        "all right" to "ঠিক আছে",
        "okay" to "ঠিক আছে"
    )

    // Token-level Word Dictionary
    private val bnToEnWords = mapOf(
        "আমি" to "I",
        "তুমি" to "you",
        "আপনি" to "you",
        "সে" to "he/she",
        "তিনি" to "he/she",
        "আমরা" to "we",
        "তোমরা" to "you all",
        "তারা" to "they",
        "এটা" to "this",
        "এটি" to "this",
        "ওটা" to "that",
        "কি" to "what",
        "কী" to "what",
        "কেন" to "why",
        "কোথায়" to "where",
        "কখন" to "when",
        "কিভাবে" to "how",
        "কে" to "who",
        "কার" to "whose",
        "হ্যাঁ" to "yes",
        "না" to "no",
        "ভালো" to "good",
        "খারাপ" to "bad",
        "দারুণ" to "great",
        "সুন্দর" to "beautiful",
        "অসাধারণ" to "amazing",
        "নতুন" to "new",
        "পুরানো" to "old",
        "বড়" to "big",
        "ছোট" to "small",
        "আজ" to "today",
        "আজকে" to "today",
        "কাল" to "tomorrow",
        "গতকাল" to "yesterday",
        "এখন" to "now",
        "পরে" to "later",
        "বাসা" to "home",
        "বাসায়" to "at home",
        "অফিস" to "office",
        "কাজ" to "work",
        "মেসেজ" to "message",
        "লেখা" to "text",
        "বন্ধু" to "friend",
        "ভাই" to "brother",
        "বোন" to "sister",
        "মা" to "mother",
        "বাবা" to "father",
        "সময়" to "time",
        "খাবার" to "food",
        "পানি" to "water",
        "ফোন" to "phone",
        "কীবোর্ড" to "keyboard",
        "জাদুকাঠি" to "magic wand",
        "শব্দ" to "sound",
        "রং" to "color",
        "দিন" to "day",
        "রাত" to "night",
        "সকাল" to "morning",
        "সন্ধ্যা" to "evening"
    )

    private val enToBnWords = mapOf(
        "i" to "আমি",
        "me" to "আমাকে",
        "my" to "আমার",
        "we" to "আমরা",
        "our" to "আমাদের",
        "you" to "আপনি/তুমি",
        "your" to "আপনার/তোমার",
        "he" to "সে",
        "she" to "সে",
        "they" to "তারা",
        "them" to "তাদের",
        "this" to "এটি",
        "that" to "ওটি",
        "what" to "কি",
        "why" to "কেন",
        "where" to "কোথায়",
        "when" to "কখন",
        "how" to "কিভাবে",
        "who" to "কে",
        "yes" to "হ্যাঁ",
        "no" to "না",
        "good" to "ভালো",
        "fine" to "ভালো",
        "bad" to "খারাপ",
        "great" to "দারুণ",
        "amazing" to "অসাধারণ",
        "beautiful" to "সুন্দর",
        "today" to "আজ",
        "tomorrow" to "আগামীকাল",
        "yesterday" to "গতকাল",
        "now" to "এখন",
        "later" to "পরে",
        "home" to "বাসা",
        "office" to "অফিস",
        "work" to "কাজ",
        "working" to "কাজ করছে",
        "message" to "মেসেজ",
        "friend" to "বন্ধু",
        "brother" to "ভাই",
        "sister" to "বোন",
        "help" to "সাহায্য",
        "food" to "খাবার",
        "water" to "পানি",
        "keyboard" to "কীবোর্ড",
        "magic" to "জাদু",
        "wand" to "কাঠি",
        "coming" to "আসছি",
        "going" to "যাচ্ছি",
        "free" to "মুক্ত/ফ্রি",
        "busy" to "ব্যস্ত",
        "please" to "দয়া করে",
        "thanks" to "ধন্যবাদ"
    )

    private fun translateBengaliToEnglish(bengaliText: String): String {
        val normalized = bengaliText.trim()

        // 1. Exact phrase check
        bnToEnPhrases[normalized]?.let { return it }

        // Strip ending punctuation for lookup
        val cleanNoPunct = normalized.trimEnd('.', '?', '!', '।', ',')
        val punct = normalized.substring(cleanNoPunct.length)
        bnToEnPhrases[cleanNoPunct]?.let {
            val endSymbol = if (punct == "।") "." else punct
            return it + endSymbol
        }

        // 2. Multi-word phrase search inside sentences
        var result = normalized
        for ((bnPhrase, enPhrase) in bnToEnPhrases) {
            if (result.contains(bnPhrase)) {
                result = result.replace(bnPhrase, enPhrase)
            }
        }
        if (result != normalized && !isBengali(result)) {
            return result
        }

        // 3. Word-by-word tokenized translation
        val tokens = normalized.split(Regex("(?<=[\\s।,!?])|(?=[\\s।,!?])"))
        val translatedTokens = tokens.map { token ->
            val cleanToken = token.trim()
            if (cleanToken.isEmpty() || token.matches(Regex("[\\s।,!?]+"))) {
                if (token == "।") "." else token
            } else {
                bnToEnWords[cleanToken]
                    ?: stripBengaliSuffix(cleanToken)?.let { (root, suffixEng) ->
                        (bnToEnWords[root] ?: root) + suffixEng
                    }
                    ?: cleanToken
            }
        }

        return translatedTokens.joinToString("").replace("  ", " ").trim()
    }

    private fun stripBengaliSuffix(word: String): Pair<String, String>? {
        val suffixes = listOf(
            "গুলো" to "s",
            "রা" to "s",
            "দের" to "'s",
            "র" to "'s",
            "এর" to "'s",
            "তে" to " in",
            "এ" to " in",
            "কে" to " to"
        )
        for ((suf, eng) in suffixes) {
            if (word.endsWith(suf) && word.length > suf.length + 1) {
                val root = word.substring(0, word.length - suf.length)
                return Pair(root, eng)
            }
        }
        return null
    }

    private fun translateEnglishToBengali(englishText: String): String {
        val normalized = englishText.trim()
        val lower = normalized.lowercase()

        // 1. Exact phrase match
        enToBnPhrases[lower]?.let { return it }

        // Strip ending punctuation
        val cleanNoPunct = lower.trimEnd('.', '?', '!', ',')
        val punct = normalized.substring(cleanNoPunct.length)
        enToBnPhrases[cleanNoPunct]?.let {
            val endSymbol = if (punct == ".") "।" else punct
            return it + endSymbol
        }

        // 2. Phrase replacement inside sentence
        var result = lower
        for ((enPhrase, bnPhrase) in enToBnPhrases) {
            if (result.contains(enPhrase)) {
                result = result.replace(enPhrase, bnPhrase)
            }
        }
        if (result != lower && isBengali(result)) {
            return result
        }

        // 3. Word-by-word tokenized translation
        val tokens = normalized.split(Regex("(?<=[\\s.,!?])|(?=[\\s.,!?])"))
        val translatedTokens = tokens.map { token ->
            val cleanToken = token.trim().lowercase()
            if (cleanToken.isEmpty() || token.matches(Regex("[\\s.,!?]+"))) {
                if (token == ".") "।" else token
            } else {
                enToBnWords[cleanToken] ?: token
            }
        }

        return translatedTokens.joinToString("").replace("  ", " ").trim()
    }
}
