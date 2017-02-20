package ru.gildor.coroutines.android.lifecycle

import android.os.Looper
import kotlinx.coroutines.experimental.*
import org.junit.Assert.*
import org.junit.Test

class AndroidLifecycleExtensionsTest {
    @Test
    fun asyncMainLooperCheck() {
        val mainLooper = Looper.getMainLooper()
        createLifecycle().apply {
            runBlocking {
                asyncMain {
                    assertEquals(mainLooper, Looper.myLooper())
                    val theAnswer = launch(CommonPool) {
                        assertNotEquals(mainLooper, Looper.myLooper())
                    }
                    assertEquals(mainLooper, Looper.myLooper())
                    assertEquals(42, theAnswer)
                }.await()
            }
        }
    }

    @Test
    fun runMainLooperCheck() {
        val mainLooper = Looper.getMainLooper()
        createLifecycle().apply {
            runBlocking {
                launch(CommonPool) {
                    runMain {
                        assertEquals(mainLooper, Looper.myLooper())
                        val theAnswer = launch(CommonPool) {
                            assertNotEquals(mainLooper, Looper.myLooper())
                        }
                        assertEquals(mainLooper, Looper.myLooper())
                        assertEquals(42, theAnswer)
                    }
                }
                fail()
            }
        }
    }

    @Test
    fun mainAsyncCancel() {
        createLifecycle().apply {
            var childJob: Job? = null
            val job = asyncMain {
                childJob = launch(CommonPool) {
                    while (true) {
                        delay(100)
                    }
                }
            }
            sendEvent(Event.Pause)
            assertTrue(job.isActive)
            sendEvent(Event.Stop)
            assertTrue(job.isActive)
            sendEvent(Event.Destroy)
            assertFalse(job.isActive)
            assertFalse(job.isCompleted)
            assertTrue(childJob!!.isCompleted)
            assertEquals(CancellationException::class, childJob!!.getCompletionException()::class)
        }
    }
}