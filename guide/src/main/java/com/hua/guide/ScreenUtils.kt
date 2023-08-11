package com.hua.guide

import android.content.res.Resources

val screenWidth: Int
    get() = Resources.getSystem().displayMetrics.widthPixels

val screenHeight: Int
    get() = Resources.getSystem().displayMetrics.heightPixels