package ru.tohaman.testempty.ui

import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.navigation.ui.setupWithNavController
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.tohaman.testempty.Constants.THEME
import ru.tohaman.testempty.DebugTag.TAG
import ru.tohaman.testempty.R
import ru.tohaman.testempty.databinding.ActivityMainBinding
import ru.tohaman.testempty.ui.shared.MyDefaultActivity
import ru.tohaman.testempty.ui.shared.UiUtilViewModel
import timber.log.Timber


class MainActivity : MyDefaultActivity(), SharedPreferences.OnSharedPreferenceChangeListener {

    private val uiUtilViewModel by viewModel<UiUtilViewModel>()
    private lateinit var binding: ActivityMainBinding
    private val sharedPreferences: SharedPreferences by inject()

    private val navController by lazy {
        Navigation.findNavController(this, R.id.mainFragment)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        DisplayMetrics().apply {
            windowManager.defaultDisplay.getMetrics(this)
            Timber.d( "$TAG Экран - $heightPixels на $widthPixels")
        }
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.viewModel = uiUtilViewModel
        binding.lifecycleOwner = this

        setupBottomNavMenu()
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    /**
     * Метод setupWithNavController вешает листенер на BottomNavigationView и выполняет навигацию
     * при нажатии на его элементы. Он сам повесит обработчик на NavigationView и по нажатию на пункты
     * меню будет выполнять навигацию к destination (или action) с тем же ID
     * в navigation\xxx_graph.xml, что и у нажатого пункта меню в menu\xxx_menu.xml.
     * При этом выполняет setChecked для нажатого элемента.
     * https://startandroid.ru/ru/courses/dagger-2/27-course/architecture-components/560-urok-27-navigation-navigationui.html
     */
    private fun setupBottomNavMenu() {
        mainNavigation.setupWithNavController(navController)
    }

    override fun onSharedPreferenceChanged(sp: SharedPreferences, key: String?) {
        Timber.d("$TAG .onSharedPreferenceChanged key = [${key}]")
        //Если изменилась тема в настройках, то меняем ее в программе
        when (key) {
            THEME -> {
                Timber.d("$TAG Theme set to - ${sp.getString(key, "AppTheme")}")
                this.recreate()
            }
        }
    }
}
