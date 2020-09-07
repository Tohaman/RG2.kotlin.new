package ru.tohaman.rg2.ui.in_app_review

import android.app.Activity
import com.google.android.play.core.ktx.launchReview
import com.google.android.play.core.ktx.requestReview
import com.google.android.play.core.review.ReviewManager
import timber.log.Timber

class RequestInAppReviewUseCase(
    private val reviewManager: ReviewManager,
    private val shouldShowInAppReviewUseCase: ShouldShowInAppReviewUseCase
) {
    suspend fun launchReview(activity: Activity) {
        Timber.d("IAR .TryLaunchReview activity = [${activity}]")
        if (shouldShowInAppReview()) {
            Timber.d("IAR launchReviewFlow")
            launchReviewFlow(activity)
        }
    }

    private fun shouldShowInAppReview(): Boolean {
        return shouldShowInAppReviewUseCase
            .shouldShowInAppReview()
            .also {
                Timber.d("IAR shouldShowInAppReview: $it")
            }
    }

    private fun resetShowInAppReviewCondition() {
        return shouldShowInAppReviewUseCase
            .resetShouldShowInAppReviewCondition()
            .also {
                Timber.d("IAR resetShowInAppReviewCondition: resetting in-app review condition")
            }
    }

    private suspend fun launchReviewFlow(activity: Activity) {
        try {
            reviewManager.run {
                launchReview(activity, requestReview())
                resetShowInAppReviewCondition()
            }
        } catch (e: Exception) {
            Timber.e(e, "unable to launch review")
        }
    }
}