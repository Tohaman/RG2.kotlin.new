package ru.tohaman.rg2.app_initializers

import android.content.Context
import androidx.startup.Initializer
import ru.tohaman.rg2.koin.KoinStarter

class KoinInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        KoinStarter().start(context)
        return
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}