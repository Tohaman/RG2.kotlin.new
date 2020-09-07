package ru.tohaman.rg2.ui.in_app_review

import ru.tohaman.rg2.AppSettings
import timber.log.Timber
import java.util.*

class InAppReviewRepository {

    companion object {
        private const val DAY_ENTER_COUNT_THRESHOLD = 3
        private const val LAST_CALL_DATE_DAYS = 5
    }

    //Нужно ли отображать IAR
    fun shouldShowInAppReview(): Boolean {
        //Timber.d("$TAG AppSettings.dayEnterCount = ${AppSettings.dayEnterCount}")
        val lastCallReviewTimestamp = AppSettings.lastCallReviewTimestamp
        val lastCallCalendar = Calendar.getInstance()
        lastCallCalendar.timeInMillis = lastCallReviewTimestamp
        lastCallCalendar.add(Calendar.DATE, LAST_CALL_DATE_DAYS)

        val now = Calendar.getInstance()

        val currentDayEnterCount = AppSettings.dayEnterCount

        //Если за сегодня это уже более чем [DAY_ENTER_COUNT_THRESHOLD] вход и
        //с момента последнего вызова прошло более [LAST_CALL_DATE_DAYS] дней, то true else false
        return (currentDayEnterCount >= DAY_ENTER_COUNT_THRESHOLD) and
                (now.time.time > lastCallCalendar.time.time)
    }

    fun resetShowInAppReviewCondition() {
        Timber.d("IAR ResetShowInAppReviewCondition")
        AppSettings.lastCallReviewTimestamp = Calendar.getInstance().time.time
    }

}