package ru.tohaman.rg2.koin

import android.content.Context
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidFileProperties
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import ru.tohaman.rg2.ui.in_app_review.inAppReviewModule

class KoinStarter {

    fun start(context: Context) {
        startKoin {
            // use AndroidLogger as Koin Logger - default Level.INFO
            androidLogger()

            // use the Android context given there
            androidContext(context)

            // load properties from assets/koin.properties file
            androidFileProperties()
            modules(myModules())
        }
    }

    private fun myModules(): List<Module> {
        return listOf(
            inAppReviewModule,
            appModule,
            viewModelsModule
        )
    }
}