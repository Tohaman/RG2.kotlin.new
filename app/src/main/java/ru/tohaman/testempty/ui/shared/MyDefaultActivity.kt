package ru.tohaman.testempty.ui.shared

import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import org.koin.android.ext.android.inject
import ru.tohaman.testempty.Constants.IS_SCREEN_ALWAYS_ON
import ru.tohaman.testempty.utils.getThemeFromSharedPreference

/**
 * Created by Toha on 07.01.2020.
 * Задаем дефолтные параметры для всех активностей в программе
 */

abstract class MyDefaultActivity : AppCompatActivity() {
    private val sp: SharedPreferences by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(getThemeFromSharedPreference(sp))
        //Включаем поддержку векторной графики на устройствах ниже Лилипопа (5.0)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

        //Настраиваем отключение экрана
        val isScreenAlwaysOn = sp.getBoolean(IS_SCREEN_ALWAYS_ON, false)
        if (isScreenAlwaysOn) {
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