package com.hua.guide


interface GuideListener {

    fun clickTarget(view: GuideView) {
        view.getRealView()?.performClick()
        view.dismiss()
    }

    fun clickOther(view: GuideView) {}

    fun dismiss(view: GuideView) {
        view.dismiss()
    }

}