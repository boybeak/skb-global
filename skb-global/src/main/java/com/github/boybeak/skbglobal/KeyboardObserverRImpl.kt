package com.github.boybeak.skbglobal

import android.annotation.TargetApi
import android.app.Activity
import android.os.Build
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowInsetsAnimation
import java.lang.ref.WeakReference

@TargetApi(Build.VERSION_CODES.R)
internal class KeyboardObserverRImpl internal constructor(
    activity: Activity,
    private val showDebug: Boolean
) : AbsKeyboardObserver() {

    companion object {
        private const val TAG = "KeyboardObserverRImpl"
    }

    private var activityRef = WeakReference(activity)

    private var observerViewRef: WeakReference<View>? = null

    override val isWatching: Boolean
        get() = observerViewRef?.get() != null

    override fun watch(notifyNow: Boolean) {
        if (isWatching) {
            return
        }
        activityRef.get()?.window?.decorView?.run {
            val observerView = View(this.context)
            observerView.id = R.id.skb_observer_view
            observerView.setWindowInsetsAnimationCallback(object : WindowInsetsAnimation.Callback(DISPATCH_MODE_STOP) {
                override fun onProgress(
                    insets: WindowInsets,
                    runningAnimations: MutableList<WindowInsetsAnimation>
                ): WindowInsets {
                    val imeHeight = insets.getInsets(WindowInsets.Type.ime()).bottom
                    Log.d(TAG, "onProgress imeHeight=${imeHeight}")
                    return insets
                }
            })
            observerView.setOnApplyWindowInsetsListener { v, insets ->
                val imeHeight = insets.getInsets(WindowInsets.Type.ime()).bottom
                Log.d(TAG, "onApplyWindowInsets imeHeight=${imeHeight}")
                safeNotifyCallbacks(imeHeight)
                insets
            }
            (this as ViewGroup).addView(observerView, 1, ViewGroup.LayoutParams.MATCH_PARENT)

            observerViewRef = WeakReference(observerView)
        }
    }

    override fun unwatch() {
        if (!isWatching) {
            return
        }
        activityRef.get()?.window?.decorView?.run {
            val observerView = observerViewRef?.get()
            if (observerView != null) {
                observerView.setOnApplyWindowInsetsListener(null)
                (this as ViewGroup).removeView(observerView)

                observerViewRef?.clear()
                observerViewRef = null
            }
        }
    }
}