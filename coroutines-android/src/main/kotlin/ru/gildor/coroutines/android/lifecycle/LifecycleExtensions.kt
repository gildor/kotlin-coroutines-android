package ru.gildor.coroutines.android.lifecycle

import kotlinx.coroutines.experimental.*
import ru.gildor.coroutines.android.dispatcher.MainThread
import ru.gildor.coroutines.android.lifecycle.Event.Destroy
import kotlin.coroutines.experimental.AbstractCoroutineContextElement
import kotlin.coroutines.experimental.CoroutineContext

/**
 * Starts coroutine with [MainThread] dispatcher that will be canceled  after [cancelEvent] of [CoroutineLifecycle]
 *
 * By default [cancelEvent]
 */
fun CoroutineLifecycle.launchLife(
        context: CoroutineContext,
        start: Boolean = true,
        block: suspend CoroutineScope.() -> Unit
): Job {
    return launch(context + LifecycleJob(this, context), start) {
        block()
    }
}

fun <T> CoroutineLifecycle.asyncLife(
        context: CoroutineContext,
        start: Boolean = true,
        block: suspend CoroutineScope.() -> T
): Deferred<T> {
    return async(context + LifecycleJob(this, context), start) {
        block()
    }
}

/**
 * Lifecycle-aware version of [run]
 *
 * You can use [run]
 */
suspend fun <T> CoroutineLifecycle.runLife(
        context: CoroutineContext,
        block: suspend CoroutineScope.() -> T
): T {
    return run(context + LifecycleJob(this, context)) {
        block()
    }
}

open class CancelEvent(val event: Event) : AbstractCoroutineContextElement(CancelEvent) {
    companion object Key : CoroutineContext.Key<CancelEvent>
    override fun toString(): String = "CancelOn($event)"
}

class LifecycleJob(
        lifecycle: CoroutineLifecycle,
        context: CoroutineContext? = null,
        cancelOn: Event = context?.get(CancelEvent)?.event ?: Destroy,
        val job: Job = context?.get(Job) ?: Job()
) : Job by job {
    init {
        if (!lifecycle.isEventSupported(cancelOn)) {
            throw IllegalStateException("Cancel event $cancelOn doesn't supported by CoroutineLifecycle ${this}")
        }

        val listener: Listener = { job.cancel() }
        lifecycle.addListener(cancelOn, listener)
        job.invokeOnCompletion { lifecycle.removeListener(listener) }
    }
}
