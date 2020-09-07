package ru.tohaman.rg2.ui.in_app_review

import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.review.testing.FakeReviewManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import ru.tohaman.rg2.BuildConfig
import timber.log.Timber

val inAppReviewModule = module {

    factory {
        Timber.d("IAR .factory ReviewManagerFactory.create")
        if (BuildConfig.DEBUG) {
            FakeReviewManager(androidContext())
        } else {
            ReviewManagerFactory.create(androidContext())
        }
    }

    factory {
        RequestInAppReviewUseCase(get(), get())
    }

    factory {
        ShouldShowInAppReviewUseCase(get())
    }

    factory {
        InAppReviewRepository()
    }
}