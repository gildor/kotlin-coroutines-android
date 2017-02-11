package ru.gildor.coroutines.android.lifecycle.activity

import android.support.v7.app.AppCompatActivity
import ru.gildor.coroutines.android.lifecycle.CoroutineLifecycle
import ru.gildor.coroutines.android.lifecycle.Event
import ru.gildor.coroutines.android.lifecycle.createLifecycle

open class CoroutineAppCompatActivity : AppCompatActivity(), CoroutineLifecycle by createLifecycle() {
    override fun onPause() {
        super.onPause()
        sendEvent(Event.Pause)
    }

    override fun onStop() {
        super.onStop()
        sendEvent(Event.Stop)
    }

    override fun onDestroy() {
        super.onDestroy()
        sendEvent(Event.Destroy)
    }
}
