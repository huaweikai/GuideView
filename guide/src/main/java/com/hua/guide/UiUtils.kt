@file:JvmName("UiUtils")
@file:Suppress("unused")
package com.hua.guide

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.res.Resources
import android.util.TypedValue
import androidx.annotation.FloatRange

val Int.dp get(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()

internal val Float.dp get(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()

internal val Int.px get(): Int = (this / Resources.getSystem().displayMetrics.density).toInt()

internal val Int.sp get(): Int = (this * Resources.getSystem().displayMetrics.scaledDensity).toInt()

internal val Int.px2dp get(): Int = (this / Resources.getSystem().displayMetrics.density).toInt()

@SuppressLint("DiscouragedApi")
internal fun Context.getThemeIneAttr(
    attr: String
): Int {
    val theme = theme ?: return -1
    val typedValue = TypedValue()
    val id = resources.getIdentifier(attr, "attr", packageName)
    if (id == 0) return -1
    theme.resolveAttribute(id, typedValue, true)
    return typedValue.data
}

fun Int.setAlpha(
    @FloatRange(from = 0.0, to = 1.0) alpha: Float
): Int {
    return ((this ushr 24) * alpha).toInt() shl 24 or (this and 0x00FFFFFF)
}

fun Context.isDebug(): Boolean {
    return runCatching {
        (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
    }.getOrNull() ?: false
}