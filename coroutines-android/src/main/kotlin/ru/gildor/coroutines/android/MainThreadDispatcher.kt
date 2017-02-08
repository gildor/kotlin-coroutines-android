package ru.gildor.coroutines.android

import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.experimental.*
import ru.gildor.coroutines.android.Event.Destroy
import java.util.concurrent.TimeUnit
import kotlin.coroutines.experimental.CoroutineContext

object MainThread : CoroutineDispatcher(), Delay {
    val handler = Handler(Looper.getMainLooper())

    override fun isDispatchNeeded(context: CoroutineContext): Boolean = Looper.myLooper() != Looper.getMainLooper()
    override fun dispatch(context: CoroutineContext, block: Runnable) {
        handler.post(block)
    }

    override fun scheduleResumeAfterDelay(time: Long, unit: TimeUnit, continuation: CancellableContinuation<Unit>) {
        if (continuation.isCancelled) return
        val runnable = Runnable { continuation.resume(Unit) }
        handler.postDelayed(runnable, unit.toMillis(time))
        continuation.onCompletion { handler.removeCallbacks(runnable) }
    }
}


fun CoroutineLifecycle.async(
        cancelEvent: Event = Destroy,
        context: CoroutineContext? = null,
        block: suspend CoroutineScope.() -> Unit
): Job {
    if (isEventSupported(cancelEvent)) {
        throw IllegalStateException("Cancel event $cancelEvent doesn't supported by CoroutineLifecycle $this")
    }
    val job = launch(if (context == null) MainThread else context + MainThread) {
        block(this)
    }
    val listener: () -> Unit = {
        job.cancel()
    }
    job.onCompletion { removeListener(listener) }
    addListener(Destroy, listener)
    return job
}
