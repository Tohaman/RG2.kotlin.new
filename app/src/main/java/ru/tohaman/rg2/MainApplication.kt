package ru.tohaman.rg2

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidFileProperties
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import ru.tohaman.rg2.dataSource.Domain
import ru.tohaman.rg2.koin.KoinStarter
import ru.tohaman.rg2.koin.appModule
import ru.tohaman.rg2.koin.viewModelsModule
import timber.log.Timber

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Domain.integrateWith(this)
    }
}