package ru.tohaman.testempty

import android.app.Application
import ru.tohaman.testempty.dataSource.Domain
import timber.log.Timber

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        //Если приложение в дебаг версии, то выводим логи, если release, то не выводим
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
        Domain.integrateWith(this)
    }
}