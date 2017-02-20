package ru.gildor.coroutines.android.lifecycle

import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.Job
import ru.gildor.coroutines.android.dispatcher.MainThread
import kotlin.coroutines.experimental.CoroutineContext

fun CoroutineLifecycle.launchMain(
        context: CoroutineContext = cancelOnDestroy,
        start: Boolean = true,
        block: suspend CoroutineScope.() -> Unit
): Job {
    return launchLife(context + MainThread, start, block)
}

fun <T> CoroutineLifecycle.asyncMain(
        context: CoroutineContext = cancelOnDestroy,
        start: Boolean = true,
        block: suspend CoroutineScope.() -> T
): Deferred<T> {
    return asyncLife(context + MainThread, start, block)
}

suspend fun <T> CoroutineLifecycle.runMain(
        context: CoroutineContext = cancelOnDestroy,
        block: suspend CoroutineScope.() -> T
): T {
    return runLife(context + MainThread, block)
}