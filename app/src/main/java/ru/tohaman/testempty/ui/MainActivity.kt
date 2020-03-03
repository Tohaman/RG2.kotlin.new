package ru.tohaman.testempty.ui

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import ru.tohaman.testempty.R
import ru.tohaman.testempty.ui.bottomNavigation.BottomNavigationDrawerFragment

class MainActivity : MyDefaultActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(bottom_app_bar)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.bottomappbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.main_favorite -> toast(getString(
                R.string.fav_clicked
            ))
            R.id.main_search -> toast(getString(
                R.string.search_clicked
            ))
            R.id.main_help -> toast(getString(
                R.string.main_help_clicked
            ))
            R.id.main_settings -> toast(getString(
                R.string.main_settings_clicked
            ))
            android.R.id.home -> {
                val bottomNavDrawerFragment =
                    BottomNavigationDrawerFragment()
                bottomNavDrawerFragment.show(supportFragmentManager, bottomNavDrawerFragment.tag)
            }
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
