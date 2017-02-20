package ru.gildor.coroutines.android.lifecycle

typealias Listener = () -> Unit

interface CoroutineLifecycle {
    fun sendEvent(event: Event)

    fun addListener(event: Event, listener: Listener)

    fun removeListener(listener: Listener)

    fun isEventSupported(event: Event): Boolean

    val cancelOnPause: CancelEvent
    val cancelOnStop: CancelEvent
    val cancelOnDestroy: CancelEvent

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
fun createLifecycle(supportedEvents: List<Event> = ALL_EVENTS): CoroutineLifecycle =
        SynchronizedCoroutineLifecycle(supportedEvents)