package ru.gildor.coroutines.android.appcompat

import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.delay
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import ru.gildor.coroutines.android.appcompat.activity.CoroutineAppCompatActivity
import ru.gildor.coroutines.android.lifecycle.launchMain

class CoroutineAppCompatActivityTest {
    @Before
    fun setUp() {

    }

    @Test
    fun onPause() {
        val activity = object : CoroutineAppCompatActivity() {
            lateinit var job: Job
            init {
                onStart()
                onPause()
                Thread.sleep(300)
                onDestroy()
            }

            override fun onStart() {
                super.onStart()
                job = launchMain(cancelOnPause) {
                    while (true) {
                        delay(100)
                    }
                }
                assertTrue(job.isActive)
                assertFalse(job.isCompleted)
            }

            override fun onDestroy() {
                super.onDestroy()
                assertFalse(job.isActive)
                assertTrue(job.isCompleted)
            }
        }
    }

    @Test
    fun onStop() {
    }

    @Test
    fun onDestroy() {
    }

    class MockActivity(
            val onPause: () -> Unit,
            val onStop: () -> Unit,
            val onDestroy: () -> Unit
    ) : CoroutineAppCompatActivity() {
        override fun onPause() {

            super.onPause()
        }

        override fun onStop() {
            super.onStop()
        }

        override fun onDestroy() {
            super.onDestroy()
        }
    }

}