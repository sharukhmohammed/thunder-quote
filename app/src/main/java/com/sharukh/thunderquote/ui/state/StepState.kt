package com.sharukh.thunderquote.ui.state

import com.sharukh.thunderquote.model.Quote

data class StepMilestone(
    val steps: Int,
    val label: String,
    val quote: Quote? = null,
    val isUnlocked: Boolean = false,
)

data class StepCounterState(
    val dailySteps: Int = 0,
    val milestones: List<StepMilestone> = emptyList(),
    val hasPermission: Boolean = false,
    val hasSensor: Boolean = true,
    val nextMilestoneSteps: Int = 0,
    val nextMilestoneProgress: Float = 0f,
)
