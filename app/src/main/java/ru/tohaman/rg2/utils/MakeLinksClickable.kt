package ru.tohaman.rg2.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.preference.PreferenceManager
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ClickableSpan
import android.text.style.URLSpan
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import ru.tohaman.rg2.Constants
import ru.tohaman.rg2.Constants.ALG
import ru.tohaman.rg2.Constants.LINK
import ru.tohaman.rg2.Constants.TIME
import ru.tohaman.rg2.DebugTag.TAG
import ru.tohaman.rg2.R
import ru.tohaman.rg2.ui.games.ScrambleGeneratorFragmentDirections
import ru.tohaman.rg2.ui.youtube.YouTubeActivity
import timber.log.Timber


object MakeLinksClickable {
    fun reformatText(text: CharSequence, callbacks: ClickTextHolder?): SpannableStringBuilder {
        val end = text.length
        val sp = text as Spannable
        val urls = sp.getSpans(0, end, URLSpan::class.java)
        val style = SpannableStringBuilder(text)
        for (url in urls) {
            style.removeSpan(url)
            val clickableSpan = CustomerTextClick(url.url, callbacks)
            style.setSpan(
                clickableSpan, sp.getSpanStart(url), sp.getSpanEnd(url),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        return style
    }

    class CustomerTextClick(var url: String, private var clickTextHolder: ClickTextHolder?) : ClickableSpan() {
        override fun onClick(widget: View) {
            val ctx = widget.context
            //Пробуем обработать строку во внешнем обработчике,
            var internalCall = clickTextHolder?.onUrlClick(url) ?: false
            //если он не смог, то проверяем на внутренние обработчики
            if (!internalCall) {
                when {
                    url.startsWith("rg2://scrmbl", true) or url.startsWith("rg2://scramble",true) -> {
                        widget.findNavController().navigate(
                            ScrambleGeneratorFragmentDirections.actionGlobalScrambleGeneratorFragment(getScrambleFromUrl(url))
                        )
                        internalCall = true
                    }
                    url.startsWith("rg2://ytplay", true) or url.startsWith("rg2://player", true) -> {
                        if ((getConnectionType(ctx) > getInternetLimits(ctx))) {
                            val intent = Intent(ctx, YouTubeActivity::class.java)
                            intent.putExtra(TIME, getTimeFromUrl(url))
                            intent.putExtra(LINK, getLinkFromUrl(url))
                            intent.putExtra(ALG, getAlgFromUrl(url))
                            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                            ctx.startActivity(intent)
                            internalCall = true
                        } else {
                            Snackbar.make(widget, ctx.getString(R.string.check_internet_access), Snackbar.LENGTH_SHORT)
                                .setAction(ctx.getString(R.string.ok)) { }
                                .setActionTextColor(ContextCompat.getColor(ctx, R.color.colorAccent))
                                .show()
                        }
                    }
                }
            }
            //Если ссылка не подходит и внутреннему обработчику, то пробуем открыть как обычную ссылку браузером
            //canOpen = смогли ли обработать ссылку хоть каким-то обработчиком или браузером
            val canOpen = if (internalCall) true
                else widget.context.browse(url, false)

            //Выводим результат в лог
            Timber.d("$TAG url clicked: $url with $canOpen result")
        }
    }
}

interface ClickTextHolder {
    fun onUrlClick(url: String): Boolean
}

fun Context.browse(url: String, newTask: Boolean = false): Boolean {
    return try {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        if (newTask) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)
        true
    } catch (e: ActivityNotFoundException) {
        e.printStackTrace()
        false
    }
}


//Получаем текущий лимит на использование интернета установленный в настройках, 0 - любой, 1 - WiFi, интернет недоступен - 4
//потом будем сравнивать с текущим доступным в системе 0 - недоступен, 1 - 3G/4G, 2 - WiFi, 3 - VPN
private fun getInternetLimits(context: Context): Int {
    //val sp = context.getSharedPreferences("${context.applicationInfo.packageName}_preferences", Context.MODE_PRIVATE)
    val sp = PreferenceManager.getDefaultSharedPreferences(context)
    val allInternet = sp.getBoolean(Constants.ALL_INTERNET, true)
    val wiFi = sp.getBoolean(Constants.ONLY_WIFI, false)
    return when {
        allInternet -> 0
        wiFi -> 1
        else -> 4
    }
}

//получаем нормальный скрамбл из строки вида rg2://scrmbl?scram=D2_F\'_R_L_U_R\'_D\'_B2_R\'_F2_R2_U2_R2_U\'
fun getScrambleFromUrl(url: String): String {
    //берем только часть строки после scram=
    var scramble = url.substringAfter("scram=")
    //Заменяем все символы которые не могут быть в скрамбле на пробелы
    scramble = Regex("[^RLDUFB 2'wMSE]").replace(scramble, " ")
    Timber.d("$TAG .getScrambleFromUrl $scramble from url = [${url}]")
    return scramble
}

//получаем время из строки вида "rg2://player/time=0:41/link=QJ8-8l9dQ_U" или "rg2://ytplay?time=17:33&link=Oh7HESee4wY&alg=F (R U R’ U’)(R U R’ U’) F’"
fun getTimeFromUrl(url: String): String {
    val time =      //берем все что после time= и до & или /
        url.substringAfter("time=")
            .substringBefore("&")
            .substringBefore("/")
    Timber.d("$TAG .getTimeFromUrl $time from url = [${url}]")
    return time
}

fun getLinkFromUrl(url: String): String {
    val link =
        url.substringAfter("link=")
            .substringBefore("&")
    Timber.d("$TAG .getLinkFromUrl $link from url = [${url}]")
    return link
}

fun getAlgFromUrl(url: String): String {
    return if (url.contains("alg=")) {
        val alg =
            url.substringAfter("alg=")
                .substringBefore("&")
                .replace("_"," ")
        Timber.d("$TAG .getAlgFromUrl $alg from url = [${url}]")
        alg
    } else ""
}