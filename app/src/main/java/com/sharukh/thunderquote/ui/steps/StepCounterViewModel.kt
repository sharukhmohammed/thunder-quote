package com.sharukh.thunderquote.ui.steps

import android.Manifest
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sharukh.thunderquote.app.App
import com.sharukh.thunderquote.repo.StepRepo
import com.sharukh.thunderquote.ui.state.StepCounterState
import com.sharukh.thunderquote.ui.state.StepMilestone
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class StepCounterViewModel : ViewModel(), SensorEventListener {

    private val repo = StepRepo()
    private val context = App.context
    private val sensorManager = context.getSystemService(SensorManager::class.java)
    private val stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

    private val _state = MutableStateFlow(StepCounterState())
    val state = _state.asStateFlow()

    init {
        val hasSensor = stepSensor != null
        val hasPermission = checkPermission()
        _state.update { it.copy(hasSensor = hasSensor, hasPermission = hasPermission) }
        buildInitialMilestones()
        if (hasPermission && hasSensor) {
            registerSensor()
        }
    }

    private fun checkPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) return true
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACTIVITY_RECOGNITION
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun onPermissionResult(granted: Boolean) {
        _state.update { it.copy(hasPermission = granted) }
        if (granted && stepSensor != null) {
            registerSensor()
        }
    }

    private fun registerSensor() {
        sensorManager?.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    private fun buildInitialMilestones() {
        val milestones = StepRepo.MILESTONE_STEPS.mapIndexed { index, steps ->
            val quoteId = repo.getMilestoneQuoteId(index)
            StepMilestone(
                steps = steps,
                label = formatSteps(steps),
                quote = null,
                isUnlocked = false,
            ).also { milestone ->
                if (quoteId != -1) {
                    viewModelScope.launch {
                        loadMilestoneQuote(index, quoteId)
                    }
                }
            }
        }
        _state.update { it.copy(milestones = milestones) }
        updateProgress(0)
    }

    private suspend fun loadMilestoneQuote(milestoneIndex: Int, quoteId: Int) {
        val quote = repo.getRandomQuote() ?: return
        _state.update { current ->
            val updated = current.milestones.toMutableList()
            if (milestoneIndex < updated.size) {
                updated[milestoneIndex] = updated[milestoneIndex].copy(
                    quote = quote,
                    isUnlocked = true
                )
            }
            current.copy(milestones = updated)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        val sensorSteps = event?.values?.get(0)?.toInt() ?: return

        var baseline = repo.getBaseline()
        if (baseline < 0f) {
            baseline = sensorSteps.toFloat()
            repo.saveBaseline(baseline)
        }

        val dailySteps = (sensorSteps - baseline.toInt()).coerceAtLeast(0)

        viewModelScope.launch {
            val currentMilestones = _state.value.milestones.toMutableList()
            StepRepo.MILESTONE_STEPS.forEachIndexed { index, milestoneSteps ->
                if (dailySteps >= milestoneSteps && !currentMilestones[index].isUnlocked) {
                    val existingId = repo.getMilestoneQuoteId(index)
                    val quote = repo.getRandomQuote()
                    if (quote != null) {
                        val quoteId = if (existingId != -1) existingId else quote.id
                        if (existingId == -1) repo.saveMilestoneQuoteId(index, quoteId)
                        currentMilestones[index] = currentMilestones[index].copy(
                            quote = quote,
                            isUnlocked = true
                        )
                    }
                }
            }
            _state.update { it.copy(dailySteps = dailySteps, milestones = currentMilestones) }
            updateProgress(dailySteps)
        }
    }

    private fun updateProgress(dailySteps: Int) {
        val nextMilestone = StepRepo.MILESTONE_STEPS.firstOrNull { it > dailySteps }
        val prevMilestone = StepRepo.MILESTONE_STEPS.lastOrNull { it <= dailySteps }

        if (nextMilestone == null) {
            _state.update { it.copy(nextMilestoneSteps = StepRepo.MILESTONE_STEPS.last(), nextMilestoneProgress = 1f) }
            return
        }

        val rangeStart = prevMilestone ?: 0
        val progress = if (nextMilestone > rangeStart) {
            (dailySteps - rangeStart).toFloat() / (nextMilestone - rangeStart)
        } else 0f

        _state.update {
            it.copy(
                nextMilestoneSteps = nextMilestone,
                nextMilestoneProgress = progress.coerceIn(0f, 1f)
            )
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit

    override fun onCleared() {
        super.onCleared()
        sensorManager?.unregisterListener(this)
    }

    private fun formatSteps(steps: Int): String = when {
        steps >= 1_000 -> "${steps / 1_000}K"
        else -> steps.toString()
    }
}
