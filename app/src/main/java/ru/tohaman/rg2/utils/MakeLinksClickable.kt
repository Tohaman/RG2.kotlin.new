package ru.tohaman.rg2.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ClickableSpan
import android.text.style.URLSpan
import android.view.View
import androidx.navigation.findNavController
import ru.tohaman.rg2.DebugTag.TAG
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

    class CustomerTextClick(var url: String, var clickTextHolder: ClickTextHolder?) : ClickableSpan() {
        override fun onClick(widget: View) {
            //Пробуем обработать строку во внешнем обработчике,
            var internalCall = clickTextHolder?.onUrlClick(url) ?: false
            //если он не смог, то проверяем стандартные для программы
            if (!internalCall) {
                when {
                    url.startsWith("rg2://scrmbl", true) or url.startsWith("rg2://scramble",true) -> {
                        widget.findNavController().navigate(
                            ScrambleGeneratorFragmentDirections.actionGlobalScrambleGeneratorFragment(getScrambleFromUrl(url))
                        )
                        internalCall = true
                    }
                    url.startsWith("rg2://ytplay", true) or url.startsWith("rg2://player", true) -> {
//                        widget.findNavController().navigate(
//                            LearnDetailFragmentDirections.actionDestLearnDetailsToYouTubeActivity(getTimeFromUrl(url), getLinkFromUrl(url))
//                        )
                        val intent = Intent(widget.context, YouTubeActivity::class.java)
                        intent.putExtra("time", getTimeFromUrl(url) )
                        intent.putExtra("link", getLinkFromUrl(url) )
                        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                        widget.context.startActivity(intent)
                        internalCall = true
                    }
                }
            }
            //Если ссылка не подходит и внутреннему обработчику, то пробуем открыть как обычную ссылку
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

//получаем нормальный скрамбл из строки вида rg2://scrmbl?scram=D2_F\'_R_L_U_R\'_D\'_B2_R\'_F2_R2_U2_R2_U\'
fun getScrambleFromUrl(url: String): String {
    //берем только часть строки после scram=
    var scramble = url.substringAfter("scram=")
    //Заменяем все символы которые не могут быть в скрамбле на пробелы
    scramble = Regex("[^RLDUFB 2'wMSE]").replace(scramble, " ")
    Timber.d("$TAG .getScrambleFromUrl $scramble from url = [${url}]")
    return scramble
}

//получаем время из строки вида "rg2://player/time=0:41/link=QJ8-8l9dQ_U" или "rg2://ytplay?time=0:41&link=QJ8-8l9dQ_U"
fun getTimeFromUrl(url: String): String {
    val time =      //берем все что после time= и до & или /
        url.substringAfter("time=")
            .substringBefore("&")
            .substringBefore("/")
    Timber.d("$TAG .getTimeFromUrl $time from url = [${url}]")
    return time
}

fun getLinkFromUrl(url: String): String {
    val phase = url.substringAfter("link=")
    Timber.d("$TAG .getTimeFromUrl $url from url = [${url}]")
    return phase
}
