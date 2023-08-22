package com.hua.guide


interface GuideListener {

    fun clickTarget(view: GuideView)

    fun clickOther(view: GuideView)

    fun onDismiss() {}

}

open class PreformClickGuideListener : GuideListener {

    companion object {
        val INSTANCE = PreformClickGuideListener()
    }

    override fun clickTarget(view: GuideView) {
        view.getRealView()?.performClick()
        view.dismiss()
    }

    override fun clickOther(view: GuideView) {
        if (view.cancelable) {
            view.dismiss()
        }
    }

    override fun onDismiss() {}

}