package ru.tohaman.rg2.utils

import android.content.Context
import android.content.Intent

fun Context.shareText(shareTitle: String, textToShare: String) {
    val shareIntent = Intent().apply {
        action = Intent.ACTION_SEND
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, textToShare)
    }
    startActivity(Intent.createChooser(shareIntent, shareTitle))
}