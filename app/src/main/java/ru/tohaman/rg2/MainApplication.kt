package ru.tohaman.rg2

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidFileProperties
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import ru.tohaman.rg2.dataSource.Domain
import ru.tohaman.rg2.koin.appModule
import ru.tohaman.rg2.koin.viewModelsModule
import timber.log.Timber

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        //Если приложение в дебаг версии, то выводим логи, если release, то не выводим
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
        Domain.integrateWith(this)

        startKoin {
            // use AndroidLogger as Koin Logger - default Level.INFO
            androidLogger()

            // use the Android context given there
            androidContext(this@MainApplication)

            // load properties from assets/koin.properties file
            androidFileProperties()

            // module list
            modules(appModule, viewModelsModule)
        }
        // а если надо стартовать Koin  не при загрузке, то надо использовать
        //loadKoinModules(myModule)
    }
}