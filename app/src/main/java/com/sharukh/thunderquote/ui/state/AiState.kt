package com.sharukh.thunderquote.ui.state

sealed class AiAvailability {
    data object Checking : AiAvailability()
    data object Available : AiAvailability()
    data class Downloading(val progress: Float = 0f) : AiAvailability()
    data object Unavailable : AiAvailability()
}

enum class AiTab { Generate, Chat, ForYou }

data class AiMessage(
    val content: String,
    val isUser: Boolean,
    val isStreaming: Boolean = false,
)

data class AiRecommendation(
    val theme: String,
    val description: String,
    val sampleQuote: String,
)

data class AiUiState(
    val availability: AiAvailability = AiAvailability.Checking,
    val activeTab: AiTab = AiTab.Generate,
    // Generate tab
    val topicInput: String = "",
    val generatedQuote: String = "",
    val isGenerating: Boolean = false,
    // Chat tab
    val chatMessages: List<AiMessage> = emptyList(),
    val chatInput: String = "",
    val isChatting: Boolean = false,
    // For You tab
    val recommendations: List<AiRecommendation> = emptyList(),
    val isLoadingRecommendations: Boolean = false,
    val recommendError: String = "",
)
