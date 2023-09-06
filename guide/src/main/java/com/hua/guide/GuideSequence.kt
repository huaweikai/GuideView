@file:Suppress("unused")
package com.hua.guide

import android.app.Activity
import android.app.Dialog
import android.widget.PopupWindow
import androidx.annotation.UiThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewTreeLifecycleOwner
import java.lang.RuntimeException
import java.util.LinkedList
import java.util.Queue

open class GuideSequence: GuideListener {

    protected val targets: Queue<TapTarget> = LinkedList()

    private var activity: Activity? = null

    private var dialog: Dialog? = null

    private var popupWindow: PopupWindow? = null

    internal var isActive: Boolean = false

    protected var listener: Listener? = null

    private var considerOuterCircleCanceled = false

    private var continueOnCancel = false

    protected var currentView: GuideView? = null

    constructor(activity: Activity?) {
        this.activity = activity
        this.dialog = null
    }

    constructor(dialog: Dialog?) {
        this.dialog = dialog
        this.activity = null
    }

    /**
     * @param lifecycleOwner 用于自动翻页查找view，因为是在协程里面做的，所以最好是有一个关联页面的Lifecycle
     */
    constructor(popupWindow: PopupWindow?, lifecycleOwner: LifecycleOwner) {
        this.popupWindow = popupWindow
        val view = popupWindow?.contentView ?: throw RuntimeException(
            "popupWindow contentView is null, please check your popupWindow"
        )
        ViewTreeLifecycleOwner.set(view, lifecycleOwner)
        this.activity = null
        this.dialog = null
    }

    fun targets(targets: Iterable<TapTarget>): GuideSequence {
        this.targets.addAll(targets)
        return this
    }

    fun addTarget(vararg targets: TapTarget): GuideSequence {
        this.targets.addAll(targets)
        return this
    }

    fun continueOnCancel(status: Boolean): GuideSequence {
        this.continueOnCancel = status
        return this
    }

    fun considerOuterCircleCanceled(status: Boolean): GuideSequence {
        considerOuterCircleCanceled = status
        return this
    }

    /** Specify the listener for this sequence  */
    fun listener(listener: Listener?): GuideSequence {
        this.listener = listener
        return this
    }


    @UiThread
    fun start() {
        if (targets.isEmpty() || isActive) return
        isActive = true
        showNext()
    }

    @UiThread
    fun cancel(): Boolean {
        val currentView = this.currentView ?: return false
        if (!isActive || !currentView.cancelable) return false
        currentView.dismiss()
        isActive = false
        targets.clear()
        listener?.onSequenceCanceled(currentView.tapTarget)
        return true
    }

    fun startAt(index: Int) {
        if (isActive) return
        if (index < 0  || index >= targets.size) return
        val expectedSize = targets.size - index
        repeat(index) {
            targets.poll()
        }
        check(targets.size == expectedSize) { "Given index $index not in sequence" }
        start()
    }

    protected open fun showNext() {
        val target = targets.poll()
        if (target == null) {
            currentView = null
            listener?.onSequenceFinish()
            return
        }
        showTapTarget(target)
    }

    override fun clickTarget(view: GuideView) {
        view.dismiss()
        listener?.onSequenceStep(view.tapTarget, view, true)
        showNext()
    }

    private fun dismiss(view: GuideView) {
        view.dismiss()
        isActive = false
        targets.clear()
        listener?.onSequenceCanceled(view.tapTarget)
    }

    override fun clickOther(view: GuideView) {
        if (considerOuterCircleCanceled) dismiss(view)
    }

    protected fun showTapTarget(tapTarget: TapTarget) {
        activity?.let {
            currentView = it.showGuideView(tapTarget.setGuideListener(this))
        }
        dialog?.let {
            currentView = it.showGuideView(tapTarget.setGuideListener(this))
        }
        popupWindow?.let {
            currentView = it.showGuideView(tapTarget.setGuideListener(this))
        }
    }

    interface Listener {
        /** Called when there are no more tap targets to display  */
        fun onSequenceFinish()

        /**
         * Called when moving onto the next tap target.
         * @param lastTarget The last displayed target
         * @param targetClicked Whether the last displayed target was clicked (this will always be true
         * unless you have set [.continueOnCancel] and the user
         * clicks outside of the target
         */
        fun onSequenceStep(lastTarget: TapTarget?, guideView: GuideView?, targetClicked: Boolean)

        /**
         * Called when the user taps outside of the current target, the target is cancelable, and
         * [.continueOnCancel] is not set.
         * @param lastTarget The last displayed target
         */
        fun onSequenceCanceled(lastTarget: TapTarget?)
    }

}