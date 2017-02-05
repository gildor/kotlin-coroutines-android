package ru.gildor.coroutines.android.sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.gildor.coroutines.android.MainThread
import ru.gildor.coroutines.retrofit.HttpException
import ru.gildor.coroutines.retrofit.await
import ru.gildor.sample.R

class MainActivity : AppCompatActivity() {

    private var job: Job? = null
    private var paused = false

    private val github by lazy {
        val retrofit = Retrofit.Builder()
                .baseUrl("https://api.github.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        retrofit.create(GitHub::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        cancel.setOnClickListener {
            if (job?.isActive == true) {
                job?.cancel()
                cancel.setText(R.string.load)
            } else {
                loadCommits()
                cancel.setText(R.string.cancel)
            }
        }
        pause.setOnClickListener {
            paused = !paused
            pause.setText(if (paused) R.string.resume else R.string.pause)
        }

        loadCommits()
    }

    private fun loadCommits() {
        job = launch(MainThread) {
            progress.visibility = View.VISIBLE
            log("Request repos")
            try {
                val repos = github.repositories("kotlin").await()
                log("Load repositories last commit")
                for ((name, owner) in repos) {
                    while (paused) delay(100)
                    val lastCommit = github.commits(owner.login, name).await().first().commit
                    log("[$name] ${lastCommit.author.name}\n${lastCommit.message}")
                    delay(1000)
                }
            } catch (e: HttpException) {
                log("Loading exception: ${e.message}")
                cancel.setText(R.string.load)
            }
            progress.visibility = View.INVISIBLE
            cancel.setText(R.string.load)
        }
    }

    private fun log(message: String) {
        logs.append(message + "\n")
    }

    override fun onDestroy() {
        super.onDestroy()
        //We must cancel job here, otherwise Activity will leak
        job?.cancel()
    }
}

