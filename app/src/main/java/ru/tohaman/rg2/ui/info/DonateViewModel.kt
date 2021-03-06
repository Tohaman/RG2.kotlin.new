package ru.tohaman.rg2.ui.info

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.android.billingclient.api.*
import ru.tohaman.rg2.AppSettings
import ru.tohaman.rg2.BuildConfig
import ru.tohaman.rg2.DebugTag.TAG
import ru.tohaman.rg2.utils.SingleLiveEvent
import timber.log.Timber
import kotlin.let as let1

/**
 * ViewModel для установки соединения с биллингом и совершения покупок
 * подробнее https://developer.android.com/google/play/billing/integrate#kotlin
 */

class DonateViewModel(app: Application): AndroidViewModel(app), PurchasesUpdatedListener {

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
        //Если включен режим разработчика, то прибавляем к монеткам 1 (типа заплатил)
        val godMoney = if (AppSettings.godMode) 1 else 0
        val isUserDonate = AppSettings.payCoins + godMoney
        val startCount = AppSettings.startCount
        Timber.d("$TAG .checkDonationShow $startCount $isUserDonate")
        //Если пользователь не платил, то каждый 6ый вход переводим на окно Доната
        if ((isUserDonate == 0) and (startCount % 6 == 0)) {
            //Поставим закладку на страничку с донатом
            AppSettings.infoBookmark = 1
            onStartOpenDonate.call()
            //Увеличим счетчик входов, чтобы повторно не вызвалось
            AppSettings.startCount = startCount + 1
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
        AppSettings.payCoins = 50

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
            Timber.d("$TAG .startItemPurchaseByNumber $number, from $products")
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
