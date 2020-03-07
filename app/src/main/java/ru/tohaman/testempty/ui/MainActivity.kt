package ru.tohaman.testempty.ui

import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.MenuItem
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.navigation.ui.setupWithNavController
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.tohaman.testempty.R
import ru.tohaman.testempty.DebugTag.TAG
import ru.tohaman.testempty.databinding.ActivityMainBinding
import ru.tohaman.testempty.ui.shared.MyDefaultActivity
import ru.tohaman.testempty.ui.shared.UiUtilViewModel
import timber.log.Timber

class MainActivity : MyDefaultActivity() {

    private val uiUtilViewModel by viewModel<UiUtilViewModel>()
    private lateinit var binding: ActivityMainBinding

    private val navController by lazy {
        Navigation.findNavController(this, R.id.mainFragment)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        DisplayMetrics().apply {
            windowManager.defaultDisplay.getMetrics(this)
            Timber.tag(TAG).d ("Экран - $heightPixels на $widthPixels")
        }
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.viewModel = uiUtilViewModel
        binding.lifecycleOwner = this

        setupBottomNavMenu()
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

//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        val inflater = menuInflater
//        inflater.inflate(R.menu.bottomappbar_menu, menu)
//        return true
//    }

    //-------------------------------------------------------------------------

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.main_learn -> toast(getString(
                R.string.learn_clicked
            ))
            R.id.main_info -> toast(getString(
                R.string.info_clicked
            ))
            R.id.main_games -> toast(getString(
                R.string.main_games_clicked
            ))
            R.id.main_settings -> toast(getString(
                R.string.main_settings_clicked
            ))
        }
        return true
    }

    // This is an extension method for easy Toast call
    fun Context.toast(message: CharSequence) {
        val toast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.BOTTOM, 0, 200)
        toast.show()
    }
}
