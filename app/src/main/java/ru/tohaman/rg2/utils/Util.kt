package ru.tohaman.rg2.utils

import android.content.Context
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.Editable
import android.text.Html
import android.text.Spanned
import android.util.TypedValue
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import ru.tohaman.rg2.DebugTag.TAG
import ru.tohaman.rg2.R
import timber.log.Timber

/**
 * Created by Test on 22.12.2017. Различные утилиты
 */


fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

// extension method to convert pixels to dp
fun Int.dp(context: Context):Int = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), context.resources.displayMetrics
).toInt()

fun spannedString(desc:String, imgGetter: Html.ImageGetter, tagHandler: Html.TagHandler? = null): Spanned {
    // Немного преобразуем текст для корректного отображения.
    val desc1 = desc.replace("%%", "%25")

    // Android 7.0 ака N (Nougat) = API 24, начиная с версии Андроид 7.0 вместо HTML.fromHtml (String)
    // лучше использовать HTML.fromHtml (String, int), где int различные флаги, влияющие на отображение html
    // аналогично для метода HTML.fromHtml (String, ImageGetter, TagHandler) -> HTML.fromHtml (String, int, ImageGetter, TagHandler)
    // поэтому используем @SuppressWarnings("deprecation") перед объявлением метода и вот такую конструкцию
    // для преобразования String в Spanned. В принципе использование старой конструкции равноценно использованию
    // новой с флагом Html.FROM_HTML_MODE_LEGACY... подробнее о флагах-модификаторах на developer.android.com
    // В методе Html.fromHtml(String, imgGetter, tagHandler) - tagHandler - это метод, который вызывется, если
    // в строке встречается тэг, который не распознан, т.е. тут можно обрабатывать свои тэги
    // пока не используется (null), но все воозможно :)

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(desc1, Html.FROM_HTML_MODE_LEGACY, imgGetter, tagHandler)
    } else {
        @Suppress("DEPRECATION")
        Html.fromHtml(desc1, imgGetter, tagHandler)
    }
}

fun Context.getResource(name:String): Drawable {
    val resID = this.resources.getIdentifier(name , "drawable", this.packageName)
    val defDrawable = ActivityCompat.getDrawable(this, R.drawable.ic_warning)!!
    return ActivityCompat.getDrawable(this,resID) ?: defDrawable
}

//Возвращает Map для определения типа головоломки (трешка, мод, большой куб и т.д.) по фазе головоломки
//т.е. Phase -> Type
fun getPhasesToTypesMap(context: Context): Map<String, String> {
    val phasesArray = context.resources.getStringArray(R.array.phases)
    val typesArray = context.resources.getStringArray(R.array.types)
    val phasesToTypes: MutableMap<String, String> = mutableMapOf()
    for (i in phasesArray.indices) {
        phasesToTypes[phasesArray[i]] = typesArray[i]
    }

    return phasesToTypes
}

//fun getNameFromListPagers(ListPagers: List<ListPager>, i: Int): String =
//        if (ListPagers[i].comment == "") {
//            ListPagers[i].title
//        } else {
//            ListPagers[i].comment
//        }
//
fun getThemeFromSharedPreference(sp: SharedPreferences) : Int {
    val theme = sp.getString("theme", "AppTheme")
    Timber.v("$TAG SetActivityTheme - $theme")
    return when (theme)  {
        "AppThemeLight" -> R.style.AppThemeLight
        //"AppThemeDayNight" -> R.style.AppThemeDayNight
        else -> R.style.AppTheme
    }
}

fun toast(message: String, view: View) {
    //Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
        .setAction("OK") { }
        .setActionTextColor(ContextCompat.getColor(view.context, R.color.colorAccent))
        .show()
}
