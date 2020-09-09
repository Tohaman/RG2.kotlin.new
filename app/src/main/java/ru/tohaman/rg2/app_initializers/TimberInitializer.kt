package ru.tohaman.rg2.app_initializers

import android.content.Context
import androidx.startup.Initializer
import ru.tohaman.rg2.BuildConfig
import timber.log.Timber
import timber.log.Timber.DebugTree

class TimberInitializer: Initializer<Unit> {
    override fun create(context: Context) {
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}