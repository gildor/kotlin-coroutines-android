package ru.gildor.coroutines.android.lifecycle

import java.util.*

/**
 * Sync
 */
open class SynchronizedCoroutineLifecycle(val supportedEvents: List<Event> = ALL_EVENTS) : CoroutineLifecycle {
    private val events = Event.values()

    private val listeners = events.map {
        Collections.synchronizedList(ArrayList<Listener>())
    }.run { Collections.synchronizedList(this) }

    override fun removeListener(listener: Listener) {
        synchronized(listeners) {
            for (i in 0 until listeners.size) {
                listeners[i].remove(listener)
            }
        }
    }

    override fun sendEvent(event: Event) {
        val eventIndex = events.indexOf(event)
        synchronized(listeners) {
            //Send event to all listeners that subscribed on previous step of lifecycle,
            //to avoid possible leaks, for example when client subscribed on Pause after Pause
            for (i in 0..eventIndex) {
                for (j in 0 until listeners[i].size) {
                    listeners[i][j].apply {
                        invoke()
                        removeListener(this)
                    }
                }
            }
        }
    }

    override fun addListener(event: Event, listener: Listener) {
        val eventIndex = events.indexOf(event)
        synchronized(listeners) {
            listeners[eventIndex]?.apply {
                if (!contains(listener)) {
                    listeners[eventIndex]?.add(listener)
                }
            }
        }
    }

    override fun isEventSupported(event: Event): Boolean {
        return supportedEvents.contains(event)
    }
}
