package com.sharukh.thunderquote.ui.steps

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sharukh.thunderquote.repo.StepRepo
import com.sharukh.thunderquote.ui.state.StepCounterState
import com.sharukh.thunderquote.ui.state.StepMilestone
import com.sharukh.thunderquote.ui.theme.Size

@Composable
fun StepCounterScreen(innerPadding: PaddingValues, state: StepCounterState, onPermissionResult: (Boolean) -> Unit) {
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> onPermissionResult(granted) }

    LaunchedEffect(state.hasPermission) {
        if (!state.hasPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            permissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        contentPadding = PaddingValues(horizontal = Size.dp16, vertical = Size.dp16),
        verticalArrangement = Arrangement.spacedBy(Size.dp16)
    ) {
        item {
            StepHeroSection(state)
        }

        item {
            if (state.hasPermission && state.hasSensor) {
                NextMilestoneProgress(state)
            }
        }

        item {
            Text(
                text = "Milestones",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = Size.dp4)
            )
        }

        if (!state.hasSensor) {
            item {
                NoSensorCard()
            }
        } else if (!state.hasPermission) {
            item {
                PermissionRequiredCard {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        permissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
                    }
                }
            }
        } else {
            items(state.milestones) { milestone ->
                MilestoneCard(milestone = milestone, currentSteps = state.dailySteps)
            }
        }
    }
}

@Composable
private fun StepHeroSection(state: StepCounterState) {
    val unlockedCount = state.milestones.count { it.isUnlocked }
    val totalMilestones = StepRepo.MILESTONE_STEPS.size

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(contentAlignment = Alignment.Center) {
            val progress by animateFloatAsState(
                targetValue = if (totalMilestones > 0) unlockedCount.toFloat() / totalMilestones else 0f,
                animationSpec = spring(stiffness = Spring.StiffnessLow),
                label = "milestone_progress"
            )
            CircularProgressIndicator(
                progress = { progress },
                modifier = Modifier.size(160.dp),
                strokeWidth = 10.dp,
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = state.dailySteps.toString(),
                    fontSize = 40.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = "steps today",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Spacer(Modifier.height(Size.dp8))
        Text(
            text = "$unlockedCount / $totalMilestones milestones unlocked",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun NextMilestoneProgress(state: StepCounterState) {
    val allDone = state.milestones.all { it.isUnlocked }
    if (allDone) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Size.dp16),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "All milestones unlocked! You're a walking legend!",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
        return
    }

    val animated by animateFloatAsState(
        targetValue = state.nextMilestoneProgress,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "next_milestone_progress"
    )

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(Size.dp16)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Next milestone",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = "${state.dailySteps} / ${state.nextMilestoneSteps} steps",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            Spacer(Modifier.height(Size.dp8))
            LinearProgressIndicator(
                progress = { animated },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun MilestoneCard(milestone: StepMilestone, currentSteps: Int) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(milestone.isUnlocked) { visible = milestone.isUnlocked }

    val isReachable = currentSteps > 0 || milestone.isUnlocked

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when {
                milestone.isUnlocked -> MaterialTheme.colorScheme.primaryContainer
                else -> MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Column(modifier = Modifier.padding(Size.dp16)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            if (milestone.isUnlocked)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = if (milestone.isUnlocked) Icons.Default.Star else Icons.Default.Lock,
                        contentDescription = null,
                        tint = if (milestone.isUnlocked)
                            MaterialTheme.colorScheme.onPrimary
                        else
                            MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(Modifier.size(Size.dp12))
                Column {
                    Text(
                        text = "${milestone.label} steps",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (milestone.isUnlocked)
                            MaterialTheme.colorScheme.onPrimaryContainer
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = if (milestone.isUnlocked) "Unlocked!" else "Walk to unlock",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (milestone.isUnlocked)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.outline,
                    )
                }
            }

            AnimatedVisibility(
                visible = milestone.isUnlocked && milestone.quote != null,
                enter = fadeIn() + expandVertically()
            ) {
                milestone.quote?.let { quote ->
                    Column {
                        Spacer(Modifier.height(Size.dp12))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                                    shape = MaterialTheme.shapes.small
                                )
                                .padding(Size.dp12)
                        ) {
                            Column {
                                Text(
                                    text = "\u201C${quote.quote}\u201D",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontWeight = FontWeight.Medium,
                                )
                                Spacer(Modifier.height(Size.dp4))
                                Text(
                                    text = "— ${quote.author}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.align(Alignment.End)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NoSensorCard() {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(Size.dp16),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "No step counter sensor found on this device.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun PermissionRequiredCard(onRequest: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(Size.dp16),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Size.dp12)
        ) {
            Text(
                text = "Activity recognition permission is required to count your steps.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
            )
            Button(onClick = onRequest) {
                Text("Grant Permission")
            }
        }
    }
}
