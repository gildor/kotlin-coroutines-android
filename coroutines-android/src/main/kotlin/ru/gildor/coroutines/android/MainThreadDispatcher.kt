package ru.gildor.coroutines.android

import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.experimental.CancellableContinuation
import kotlinx.coroutines.experimental.CoroutineDispatcher
import kotlinx.coroutines.experimental.Delay
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
