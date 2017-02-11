package ru.gildor.coroutines.android.lifecycle

import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.launch
import ru.gildor.coroutines.android.dispatcher.MainThread
import kotlin.coroutines.experimental.CoroutineContext
import kotlin.coroutines.experimental.EmptyCoroutineContext

/**
 * Starts coroutine with [MainThread] dispatcher that will be canceled  after [cancelEvent] of [CoroutineLifecycle]
 *
 * By default [cancelEvent]
 */
fun CoroutineLifecycle.mainAsync(
        cancelEvent: Event = Event.Destroy,
        context: CoroutineContext = EmptyCoroutineContext,
        block: suspend CoroutineScope.() -> Unit
): Job {
    if (!isEventSupported(cancelEvent)) {
        throw IllegalStateException("Cancel event $cancelEvent doesn't supported by CoroutineLifecycle $this")
    }
    // Add MainThread dispatcher to context,
    // even if context contains dispatcher we will replace it
    val job = launch(context + MainThread) {
        block(this)
    }
    // Lifecycle cancel listener
    val listener: Listener = { job.cancel() }
    addListener(Event.Destroy, listener)
    job.onCompletion { removeListener(listener) }
    return job
}
