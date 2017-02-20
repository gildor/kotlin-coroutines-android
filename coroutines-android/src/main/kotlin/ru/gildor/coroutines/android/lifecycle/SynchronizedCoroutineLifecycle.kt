package ru.gildor.coroutines.android.lifecycle

import java.util.*

internal class SynchronizedCoroutineLifecycle(val supportedEvents: List<Event> = ALL_EVENTS) : CoroutineLifecycle {
    private val events = Event.values()

    private val listeners = events.map {
        Collections.synchronizedList(ArrayList<Listener>())
    }.run { Collections.synchronizedList(this) }

    override fun removeListener(listener: Listener) {
        for (i in 0 until listeners.size) {
            synchronized(listeners[i]) {
                listeners[i].remove(listener)
            }
        }
    }

    override fun sendEvent(event: Event) {
        //Send event to all listeners that subscribed on previous step of lifecycle,
        //to avoid possible leaks, for example when client subscribed on Pause after Pause
        val eventIndex = events.indexOf(event)
        for (i in 0..eventIndex) {
            for (j in 0 until listeners[i].size) {
                //Copy collection before invoke listener, because callback
                synchronized(listeners[i]) {
                    listeners[i].toList().forEach { it.invoke() }
                }
            }
        }
    }

    override fun addListener(event: Event, listener: Listener) {
        val eventIndex = events.indexOf(event)
        for (i in 0 until listeners.size) {
            synchronized(listeners[i]) {
                listeners[i].remove(listener)
                if (i == eventIndex) {
                    listeners[eventIndex].add(listener)
                }
            }
        }
    }

    override fun isEventSupported(event: Event): Boolean {
        return supportedEvents.contains(event)
    }

    override val cancelOnPause = CancelEvent(Event.Pause)
    override val cancelOnStop = CancelEvent(Event.Stop)
    override val cancelOnDestroy = CancelEvent(Event.Destroy)
}
