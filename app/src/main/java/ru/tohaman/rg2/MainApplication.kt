package ru.tohaman.rg2

import androidx.multidex.MultiDexApplication
import ru.tohaman.rg2.dataSource.Domain

class MainApplication : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        Domain.integrateWith(this)
    }
}