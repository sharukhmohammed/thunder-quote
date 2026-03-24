package com.sharukh.thunderquote.repo

import com.sharukh.thunderquote.di.ServiceLocator
import com.sharukh.thunderquote.model.Quote
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class StepRepo {

    private val prefs = ServiceLocator.preferences()
    private val db = ServiceLocator.appDatabase()
    private val dao = db.quoteDao()

    companion object {
        private const val KEY_BASELINE_VALUE = "steps_baseline_value"
        private const val KEY_BASELINE_DATE = "steps_baseline_date"
        private const val KEY_MILESTONE_QUOTE_PREFIX = "steps_milestone_quote_"
        private val DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd", Locale.US)

        val MILESTONE_STEPS = listOf(500, 1_000, 2_500, 5_000, 10_000)
    }

    private fun todayKey(): String = DATE_FORMAT.format(Date())

    fun getBaseline(): Float {
        val savedDate = prefs.getString(KEY_BASELINE_DATE, null)
        return if (savedDate == todayKey()) {
            prefs.getFloat(KEY_BASELINE_VALUE, -1f)
        } else {
            -1f
        }
    }

    fun saveBaseline(sensorValue: Float) {
        prefs.edit()
            .putFloat(KEY_BASELINE_VALUE, sensorValue)
            .putString(KEY_BASELINE_DATE, todayKey())
            .apply()
    }

    fun getMilestoneQuoteId(milestoneIndex: Int): Int {
        return prefs.getInt("$KEY_MILESTONE_QUOTE_PREFIX${milestoneIndex}_${todayKey()}", -1)
    }

    fun saveMilestoneQuoteId(milestoneIndex: Int, quoteId: Int) {
        prefs.edit()
            .putInt("$KEY_MILESTONE_QUOTE_PREFIX${milestoneIndex}_${todayKey()}", quoteId)
            .apply()
    }

    suspend fun getRandomQuote(): Quote? {
        val id = dao.getRandomId() ?: return null
        var result: Quote? = null
        dao.getQuote(id).collect { result = it }
        return result
    }
}
