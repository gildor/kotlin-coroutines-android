# Coroutines Android for AppCompat 

Library contains implementation of `AppCompatActivity` and `android.support.v4.app.Fragment` with CoroutinesLifecycle

If you want to use `CoroutinesLifecycle.asyncMain {}` just extend your activity from `CoroutineAppCompatActivity` and your fragment from `CoroutineAppCompatFragment`.

If you don't want to add additional layer to your Activity/Fragment hierarchy please check [README of coroutines-android](../coroutines-android/README.md#How to use) 

[Sample of usage](../sample/src/main/kotlin/ru/gildor/coroutines/android/sample/MainActivity.kt)