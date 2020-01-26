package ru.tohaman.testempty.activitys

import android.content.Intent
import android.os.Build
import android.os.Bundle
import ru.tohaman.testempty.MainActivity
import ru.tohaman.testempty.R


class SplashActivity : MyDefaultActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            setTheme(R.style.AppTheme)
        }
        super.onCreate(savedInstanceState)
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
