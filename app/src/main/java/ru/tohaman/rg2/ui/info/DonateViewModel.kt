package ru.tohaman.rg2.ui.info

import android.app.Activity
import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.android.billingclient.api.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.KoinComponent
import org.koin.core.get
import ru.tohaman.rg2.BuildConfig
import ru.tohaman.rg2.Constants
import ru.tohaman.rg2.Constants.PAYED_COINS
import ru.tohaman.rg2.DebugTag
import ru.tohaman.rg2.DebugTag.TAG
import ru.tohaman.rg2.utils.Resource
import ru.tohaman.rg2.utils.SingleLiveEvent
import ru.tohaman.rg2.utils.Status
import timber.log.Timber
import kotlin.let as let1

/**
 * ViewModel для установки соединения с биллингом и совершения покупок
 * подробнее https://developer.android.com/google/play/billing/integrate#kotlin
 */

class DonateViewModel(app: Application): AndroidViewModel(app), KoinComponent,
    PurchasesUpdatedListener {
    private val sp = get<SharedPreferences>()

    //Создаем экземпляр Billing-клиента
    private var billingClient = BillingClient.newBuilder(app)
        .setListener(this)
        .enablePendingPurchases()
        .build()

    init {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {
                // сюда мы попадем если что-то пойдет не так
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }

            //в методе onBillingSetupFinished() можно запросить информацию о товарах и покупках.
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                //запрашиваем доступные товары
                loadProducts()

                //запрос о совершенных покупках
                val purchases = billingClient.queryPurchases(BillingClient.SkuType.INAPP)
                purchases.purchasesList?.forEach {
                    if (!it.isAcknowledged) {
                        consumePurchase(it.purchaseToken, false)
                    }
                }
            }
        })
    }

    //СинглЛайвЭвент сработает во фрагменте только один раз при вызове .call() во viewModel
    val onStartOpenDonate  = SingleLiveEvent<Nothing>()

    //Проверяем нужно ли перейти на страничку доната
    fun checkDonationShow() {
        val isUserDonate = sp.getInt(PAYED_COINS, 0)
        val startCount = sp.getInt(Constants.START_COUNT, 1)
        Timber.d("$TAG .checkDonationShow $startCount $isUserDonate")
        //Если пользователь не платил, то каждый 10ый вход переводим на окно Доната
        if ((isUserDonate == 0) and (startCount % 10 == 0)) {
            //Поставим закладку на страничку с донатом
            sp.edit().putInt(Constants.INFO_BOOKMARK, 1).apply()
            onStartOpenDonate.call()
            //Увеличим счетчик входов, чтобы повторно не вызвалось
            sp.edit().putInt(Constants.START_COUNT, startCount + 1 ).apply()
        }
    }

    //Подгружаем список продуктов из Google Play Billing
    private fun loadProducts() {
        //Показать продукты, доступные для покупки
        val params = SkuDetailsParams.newBuilder()
            .setType(BillingClient.SkuType.INAPP)                      //Список внутриигровых покупок, BillingClient.SkuType.SUBS для подписок
            .setSkusList(
                listOf("small_donation", "medium_donation", "big_donation", "very_big_donation") +
                        if (BuildConfig.DEBUG) {
                            listOf("android.test.purchased", "android.test.canceled", "android.test.item_unavailable")
                        } else {
                            emptyList()
                }
            )
            .build()
        //Запуск потока покупок, получаем статус (result) и список (details)
        billingClient.querySkuDetailsAsync(params) { result, details ->
            products = if (result.responseCode == BillingClient.BillingResponseCode.OK && details != null) {
                //Если список загрузился успешно, то записываем его в products и ставим статус Success
                details.sortedBy { it.priceAmountMicros }
            } else {
                //иначе значеие null и статус Error
                listOf()
            }
        }
    }

    //Если была какая-то покупка
    private fun consumePurchase(purchaseToken: String, showSuccess: Boolean = true) {
        val params = ConsumeParams.newBuilder()
            .setPurchaseToken(purchaseToken)
            .build()
        //Запишем в SP, что пользователь что-то заплатил (условные 50 тугриков), если там не 0,
        // то не будем его периодически перекидывать на страничку с рекламой
        sp.edit().putInt(PAYED_COINS, 50).apply()

        //Если true, то отображаем snackBar - Спасибо за поддержку!
        billingClient.consumeAsync(params) { _, _ ->
            if (showSuccess) purchaseSuccessful.call()
        }
    }

    val purchaseSuccessful = SingleLiveEvent<Nothing>()         //SingleLiveEvent<T> : MutableLiveData<T>() with some
    val purchaseFailed = SingleLiveEvent<Nothing>()

    //Список продуктов
    private var products = listOf<SkuDetails>()

    //В метод onPurchasesUpdated() мы попадаем когда покупка осуществлена
    override fun onPurchasesUpdated(result: BillingResult, purchases: MutableList<Purchase>?) {
        if (result.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                val purchaseToken = purchase.purchaseToken
                consumePurchase(purchaseToken)
            }
        } else if (result.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            // Handle an error caused by a user cancelling the purchase flow.
        } else {
            //Если не ОК и не Cancel, значит "что-то пошло нет так"
            purchaseFailed.call()
        }
    }

    var activity: Activity? = null

    //вызываем при нажатии на элемент в интерфейсе
    fun startItemPurchaseByNumber(number: Int) {
        activity?.let1 {
            if (products.size > number) {
                startPurchase(products[number], it)
            }
        }
    }


    private fun startPurchase(it: SkuDetails, activity: Activity) {
        val flowParams = BillingFlowParams.newBuilder()
            .setSkuDetails(it)
            .build()
        val response = billingClient.launchBillingFlow(activity, flowParams)
        if (response.responseCode != BillingClient.BillingResponseCode.OK) {
            purchaseFailed.call()
        }
    }

    override fun onCleared() {
        billingClient.endConnection()
    }

}
