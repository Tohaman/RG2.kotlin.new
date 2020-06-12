package ru.tohaman.rg2.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.koin.android.ext.android.inject
import ru.tohaman.rg2.R
import ru.tohaman.rg2.dbase.FillDB
import timber.log.Timber
import ru.tohaman.rg2.DebugTag.TAG
import ru.tohaman.rg2.ui.shared.MyDefaultActivity


class SplashActivity : MyDefaultActivity() {
    private val sharedPreferences: SharedPreferences by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        //Меняем тему SplashActivity на обычную
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            setTheme(R.style.AppTheme)
        }
        super.onCreate(savedInstanceState)
        dbInit(this)
        routeToAppropriatePage()
        finish()
    }

    private fun routeToAppropriatePage() {
        Timber.d( "$TAG Запускаем основную Activity")
        val intent = Intent(this, MainActivity::class.java)
        //Тут можно сделать запуск какой-то Activity в зависимости от какого-то параметра
        val savedPhase = sharedPreferences.getString("savedPhase", "3X3")
        Timber.d( "$TAG Сохраненная фаза $savedPhase")
//        when (){
//            user == null -> intent = Intent(this, FirstActivity::class.java)
//            user.hasPhoneNumber() -> intent = Intent(this, EditProfileActivity::class.java)
//            user.hasSubscriptionExpired() -> intent = Intent(this, PaymentPlansActivity::class.java)
//        }
        startActivity(intent)
    }

    private fun dbInit(context: Context) {
        Timber.d( "$TAG Инициализируем БД")
        //Чтобы дождаться завершения выполнения инициализации, запустим в runBlocking,
        //а поскольку нельзя к БД Room обращаться в основном потоке, то запустим корутину в IO потоке
        runBlocking (Dispatchers.IO){
            //Пересоздаем базу при каждом запуске
            //TODO поменять логику, пересоздаавать базу только при первом входе или обновлении программы
            FillDB.reCreateDB(context)
        }
    }
}