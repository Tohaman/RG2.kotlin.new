package ru.tohaman.rg2.ui.shared

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import ru.tohaman.rg2.AppSettings
import ru.tohaman.rg2.utils.getThemeFromSharedPreference

/**
 * Created by Toha on 07.01.2020.
 * Задаем дефолтные параметры для всех активностей в программе
 */

abstract class MyDefaultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(getThemeFromSharedPreference())
        //Включаем поддержку векторной графики на устройствах ниже Лилипопа (5.0)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

        //Настраиваем отключение экрана
        if (AppSettings.isScreenAlwaysON) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }

        super.onCreate(savedInstanceState)
    }

    override fun applyOverrideConfiguration(overrideConfiguration: Configuration?) {
        if (Build.VERSION.SDK_INT in 21..25) {
            return
        }
        super.applyOverrideConfiguration(overrideConfiguration)
    }
}