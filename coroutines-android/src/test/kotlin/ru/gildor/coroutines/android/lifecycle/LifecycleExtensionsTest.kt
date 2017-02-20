package ru.gildor.coroutines.android.lifecycle

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Unconfined
import kotlinx.coroutines.experimental.delay
import org.junit.Assert.*
import org.junit.Test

class LifecycleExtensionsTest {
    @Test
    fun launchLife() {
        createLifecycle().apply {
            launchLife(Unconfined) {

            }
        }
    }

    @Test
    fun lifecycleEventsStart() {
        createLifecycle().apply {
            val launch = launchLife(CommonPool, false) {
                while (true) delay(100)
            }
            assertFalse(launch.isActive)
            assertFalse(launch.isCompleted)
            assertTrue(launch.start())
            assertTrue(launch.isActive)

            val async = asyncLife(CommonPool, false) {
                while (true) delay(100)
            }
            assertFalse(async.isActive)
            assertFalse(async.isCancelled)
            assertTrue(async.start())
            assertTrue(launch.isActive)

            sendEvent(Event.Destroy)

            assertTrue(launch.isCompleted)
            assertTrue(async.isCompletedExceptionally)
        }
    }

    @Test
    fun asyncLife() {
    }

    @Test
    fun runLife() {
    }

}