@file:Suppress("unused")
package com.hua.guide

import android.graphics.RectF

fun RectF.toNew(): RectF {
    return RectF(this.left, this.top, this.right, this.bottom)
}