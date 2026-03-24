package com.sharukh.thunderquote.ai

import android.content.Context
import com.google.ai.edge.aicore.AvailabilityStatus
import com.google.ai.edge.aicore.GenerativeModel
import com.google.ai.edge.aicore.content
import com.google.ai.edge.aicore.generationConfig
import com.sharukh.thunderquote.model.Quote
import com.sharukh.thunderquote.ui.state.AiAvailability
import com.sharukh.thunderquote.ui.state.AiMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Wraps Android's AI Core (Gemini Nano on-device) for text generation.
 * Supported on Pixel 8 Pro and later, Samsung Galaxy S24 and later.
 */
class AiCoreRepository(private val context: Context) {

    private val model: GenerativeModel by lazy {
        GenerativeModel(
            generationConfig = generationConfig {
                this.context = this@AiCoreRepository.context
                temperature = 0.8f
                topK = 16
                maxOutputTokens = 512
            }
        )
    }

    suspend fun checkAvailability(): AiAvailability {
        return try {
            when (model.checkAvailability()) {
                AvailabilityStatus.AVAILABLE -> AiAvailability.Available
                AvailabilityStatus.DOWNLOADABLE -> AiAvailability.Downloading()
                else -> AiAvailability.Unavailable
            }
        } catch (e: Exception) {
            AiAvailability.Unavailable
        }
    }

    /**
     * Triggers model download / preparation and suspends until ready.
     * Call when [checkAvailability] returns [AiAvailability.Downloading].
     */
    suspend fun prepareModel() {
        model.prepareInferenceEngine()
    }

    /**
     * Streams a new original quote about [topic].
     */
    fun generateQuoteStream(topic: String): Flow<String> {
        val prompt = "Write a single original, inspiring quote about \"$topic\". " +
            "Format: \"[the quote]\" — [Author Name]. " +
            "Make it profound and memorable. Output only the formatted quote, nothing else."
        return model.generateContentStream(content { text(prompt) }).map { it.text ?: "" }
    }

    /**
     * Streams a chat reply given the full [history] and a new [userMessage].
     * History is formatted as a plain-text dialogue to work reliably with small on-device models.
     */
    fun chatStream(userMessage: String, history: List<AiMessage>): Flow<String> {
        val recentHistory = history.takeLast(10) // keep context window manageable
        val prompt = buildString {
            append("You are a thoughtful assistant who loves quotes, wisdom, and inspiration. ")
            if (recentHistory.isNotEmpty()) {
                append("Continue this conversation:\n\n")
                recentHistory.forEach { msg ->
                    if (msg.isUser) append("User: ${msg.content}\n")
                    else append("Assistant: ${msg.content}\n")
                }
                append("\nUser: $userMessage\nAssistant:")
            } else {
                append("Answer this: $userMessage")
            }
        }
        return model.generateContentStream(content { text(prompt) }).map { it.text ?: "" }
    }

    /**
     * Returns themed recommendations based on the user's favourite [quotes].
     * Falls back to general recommendations when no favourites exist.
     */
    suspend fun getRecommendations(quotes: List<Quote>): String {
        val prompt = if (quotes.isEmpty()) {
            "Suggest 3 inspiring quote themes for someone who loves wisdom and motivation. " +
                "For each theme output exactly:\n" +
                "Theme: [name]\nDescription: [one sentence]\nQuote: \"[sample quote]\" — [Author]\n"
        } else {
            val sample = quotes.take(10).joinToString("\n") { "\"${it.quote}\" — ${it.author}" }
            "Based on these quotes I love:\n$sample\n\n" +
                "Suggest 3 similar themes I might enjoy. For each theme output exactly:\n" +
                "Theme: [name]\nDescription: [one sentence]\nQuote: \"[sample quote]\" — [Author]\n"
        }
        return model.generateContent(content { text(prompt) }).text ?: ""
    }
}
