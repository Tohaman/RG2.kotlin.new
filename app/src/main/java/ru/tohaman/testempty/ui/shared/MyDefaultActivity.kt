package ru.tohaman.testempty.ui.shared

import android.os.Bundle
import androidx.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

/**
 * Created by Toha on 07.01.2020.
 * Задаем дефолтные параметры для всех активностей в программе
 */

abstract class MyDefaultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
//        setTheme(getThemeFromSharedPreference(this))
        //Включаем поддержку векторной графики на устройствах ниже Лилипопа (5.0)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        //Настраиваем отключение экрана
//        val isScreenAlwaysOn = sp.getBoolean(IS_SCREEN_ALWAYS_ON, false)
//        if (isScreenAlwaysOn) {
//            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
//        } else {
//            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
//        }

        super.onCreate(savedInstanceState)
    }
}