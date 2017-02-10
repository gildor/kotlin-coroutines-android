package ru.gildor.coroutines.android

import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.experimental.*
import ru.gildor.coroutines.android.Event.Destroy
import java.util.concurrent.TimeUnit
import kotlin.coroutines.experimental.CoroutineContext

object MainThread : CoroutineDispatcher(), Delay {
    val handler = Handler(Looper.getMainLooper())

//    override fun isDispatchNeeded(context: CoroutineContext): Boolean = Looper.myLooper() != Looper.getMainLooper()
/*
   UI dispatchers _should not_ override `isDispatchNeeded`, but leave a default implementation that
   returns `true`. To understand the rationale beyond this recommendation, consider the following code:

   fun asyncUpdateUI(...) = async(MainThread) {
       // do something here that updates something in UI
   }

   When you invoke `asyncUpdateUI` in some background thread, it immediately continues to the next
   line, while UI update happens asynchronously in the main thread. However, if you invoke
   it in the main thread itself, it updates UI _synchronously_ if your `isDispatchNeeded` is
   overridden with a thread check. Checking if we are already in the main thread seems more
   efficient (and it might indeed save a few CPU cycles), but this subtle and context-sensitive
   difference in behavior makes the resulting async code harder to debug.

   Basically, the choice here is between "JS-style" asynchronous approach (async actions
   are always postponed to be executed later in the even dispatch thread) and "C#-style" approach
   (async actions are executed in the invoker thread until the first suspension point).
   While, C# approach seems to be more efficient, it ends up with recommendations like
   "use `yield` if you need to ....". This is error-prone. JS-style approach is more consistent
   and does not require programmers to think about whether they need to yield or not.
*/

    override fun dispatch(context: CoroutineContext, block: Runnable) {
        handler.post(block)
    }

    override fun scheduleResumeAfterDelay(time: Long, unit: TimeUnit, continuation: CancellableContinuation<Unit>) {
//        if (continuation.isCancelled) return
/*
    Continuation must be scheduled even if it is already cancelled, because a cancellation is just
    an exception that the coroutine that used `delay` might wanted to catch and process. It might
    need to close some resources in its `finally` blocks, for example.
*/

        val runnable = Runnable { continuation.resume(Unit) }
/*
    Because `isDispatchNeeded` is not overriden anymore, this `continuation.resume(Unit)` line
    causes an extra dispatch. Upcoming version of `kotlinx.coroutines` (version 0.9) introduces
    a new primitive to enable efficient implementation of such dispatched. The line above will
    need to be replaced with:

        val runnable = Runnable {
            with(continuation) { resumeUndispatched(Unit) }
        }

*/
        handler.postDelayed(runnable, unit.toMillis(time))
        continuation.onCompletion { handler.removeCallbacks(runnable) }
    }
}

/**
 * Starts coroutine with [MainThread] dispatcher that will be canceled  after [cancelEvent] of [CoroutineLifecycle]
 *
 * By default [cancelEvent]
 */
fun CoroutineLifecycle.mainAsync(
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
