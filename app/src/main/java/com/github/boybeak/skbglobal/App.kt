package com.github.boybeak.skbglobal

import android.app.Application
import android.util.Log

class App : Application() {

    companion object {
        private const val TAG = "App"
    }

    override fun onCreate() {
        super.onCreate()
        SoftKeyboardGlobal.install(this, true)
        SoftKeyboardGlobal.addSoftKeyboardCallback(object : SoftKeyboardGlobal.SoftKeyboardCallback {
            override fun onOpen(height: Int) {
                Log.d(TAG, "onOpen height=$height")
            }

            override fun onClose() {
                Log.d(TAG, "onClose")
            }
        })
    }
}