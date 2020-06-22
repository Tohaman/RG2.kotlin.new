package ru.tohaman.rg2.ui.info

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.android.billingclient.api.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.KoinComponent
import ru.tohaman.rg2.BuildConfig
import ru.tohaman.rg2.utils.Resource
import ru.tohaman.rg2.utils.SingleLiveEvent
import ru.tohaman.rg2.utils.Status

/**
 * ViewModel для установки соединения с биллингом и совершения покупок
 * подробнее https://developer.android.com/google/play/billing/integrate#kotlin
 */

class DonateViewModel(app: Application): AndroidViewModel(app), KoinComponent,
    PurchasesUpdatedListener {

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
            if (result.responseCode == BillingClient.BillingResponseCode.OK && details != null) {
                //Если список загрузился успешно, то записываем его в products и ставим статус Success
                products.value = Resource.success(details
                    .sortedBy { it.priceAmountMicros }
                    //.map { DonationItem(it) }
                )
            } else {
                //иначе значеие null и статус Error
                products.value = Resource.error(result.debugMessage, null)
            }
        }
    }

    private fun consumePurchase(purchaseToken: String, showSuccess: Boolean = true) {
        val params = ConsumeParams.newBuilder()
            .setPurchaseToken(purchaseToken)
            .build()
        billingClient.consumeAsync(params) { _, _ ->
            if (showSuccess) purchaseSuccessful.call()
        }
    }

    val purchaseSuccessful = SingleLiveEvent<Nothing>()         //SingleLiveEvent<T> : MutableLiveData<T>() with some
    val purchaseFailed = SingleLiveEvent<Nothing>()

    //Список продуктов
    val products: MutableLiveData<Resource<List<SkuDetails>>> by lazy {
        MutableLiveData<Resource<List<SkuDetails>>>().apply {
            value = Resource.loading(null)
        }
    }

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
            purchaseFailed.call()
        }
    }

    var activity: Activity? = null

    //вызываем при нажатии на элемент в интерфейсе
    fun startItemPurchaseByNumber(number: Int) {
        activity?.let {
            val activity = it
            products.value?.data?.get(number)?.let { skuDetails ->
                startPurchase(skuDetails, activity)
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

//data class DonationItem(val sku: SkuDetails) : Equatable