package com.github.boybeak.skbglobal

import android.app.Activity
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.PopupWindow
import android.widget.TextView
import java.lang.ref.WeakReference
import java.util.LinkedList
import kotlin.math.max

class KeyboardObserver private constructor(activity: Activity, private val showDebug: Boolean = false) {

    companion object {
        fun create(activity: Activity, showDebug: Boolean = false): KeyboardObserver {
            return KeyboardObserver(activity, showDebug)
        }
    }

    private val density = activity.resources.displayMetrics.density
    private val Number.dp get() = (this.toFloat() * density).toInt()

    private val decorView: View? get() {
        return activityRef.get()?.window?.decorView
    }

    private val activityRef = WeakReference(activity)
    private val rulerPopWin by lazy { makeRulerPopWin(activity) }
    private val cursorPopWin by lazy { makeCursorPopWin(activity) }

    private val rulerRect = Rect()
    private val cursorRect = Rect()

    /**
     * Calculate soft keyboard height and notify callbacks.
     */
    private val rulerLayoutChangeListener = object : View.OnLayoutChangeListener {

        private var lastKeyboardHeight = 0

        override fun onLayoutChange(
            v: View?,
            left: Int,
            top: Int,
            right: Int,
            bottom: Int,
            oldLeft: Int,
            oldTop: Int,
            oldRight: Int,
            oldBottom: Int
        ) {
            rulerPopWin.contentView.removeOnLayoutChangeListener(this)
            rulerPopWin.contentView.getGlobalVisibleRect(rulerRect)
            cursorPopWin.contentView.getGlobalVisibleRect(cursorRect)

            val keyboardHeight = rulerRect.bottom - cursorRect.bottom

            if (keyboardHeight != lastKeyboardHeight) {
                if (callbacks.isNotEmpty()) {
                    val cbs = ArrayList<Callback>(callbacks)
                    cbs.forEach {
                        it.onKeyboardHeightChanged(keyboardHeight)
                    }
                    cbs.clear()
                }
                lastKeyboardHeight = keyboardHeight
            }

            if (showDebug) {
                (v as TextView).run {
                    text = "$keyboardHeight"
                    setPadding(0, 0, 0, max(keyboardHeight - this.lineHeight, 0))
                }
            }
        }

    }

    /**
     * Show rulerPopWin after soft keyboard open or close, that makes rulerPopWin can cover on
     * soft keyboard, then you can get a full height PopupWindow.
     * If not do like this, in some devices, rulerPopWin performs like cursorPopWin. Height will change
     * with the soft keyboard open or close.
     */
    private val cursorLayoutChangeListener =
        View.OnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            if (rulerPopWin.isShowing) {
                rulerPopWin.dismiss()
            }
            rulerPopWin.showAtLocation(decorView, Gravity.BOTTOM or Gravity.END, 0, 0)
            rulerPopWin.contentView.addOnLayoutChangeListener(rulerLayoutChangeListener)
        }

    private val callbacks = LinkedList<Callback>()

    fun watch() {
        if (!cursorPopWin.isShowing) {
            cursorPopWin.showAtLocation(decorView, Gravity.BOTTOM or Gravity.END, 0, 0)
            cursorPopWin.contentView.addOnLayoutChangeListener(cursorLayoutChangeListener)
        }
    }

    fun unwatch() {
        cursorPopWin.contentView.removeOnLayoutChangeListener(cursorLayoutChangeListener)
        if (cursorPopWin.isShowing) {
            cursorPopWin.dismiss()
        }
        rulerPopWin.contentView.removeOnLayoutChangeListener(rulerLayoutChangeListener)
        if (rulerPopWin.isShowing) {
            rulerPopWin.dismiss()
        }
    }

    fun addCallback(callback: Callback) {
        if (callbacks.contains(callback)) {
            return
        }
        callbacks.add(callback)
    }

    fun removeCallback(callback: Callback) {
        callbacks.remove(callback)
    }

    private fun makeRulerPopWin(activity: Activity) = PopupWindow(activity).apply {
        contentView = if (showDebug) {
            TextView(activity).apply {
                background = GradientDrawable().apply {
                    this.setStroke(1.dp, Color.LTGRAY)
                }
                gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
                setTextColor(Color.RED)
            }
        } else {
            View(activity)
        }
        setBackgroundDrawable(null)
        width = if (showDebug) 80.dp else 1     // if set to 0, getGlobalVisibleRect will not work
        height = WindowManager.LayoutParams.MATCH_PARENT
        elevation = 0F

        isFocusable = false
        isTouchable = false
        isOutsideTouchable = false
    }
    private fun makeCursorPopWin(activity: Activity) = PopupWindow(activity).apply {
        contentView = if (showDebug) {
            FrameLayout(activity).apply {
                addView(
                    View(activity).apply {
                        background = ColorDrawable(Color.RED)
                    },
                    FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        1.dp,
                        Gravity.BOTTOM
                    )
                )
            }
        } else {
            View(activity)
        }
        setBackgroundDrawable(null)

        width = if (showDebug) 80.dp else 1     // if set to 0, getGlobalVisibleRect will not work
        height = WindowManager.LayoutParams.MATCH_PARENT
        elevation = 0F

        softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        inputMethodMode = PopupWindow.INPUT_METHOD_NEEDED

        isFocusable = false
        isTouchable = false
        isOutsideTouchable = false
    }

    interface Callback {
        fun onKeyboardHeightChanged(height: Int)
    }
}