package com.hua.guide

import android.app.Activity
import android.app.Dialog
import android.widget.ScrollView
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.ViewTreeLifecycleOwner
import androidx.lifecycle.coroutineScope
import androidx.recyclerview.widget.RecyclerView
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
                scrollToTarget(target)
                if (!this@AutoScrollGuideSequence.isActive) { return@launch }
                showTapTarget(target)
            }
        }
    }

    private suspend fun scrollToTarget(target: TapTarget) {
        val view = target.view ?: return
        if (view is RecyclerView) {
            val itemView = view.findItemViewByRecyclerView(target.indexAndView)
            target.view = itemView
            return
        }
        val pParent = view.parent.parent ?: return
        when (pParent) {
            is NestedScrollView -> {
                pParent.findAndScrollView(view)
            }
            is ScrollView -> {
                pParent.findAndScrollView(view)
            }
            else -> return
        }
    }

}