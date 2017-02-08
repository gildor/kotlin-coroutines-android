# WIP: Kotlin Coroutines for Android
Experimental implementation of [Kotlin Coroutines](https://github.com/Kotlin/kotlin-coroutines/blob/master/kotlin-coroutines-informal.md) for Android 

## coroutines-android
Provides MainThread coroutine context to dispatch coroutine invocation in main thread

Now you must handle activity onStop/onDestroy callbacks manually, to avoid memory leaks.

TODO: Provide automatic Activity life-cycle management

## coroutines-retrofit
Suspendable await extension for [Retrofit](https://github.com/square/retrofit) callbacks class [Call](https://square.github.io/retrofit/2.x/retrofit/retrofit2/Call.html)

TODO: Additional extension functions awaitOrNull() and awaitOrDefault()

## How to use
[Sample of usage](sample/src/main/kotlin/ru/gildor/coroutines/android/sample/MainActivity.kt)