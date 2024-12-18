package com.github.boybeak.skbglobal

interface IKeyboardObserver {
    val isWatching: Boolean
    fun watch(notifyNow: Boolean = false)
    fun unwatch()

    fun addCallback(callback: Callback)

    fun removeCallback(callback: Callback)

    interface Callback {
        fun onKeyboardHeightChanged(height: Int)
    }
}