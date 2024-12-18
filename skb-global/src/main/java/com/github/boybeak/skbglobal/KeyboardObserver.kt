package com.github.boybeak.skbglobal

import android.app.Activity
import android.os.Build
import android.util.Log

class KeyboardObserver private constructor (
    activity: Activity,
    private val showDebug: Boolean = false,
    private val forceLegacy: Boolean = false
) : IKeyboardObserver by makeWorker(activity, showDebug, forceLegacy) {

    companion object {

        private const val TAG = "KeyboardObserver"

        fun create(activity: Activity, showDebug: Boolean = false, forceLegacy: Boolean = false): KeyboardObserver {
            return KeyboardObserver(activity, showDebug, forceLegacy)
        }

        private fun makeWorker(activity: Activity, showDebug: Boolean, forceLegacy: Boolean): IKeyboardObserver {
            return if (forceLegacy) {
                KeyboardObserverLegacyImpl(activity, showDebug)
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    Log.d(TAG, "makeWorker R")
                    KeyboardObserverRImpl(activity, showDebug)
//                    KeyboardObserverLegacyImpl(activity, showDebug)
                } else {
                    Log.d(TAG, "makeWorker Legacy ${Build.VERSION.SDK_INT}")
                    KeyboardObserverLegacyImpl(activity, showDebug)
                }
            }
        }
    }
}