package ru.gildor.coroutines.android

typealias Listener = () -> Unit

interface CoroutineLifecycle {
    fun sendEvent(event: Event)

    fun addListener(event: Event, listener: Listener)

    fun removeListener(listener: Listener)

    fun isEventSupported(event: Event): Boolean
}

enum class Event {
    Pause,
    Stop,
    Destroy
}

val ALL_EVENTS = Event.values().toList()

/**
 * Creates default coroutine lifecycle implementation
 */
fun createLifecycle(): CoroutineLifecycle = SynchronizedCoroutineLifecycle()