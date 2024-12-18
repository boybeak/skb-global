package com.github.boybeak.skbglobal

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    private val observer by lazy {
        KeyboardObserver.create(this, true)
    }

    private val switchBtn: SwitchCompat by lazy {
        findViewById(R.id.switchBtn)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { v, insets ->
            val imeHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
            Log.d(TAG, "imeHeight=$imeHeight")
            insets
        }

        switchBtn.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                observer.watch()
            } else {
                observer.unwatch()
            }
        }
    }

}