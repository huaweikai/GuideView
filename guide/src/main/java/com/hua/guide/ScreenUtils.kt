@file:Suppress("InternalInsetResource", "DiscouragedApi")
package com.hua.guide

import android.content.Context
import android.content.res.Resources

val screenWidth: Int
    get() = Resources.getSystem().displayMetrics.widthPixels

val screenHeight: Int
    get() = Resources.getSystem().displayMetrics.heightPixels

sealed class ScreenStatus {
    object Large: ScreenStatus()
    object Medium: ScreenStatus()
    object Small: ScreenStatus()
}

val wrapTextWidth: Int
    get() = when (screenStatus) {
        ScreenStatus.Large -> screenWidth / 6
        ScreenStatus.Medium -> screenWidth / 5
        ScreenStatus.Small -> screenWidth / 3
    }

val screenStatus: ScreenStatus
    get() {
        val smallWidth = Resources.getSystem().configuration.screenWidthDp
        return when {
            smallWidth >= 720 -> ScreenStatus.Large
            smallWidth >= 600 -> ScreenStatus.Medium
            else -> ScreenStatus.Small
        }
    }

val Context.statusHeight: Int
    get() {
        val resourceId =
            Resources.getSystem().getIdentifier("status_bar_height", "dimen", "android")
        return if (resourceId > 0) {
            resources.getDimensionPixelSize(resourceId)
        } else {
            0
        }
    }