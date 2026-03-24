package com.sharukh.thunderquote.ui.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sharukh.thunderquote.ai.AiCoreRepository
import com.sharukh.thunderquote.app.App
import com.sharukh.thunderquote.repo.QuoteRepo
import com.sharukh.thunderquote.ui.state.AiAvailability
import com.sharukh.thunderquote.ui.state.AiMessage
import com.sharukh.thunderquote.ui.state.AiRecommendation
import com.sharukh.thunderquote.ui.state.AiTab
import com.sharukh.thunderquote.ui.state.AiUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AiViewModel : ViewModel() {

    private val aiRepo = AiCoreRepository(App.context)
    private val quoteRepo = QuoteRepo()

    private val _uiState = MutableStateFlow(AiUiState())
    val uiState = _uiState.asStateFlow()

    init {
        checkAvailability()
    }

    private fun checkAvailability() = viewModelScope.launch {
        val status = aiRepo.checkAvailability()
        _uiState.update { it.copy(availability = status) }

        // If the model needs to be downloaded, trigger it immediately
        if (status is AiAvailability.Downloading) {
            try {
                aiRepo.prepareModel()
                _uiState.update { it.copy(availability = AiAvailability.Available) }
            } catch (e: Exception) {
                _uiState.update { it.copy(availability = AiAvailability.Unavailable) }
            }
        }
    }

    // ── Generate tab ─────────────────────────────────────────────────────────

    fun onTopicInputChange(input: String) {
        _uiState.update { it.copy(topicInput = input) }
    }

    fun generateQuote() = viewModelScope.launch {
        val topic = _uiState.value.topicInput.trim()
        if (topic.isEmpty() || _uiState.value.isGenerating) return@launch

        _uiState.update { it.copy(isGenerating = true, generatedQuote = "") }
        try {
            aiRepo.generateQuoteStream(topic).collect { chunk ->
                _uiState.update { it.copy(generatedQuote = it.generatedQuote + chunk) }
            }
        } catch (e: Exception) {
            _uiState.update { it.copy(generatedQuote = "Could not generate quote. Please try again.") }
        }
        _uiState.update { it.copy(isGenerating = false) }
    }

    // ── Chat tab ──────────────────────────────────────────────────────────────

    fun onChatInputChange(input: String) {
        _uiState.update { it.copy(chatInput = input) }
    }

    fun sendChatMessage() = viewModelScope.launch {
        val message = _uiState.value.chatInput.trim()
        if (message.isEmpty() || _uiState.value.isChatting) return@launch

        val userMsg = AiMessage(content = message, isUser = true)
        val placeholder = AiMessage(content = "", isUser = false, isStreaming = true)

        _uiState.update { state ->
            state.copy(
                chatInput = "",
                isChatting = true,
                chatMessages = state.chatMessages + userMsg + placeholder,
            )
        }

        // History for context — exclude the streaming placeholder we just appended
        val history = _uiState.value.chatMessages.dropLast(1)
        var accumulated = ""
        try {
            aiRepo.chatStream(message, history).collect { chunk ->
                accumulated += chunk
                _uiState.update { state ->
                    val updated = state.chatMessages.dropLast(1) +
                        AiMessage(content = accumulated, isUser = false, isStreaming = true)
                    state.copy(chatMessages = updated)
                }
            }
        } catch (e: Exception) {
            accumulated = "Something went wrong. Please try again."
        }

        _uiState.update { state ->
            val finalMessages = state.chatMessages.dropLast(1) +
                AiMessage(content = accumulated, isUser = false, isStreaming = false)
            state.copy(chatMessages = finalMessages, isChatting = false)
        }
    }

    // ── For You tab ───────────────────────────────────────────────────────────

    fun onTabChange(tab: AiTab) {
        _uiState.update { it.copy(activeTab = tab) }
        if (tab == AiTab.ForYou && _uiState.value.recommendations.isEmpty()) {
            loadRecommendations()
        }
    }

    fun loadRecommendations() = viewModelScope.launch {
        if (_uiState.value.isLoadingRecommendations) return@launch
        _uiState.update {
            it.copy(isLoadingRecommendations = true, recommendations = emptyList(), recommendError = "")
        }
        try {
            val favorites = quoteRepo.getFavoritesList()
            val raw = aiRepo.getRecommendations(favorites)
            _uiState.update {
                it.copy(
                    isLoadingRecommendations = false,
                    recommendations = parseRecommendations(raw),
                )
            }
        } catch (e: Exception) {
            _uiState.update {
                it.copy(
                    isLoadingRecommendations = false,
                    recommendError = "Could not load recommendations. Please try again.",
                )
            }
        }
    }

    private fun parseRecommendations(raw: String): List<AiRecommendation> {
        // Split on blank lines to get per-theme blocks; each block has Theme/Description/Quote lines
        return raw.split("\n\n").filter { it.isNotBlank() }.mapNotNull { block ->
            val lines = block.trim().lines()
            val theme = lines.firstOrNull { it.startsWith("Theme:") }
                ?.removePrefix("Theme:")?.trim() ?: return@mapNotNull null
            val desc = lines.firstOrNull { it.startsWith("Description:") }
                ?.removePrefix("Description:")?.trim() ?: ""
            val quote = lines.firstOrNull { it.startsWith("Quote:") }
                ?.removePrefix("Quote:")?.trim() ?: ""
            AiRecommendation(theme = theme, description = desc, sampleQuote = quote)
        }
    }
}
