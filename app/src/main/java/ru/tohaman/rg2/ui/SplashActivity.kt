package ru.tohaman.rg2.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.tohaman.rg2.Constants.DAY_PATTERN
import ru.tohaman.rg2.Constants.TEXT_SIZE
import ru.tohaman.rg2.Constants.THEME
import ru.tohaman.rg2.R
import ru.tohaman.rg2.dbase.FillDB
import timber.log.Timber
import ru.tohaman.rg2.DebugTag.TAG
import ru.tohaman.rg2.AppSettings
import java.text.SimpleDateFormat
import java.util.*


class SplashActivity : AppCompatActivity() {
    private val sp: SharedPreferences by inject()
    private val migrationsViewModel by viewModel<MigrationsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        //Меняем тему SplashActivity на обычную
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            setTheme(R.style.AppTheme)
        }
        super.onCreate(savedInstanceState)
        checkEnteringCount()
        migrationsViewModel.migrationToNewVersion()
        routeToAppropriatePage()
        finish()
    }

    private fun routeToAppropriatePage() {
        Timber.d("$TAG Запускаем основную Activity")
        val intent = Intent(this, MainActivity::class.java)
        //Тут можно сделать запуск какой-то Activity в зависимости от какого-то параметра
//        val savedPhase = sharedPreferences.getString("savedPhase", "3X3")
//        Timber.d( "$TAG Сохраненная фаза $savedPhase")
//        when (){
//            user == null -> intent = Intent(this, FirstActivity::class.java)
//            user.hasPhoneNumber() -> intent = Intent(this, EditProfileActivity::class.java)
//            user.hasSubscriptionExpired() -> intent = Intent(this, PaymentPlansActivity::class.java)
//        }
        startActivity(intent)
    }

    private fun checkEnteringCount() {
        var startCount = AppSettings.startCount
        if (startCount == 0) {
            dbInit(this)        //Первый запуск программы
        } else {
            dbUpdate(this)      //Не первый запуск
        }
        startCount++
        //startCount = 0                //Если надо сбросить счетчик
        var dateOfLastEnter = AppSettings.dayOfLastEnter
        var currentDayEnterCount = AppSettings.dayEnterCount

        val sdf = SimpleDateFormat(DAY_PATTERN, Locale.getDefault())
        val now = Calendar.getInstance()
        val stringToday = sdf.format(now.time)
        val stringDayOfLastEnter = sdf.format(Date(dateOfLastEnter))

        Timber.d("$TAG .checkEnteringCount today = $stringToday, lastEnter = $stringDayOfLastEnter")
        if (stringToday == stringDayOfLastEnter) {
            currentDayEnterCount++
        } else {
            dateOfLastEnter = now.timeInMillis
            currentDayEnterCount = 1
        }

        saveEnterParameters(currentDayEnterCount, dateOfLastEnter, startCount)
    }

    private fun saveEnterParameters(currentDayEnterCount: Int, lastEnterDate: Long, startCount: Int) {
        Timber.d("$TAG .saveEnterParameters currentDayEnterCount = [${currentDayEnterCount}], dateOfLastEnter = [${lastEnterDate}], startCount = [${startCount}]")
        AppSettings.dayEnterCount = currentDayEnterCount
        AppSettings.dayOfLastEnter = lastEnterDate
        AppSettings.startCount = startCount
    }

    private fun dbInit(context: Context) {
        Timber.d("$TAG Инициализируем БД")
        //Чтобы дождаться завершения выполнения инициализации, запустим в runBlocking,
        //а поскольку нельзя к БД Room обращаться в основном потоке, то запустим корутину в IO потоке
        runBlocking(Dispatchers.IO) {
            sp.edit().putInt(TEXT_SIZE, 2).apply()
            sp.edit().putString(THEME, "AppTheme").apply()
            FillDB.reCreateDB(context)
            migrationsViewModel.migrateFavourite()
        }
    }

    private fun dbUpdate(context: Context) {
        Timber.d("$TAG апдейтим БД")
        runBlocking(Dispatchers.IO) {
            FillDB.updateDB(context)
        }
    }
}
