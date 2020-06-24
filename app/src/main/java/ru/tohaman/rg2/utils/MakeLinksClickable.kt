package ru.tohaman.rg2.utils

import android.content.ActivityNotFoundException
import android.content.ComponentCallbacks
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ClickableSpan
import android.text.style.URLSpan
import android.view.View
import androidx.databinding.ObservableField
import ru.tohaman.rg2.DebugTag.TAG
import timber.log.Timber


object MakeLinksClickable {
    fun reformatText(text: CharSequence, callbacks: ClickableText?): SpannableStringBuilder {
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

    class CustomerTextClick(var url: String, var callBack: ClickableText?) : ClickableSpan() {
        override fun onClick(widget: View) {
            //Тут можно как-то обработать нажатие на ссылку
            //Сейчас же мы просто открываем браузер с ней (

            val internalCall = callBack?.clickCallBack(url) ?: false
            //Если ссылка не подходит внутреннему обработчику, то пробуем открыть как обычную ссылку
            val canOpen = if (internalCall) true
            else widget.context.browse(url, false)

            Timber.d("$TAG url clicked: $url with $canOpen result")
        }
    }
}

interface ClickableText {
    fun clickCallBack(url: String): Boolean
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