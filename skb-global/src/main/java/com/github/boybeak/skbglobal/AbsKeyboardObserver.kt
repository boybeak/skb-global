package com.github.boybeak.skbglobal

import java.util.LinkedList

internal abstract class AbsKeyboardObserver : IKeyboardObserver {

    private val callbacks = LinkedList<IKeyboardObserver.Callback>()

    override fun addCallback(callback: IKeyboardObserver.Callback) {
        if (callbacks.contains(callback)) {
            return
        }
        callbacks.add(callback)
    }

    override fun removeCallback(callback: IKeyboardObserver.Callback) {
        callbacks.remove(callback)
    }

    internal fun safeNotifyCallbacks(keyboardHeight: Int) {
        if (callbacks.isNotEmpty()) {
            val cbs = ArrayList<IKeyboardObserver.Callback>(callbacks)
            cbs.forEach {
                it.onKeyboardHeightChanged(keyboardHeight)
            }
            cbs.clear()
        }
    }
}