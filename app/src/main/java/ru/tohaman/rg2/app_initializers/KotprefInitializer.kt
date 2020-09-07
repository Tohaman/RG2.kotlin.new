package ru.tohaman.rg2.app_initializers

import android.content.Context
import androidx.startup.Initializer
import com.chibatching.kotpref.Kotpref

class KotprefInitializer: Initializer<Unit> {
    override fun create(context: Context) {
        Kotpref.init(context)
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}