package ru.gildor.coroutines.android

import org.junit.Assert.*
import org.junit.Test
import ru.gildor.coroutines.android.lifecycle.CoroutineLifecycle
import ru.gildor.coroutines.android.lifecycle.Event.*
import ru.gildor.coroutines.android.lifecycle.Listener
import ru.gildor.coroutines.android.lifecycle.SynchronizedCoroutineLifecycle
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

class CoroutineLifecycleTest {
    @Test
    fun removeListener() = testListener {
        var counter = 0
        val listener: Listener = {
            counter++
        }
        addListener(Pause, listener)
        removeListener(listener)
        sendEvent(Pause)
        sendEvent(Stop)
        sendEvent(Destroy)
        assertEquals(0, counter)
    }

    @Test
    fun sendEvent() = testListener {
        var called = false
        addListener(Destroy) {
            called = true
        }

        sendEvent(Pause)
        assertFalse(called)

        sendEvent(Stop)
        assertFalse(called)

        sendEvent(Destroy)
        assertTrue(called)
    }

    @Test
    fun addListener() = testListener {
        var counter = 0
        val listener: Listener = { counter++ }
        addListener(Pause, listener)
        addListener(Pause, listener)
        addListener(Pause, listener)
        addListener(Pause, listener)
        sendEvent(Pause)
        assertEquals(1, counter)

        addListener(Pause, listener)
        addListener(Stop, listener)
        addListener(Destroy, listener)
        sendEvent(Destroy)
        assertEquals(2, counter)
    }


    @Test
    @Throws(Exception::class)
    fun concurrentStressEvents() = testListener {
        val latch = CountDownLatch(50000)
        val listener1: Listener = {}
        val listener2: Listener = {}
        val listener3: Listener = {}
        val events = values()
        latch.repeat(10) {
            thread(true) {
                latch.repeat(1000) { addListener(Destroy, listener1) }
            }

            thread(true) {
                latch.repeat(1000) { addListener(Pause, listener2) }
            }

            thread(true) {
                latch.repeat(1000) { addListener(Stop, listener3) }
            }

            thread(true) {
                latch.repeat(1000) { sendEvent(events[it % 2]) }
            }

            thread(true) {
                latch.repeat(1000) { removeListener(listener3) }
            }
        }

        latch.await(2, TimeUnit.SECONDS)
        assertEquals("Concurrent thread exception", 0, latch.count)

    }

    private fun testListener(test: CoroutineLifecycle.() -> Unit) {
        SynchronizedCoroutineLifecycle().apply {
            test(this)
        }
    }

    inline private fun CountDownLatch.repeat(cnt: Int, block: (Int) -> Unit) {
        kotlin.repeat(cnt) {
            block(it)
            countDown()
        }
    }
}