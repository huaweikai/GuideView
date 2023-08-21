package com.hua.guide


interface GuideListener {

    fun clickTarget(view: GuideView) {
        view.getRealView()?.performClick()
        view.dismiss()
    }

    fun clickOther(view: GuideView) {}

    fun onDismiss() {}

}