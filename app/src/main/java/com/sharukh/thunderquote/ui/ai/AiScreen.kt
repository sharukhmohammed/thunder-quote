@file:OptIn(ExperimentalMaterial3Api::class)

package com.sharukh.thunderquote.ui.ai

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sharukh.thunderquote.R
import com.sharukh.thunderquote.ui.state.AiAvailability
import com.sharukh.thunderquote.ui.state.AiMessage
import com.sharukh.thunderquote.ui.state.AiRecommendation
import com.sharukh.thunderquote.ui.state.AiTab
import com.sharukh.thunderquote.ui.state.AiUiState
import com.sharukh.thunderquote.ui.theme.Size

@Composable
fun AiScreen(innerPadding: PaddingValues, viewModel: AiViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
    ) {
        when (state.availability) {
            AiAvailability.Checking -> AiLoadingContent(message = stringResource(R.string.ai_checking))
            AiAvailability.Unavailable -> AiUnavailableContent()
            is AiAvailability.Downloading -> AiLoadingContent(message = stringResource(R.string.ai_downloading))
            AiAvailability.Available -> AiMainContent(state = state, viewModel = viewModel)
        }
    }
}

// ── Availability states ───────────────────────────────────────────────────────

@Composable
private fun AiLoadingContent(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(Size.dp16))
            Text(text = message, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun AiUnavailableContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(Size.screenPadding)
        ) {
            Icon(
                imageVector = Icons.Rounded.AutoAwesome,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.outline
            )
            Spacer(modifier = Modifier.height(Size.dp16))
            Text(
                text = stringResource(R.string.ai_unavailable_title),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(Size.dp8))
            Text(
                text = stringResource(R.string.ai_unavailable_body),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ── Main content with tab row ─────────────────────────────────────────────────

@Composable
private fun AiMainContent(state: AiUiState, viewModel: AiViewModel) {
    val tabs = listOf(
        stringResource(R.string.ai_tab_generate),
        stringResource(R.string.ai_tab_chat),
        stringResource(R.string.ai_tab_for_you),
    )
    val selectedIndex = state.activeTab.ordinal

    SecondaryTabRow(selectedTabIndex = selectedIndex) {
        tabs.forEachIndexed { index, title ->
            Tab(
                selected = selectedIndex == index,
                onClick = { viewModel.onTabChange(AiTab.entries[index]) },
                text = { Text(title) }
            )
        }
    }

    when (state.activeTab) {
        AiTab.Generate -> GenerateTab(state = state, viewModel = viewModel)
        AiTab.Chat -> ChatTab(state = state, viewModel = viewModel)
        AiTab.ForYou -> ForYouTab(state = state, viewModel = viewModel)
    }
}

// ── Generate tab ──────────────────────────────────────────────────────────────

@Composable
private fun GenerateTab(state: AiUiState, viewModel: AiViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Size.screenPadding),
        verticalArrangement = Arrangement.spacedBy(Size.dp16)
    ) {
        Spacer(modifier = Modifier.height(Size.dp8))

        OutlinedTextField(
            value = state.topicInput,
            onValueChange = viewModel::onTopicInputChange,
            label = { Text(stringResource(R.string.ai_generate_hint)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { viewModel.generateQuote() }),
        )

        Button(
            onClick = viewModel::generateQuote,
            enabled = state.topicInput.isNotBlank() && !state.isGenerating,
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (state.isGenerating) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            } else {
                Icon(Icons.Rounded.AutoAwesome, contentDescription = null)
            }
            Spacer(modifier = Modifier.size(Size.dp8))
            Text(stringResource(R.string.ai_generate_button))
        }

        AnimatedVisibility(visible = state.generatedQuote.isNotEmpty()) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(Size.dp16)) {
                    Text(
                        text = state.generatedQuote,
                        style = MaterialTheme.typography.titleMedium,
                        fontStyle = if (state.isGenerating) FontStyle.Italic else FontStyle.Normal,
                    )
                }
            }
        }
    }
}

// ── Chat tab ──────────────────────────────────────────────────────────────────

@Composable
private fun ChatTab(state: AiUiState, viewModel: AiViewModel) {
    val listState = rememberLazyListState()

    LaunchedEffect(state.chatMessages.size) {
        if (state.chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(state.chatMessages.lastIndex)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(Size.dp16),
            verticalArrangement = Arrangement.spacedBy(Size.dp8),
        ) {
            if (state.chatMessages.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillParentMaxSize()
                            .padding(Size.screenPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.ai_chat_empty),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            } else {
                items(state.chatMessages) { msg ->
                    ChatBubble(message = msg)
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = Size.dp16, vertical = Size.dp8),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Size.dp8),
        ) {
            OutlinedTextField(
                value = state.chatInput,
                onValueChange = viewModel::onChatInputChange,
                placeholder = { Text(stringResource(R.string.ai_chat_hint)) },
                modifier = Modifier.weight(1f),
                maxLines = 4,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = { viewModel.sendChatMessage() }),
            )
            FilledIconButton(
                onClick = viewModel::sendChatMessage,
                enabled = state.chatInput.isNotBlank() && !state.isChatting,
            ) {
                Icon(Icons.AutoMirrored.Rounded.Send, contentDescription = stringResource(R.string.ai_chat_send))
            }
        }
    }
}

@Composable
private fun ChatBubble(message: AiMessage) {
    val bubbleColor = if (message.isUser)
        MaterialTheme.colorScheme.primaryContainer
    else
        MaterialTheme.colorScheme.surfaceVariant

    val textColor = if (message.isUser)
        MaterialTheme.colorScheme.onPrimaryContainer
    else
        MaterialTheme.colorScheme.onSurfaceVariant

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start,
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .background(color = bubbleColor, shape = RoundedCornerShape(12.dp))
                .padding(horizontal = Size.dp16, vertical = Size.dp8)
        ) {
            if (message.isStreaming && message.content.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
            } else {
                Text(
                    text = message.content,
                    color = textColor,
                    style = MaterialTheme.typography.bodyMedium,
                    fontStyle = if (message.isStreaming) FontStyle.Italic else FontStyle.Normal,
                )
            }
        }
    }
}

// ── For You tab ───────────────────────────────────────────────────────────────

@Composable
private fun ForYouTab(state: AiUiState, viewModel: AiViewModel) {
    Box(modifier = Modifier.fillMaxSize()) {
        when {
            state.isLoadingRecommendations -> {
                AiLoadingContent(message = stringResource(R.string.ai_for_you_loading))
            }

            state.recommendError.isNotEmpty() -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(Size.screenPadding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(text = state.recommendError, style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(Size.dp16))
                    Button(onClick = viewModel::loadRecommendations) {
                        Icon(Icons.Rounded.Refresh, contentDescription = null)
                        Spacer(modifier = Modifier.size(Size.dp8))
                        Text(stringResource(R.string.ai_for_you_retry))
                    }
                }
            }

            state.recommendations.isEmpty() -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = stringResource(R.string.ai_for_you_empty),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(Size.dp16),
                    verticalArrangement = Arrangement.spacedBy(Size.dp16),
                ) {
                    items(state.recommendations) { rec ->
                        RecommendationCard(rec)
                    }
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center,
                        ) {
                            IconButton(onClick = viewModel::loadRecommendations) {
                                Icon(Icons.Rounded.Refresh, contentDescription = stringResource(R.string.ai_for_you_retry))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RecommendationCard(recommendation: AiRecommendation) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(Size.dp16)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Rounded.AutoAwesome,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.size(Size.dp8))
                Text(
                    text = recommendation.theme,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            if (recommendation.description.isNotEmpty()) {
                Spacer(modifier = Modifier.height(Size.dp8))
                Text(
                    text = recommendation.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            if (recommendation.sampleQuote.isNotEmpty()) {
                Spacer(modifier = Modifier.height(Size.dp16))
                Text(
                    text = recommendation.sampleQuote,
                    style = MaterialTheme.typography.bodyMedium,
                    fontStyle = FontStyle.Italic,
                )
            }
        }
    }
}
