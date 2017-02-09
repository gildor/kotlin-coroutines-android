package ru.gildor.coroutines.android.fragment

import android.app.Fragment
import ru.gildor.coroutines.android.CoroutineLifecycle
import ru.gildor.coroutines.android.Event
import ru.gildor.coroutines.android.createLifecycle

open class CoroutineFragment : Fragment(), CoroutineLifecycle by createLifecycle() {
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