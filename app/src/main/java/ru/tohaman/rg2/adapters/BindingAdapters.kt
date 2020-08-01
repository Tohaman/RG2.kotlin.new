package ru.tohaman.rg2.adapters

import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.text.Html
import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.widget.ImageViewCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import ru.tohaman.rg2.DebugTag.TAG
import ru.tohaman.rg2.R
import ru.tohaman.rg2.adapters.universal.DataBindingRecyclerAdapter
import ru.tohaman.rg2.dataSource.entitys.RecyclerItem
import ru.tohaman.rg2.utils.ClickTextHolder
import ru.tohaman.rg2.utils.MakeLinksClickable
import ru.tohaman.rg2.utils.getResource
import ru.tohaman.rg2.utils.spannedString
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

@BindingAdapter("android:srcId")
fun setImageResource(imageView: ImageView, resource: Int) {
    if (resource != 0) imageView.setImageResource(resource)
    else imageView.setImageResource(R.drawable.ic_wrong)
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

    val spannedString = MakeLinksClickable.reformatText(spannedString(textString, imgGetter, tagHandler), null)

    textView.text = spannedString
}


@BindingAdapter("app:clickedHtmlText", "app:urlClick")
fun setClickedHtmlResource(textView: TextView, htmlId: Int, urlClickCallBack: ClickTextHolder?) {
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

    val spannedString = MakeLinksClickable.reformatText(spannedString(textString, imgGetter, tagHandler), urlClickCallBack)

    textView.text = spannedString
    Linkify.addLinks(textView, Linkify.WEB_URLS)
    textView.movementMethod = LinkMovementMethod.getInstance()
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

@BindingAdapter("app:isEnabled", "app:enabledDrawable", "app:disabledDrawable")
fun setDrawableByComment(imageView: ImageView, isFavourite: Boolean, enDrawable: Drawable?, disDrawable: Drawable?) {
    imageView.setImageDrawable(if (isFavourite) enDrawable else disDrawable)
}

@BindingAdapter("app:visibleIfNotEmpty")
fun visibleIfNotEmpty(view: View, id: String) {
    Timber.d("$TAG VisibleIfNotEmpty - $id")
    view.visibility = if (id != "") View.VISIBLE else View.GONE
}

@BindingAdapter("app:favComment", "app:setComment")
fun commentChoose(textView: TextView, favComment: String, comment: String) {
    val text = if (favComment == "") comment else favComment
    textView.text = text
}

@BindingAdapter("app:text")
fun textToString(textView: TextView, int: Int) {
    textView.text = int.toString()
}

@BindingAdapter("android:textSizeSP")
fun textSizeToSP(textView: TextView, size: Int) {
    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, size.toFloat());
}

@BindingAdapter("app:fullDate")
fun fullDateToString(textView: TextView, calendar: Calendar?) {
    val sdf = SimpleDateFormat("dd MMM yy HH:mm:ss", Locale.getDefault())
    val strDate = sdf.format(calendar?.time ?: Calendar.getInstance().time)
    textView.text = strDate
}

@BindingAdapter("app:dateText")
fun dateToString(textView: TextView, calendar: Calendar?) {
    val sdf = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
    val strDate = sdf.format(calendar?.time ?: Calendar.getInstance().time)
    textView.text = strDate
}



@BindingAdapter("onSeekListener")
fun bindOnSeekListener(seekBar: SeekBar, listener: SeekBar.OnSeekBarChangeListener?) {
    seekBar.setOnSeekBarChangeListener(listener)
}

@BindingAdapter("app:isEnabled")
fun seekEnabled(seekBar: SeekBar, enabled: Boolean) {
    seekBar.isEnabled = enabled
}

@BindingAdapter("app:isEnabled")
fun seekEnabled(view: View, enabled: Boolean) {
    view.isEnabled = enabled
}

@BindingAdapter("app:tint")
fun setTint(imageView: ImageView?, int: Int) {
    if (int == 0 || int == -1) {
        ImageViewCompat.setImageTintList(imageView ?: return, null)
    } else {
        ImageViewCompat.setImageTintList(imageView ?: return, ColorStateList.valueOf(int))
    }
}

@BindingAdapter("app:srcDrawable")
fun setDrawable(imageView: AppCompatImageView, drawable: LayerDrawable) {
    imageView.setImageDrawable(drawable)
}

@BindingAdapter("items")
fun setRecyclerViewItems(recyclerView: RecyclerView, items: List<RecyclerItem>?) {
    var adapter = (recyclerView.adapter as? DataBindingRecyclerAdapter)
    if (adapter == null) {
        adapter = DataBindingRecyclerAdapter()
        recyclerView.adapter = adapter
    }

    adapter.submitList(
        items.orEmpty()
    )
}

@BindingAdapter("app:state")
fun setStateBackground(imgView: ImageView, state: PlayerConstants.PlayerState) {
    if (state == PlayerConstants.PlayerState.PLAYING)
        imgView.setImageResource(R.drawable.player_pause)
    else
        imgView.setImageResource(R.drawable.player_play)
}