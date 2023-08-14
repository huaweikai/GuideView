@file:Suppress("unused")
package com.hua.guide

import android.graphics.Rect
import android.graphics.RectF

fun RectF.toNew(): RectF {
    return RectF(this.left, this.top, this.right, this.bottom)
}

fun Rect.toNewRectF(): RectF {
    return RectF(this.left.toFloat(), this.top.toFloat(), this.right.toFloat(), this.bottom.toFloat())
}