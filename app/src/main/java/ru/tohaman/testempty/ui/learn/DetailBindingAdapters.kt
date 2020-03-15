package ru.tohaman.testempty.ui.learn

import android.text.Html
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import ru.tohaman.testempty.DebugTag.TAG
import ru.tohaman.testempty.utils.getResource
import ru.tohaman.testempty.utils.spannedString
import timber.log.Timber

@BindingAdapter("android:srcId")
fun setImageResource(imageView: ImageView, resource: Int) {
    imageView.setImageResource(resource)
}

@BindingAdapter("android:htmlText")
fun setHtmlResource(textView: TextView, htmlId: Int) {
    var textString = "<html><body style=\"text-align:justify\"> %s </body></html>"
    var st = ""
    if (htmlId != 0) {
        st = textView.context.getString(htmlId)
    }
    textString = String.format(textString, st)

    val imgGetter = Html.ImageGetter { source ->
        var sourceString = source
        sourceString = sourceString.replace(".png", "")
        sourceString = sourceString.replace(".xml", "")
        val drawable = textView.context.getResource(sourceString)
        drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        drawable
    }

    textView.text = spannedString(textString, imgGetter, tagHandler)
}


@BindingAdapter("showTime")
fun showTime(textView: TextView, time: Long?) {
    time?.let {
        textView.text = "$it сек"
    }
}

private val tagHandler = Html.TagHandler { opening, tag, output, xmlReader ->
    //Тут можно обрабатывать свои тэги, например mytag, хотя в данном случае можно
    //просто написть _, _, _, _ -> null
    if (tag.equals("mytag", true)) {
        val open = opening
        val tag1 = tag
        val out = output
        val xml = xmlReader
        Timber.d ("$TAG - tagHandler = $open, $tag1, $out, $xml")
    }
}