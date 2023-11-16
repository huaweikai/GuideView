package com.hua.guide

import android.app.Activity
import android.app.Dialog
import androidx.lifecycle.ViewTreeLifecycleOwner
import androidx.lifecycle.coroutineScope
import kotlinx.coroutines.launch

class AutoScrollGuideSequence: GuideSequence {

    constructor(activity: Activity?) : super(activity)

    constructor(dialog: Dialog?) : super(dialog)


    override fun showNext() {
        val target = targets.poll()
        if (target == null) {
            currentView = null
            listener?.onSequenceFinish()
            return
        }
        val view = target.view
        if (view == null) {
            showTapTarget(target)
        } else {
            ViewTreeLifecycleOwner.get(view)?.lifecycle?.coroutineScope?.launch {
                target.scrollToTarget()
                // 当队列已经cancel了，就不要再继续了
                if (!this@AutoScrollGuideSequence.isActive) { return@launch }
                showTapTarget(target)
            } ?: showTapTarget(target)
        }
    }

}