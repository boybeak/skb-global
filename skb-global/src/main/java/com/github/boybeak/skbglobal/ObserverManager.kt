package com.github.boybeak.skbglobal

import android.app.Activity
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle
import android.view.View
import android.view.View.OnLayoutChangeListener
import com.github.boybeak.skbglobal.KeyboardObserver.Companion.create
import java.util.WeakHashMap

internal class ObserverManager : ActivityLifecycleCallbacks {

    private val observersMap by lazy {
        WeakHashMap<Activity, KeyboardObserver>()
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        val observer = create(activity, SoftKeyboardGlobal.isDebug)
        observer.addCallback(SoftKeyboardGlobal)
        observersMap[activity] = observer
    }

    override fun onActivityStarted(activity: Activity) {}

    override fun onActivityResumed(activity: Activity) {
        // avoid bad window token problem
        activity.window.decorView.addOnLayoutChangeListener(object : OnLayoutChangeListener {
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
                v?.removeOnLayoutChangeListener(this)
                observersMap[activity]?.watch()
            }
        })
    }

    override fun onActivityPaused(activity: Activity) {
        observersMap[activity]?.unwatch()
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
        val observer = observersMap.remove(activity)
        observer?.removeCallback(SoftKeyboardGlobal)
    }
}
