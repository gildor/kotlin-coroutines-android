package ru.gildor.coroutines.android.dispatcher

import android.os.Looper
import kotlinx.coroutines.experimental.Unconfined
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking
import org.junit.Assert.*
import org.junit.Test

class MainThreadDispatcherTest {
    @Test
    fun runInMainThread() {
        runBlocking {
            //Check for main thread inside block without MainThread
            async(Unconfined) {
                assertNotEquals(Looper.getMainLooper(), Looper.myLooper())
            }.await()

            //Check the same coroutine with MainThread
            async(MainThread) {
                assertEquals(Looper.getMainLooper(), Looper.myLooper())
            }.await()
        }
    }
}