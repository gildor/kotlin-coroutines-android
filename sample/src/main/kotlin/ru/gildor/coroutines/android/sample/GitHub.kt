package ru.gildor.coroutines.android.sample

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface GitHub {
    @GET("/users/{owner}/repos")
    fun repositories(@Path("owner") owner: String): Call<List<Repository>>

    @GET("/repos/{owner}/{repo}/branches")
    fun branches(@Path("owner") owner: String, @Path("repo") repo: String): Call<List<Branch>>

    @GET("/repos/{owner}/{repo}/commits")
    fun commits(@Path("owner") owner: String, @Path("repo") repo: String): Call<List<Commit>>
}

data class Repository(
        val name: String,
        val owner: User
)

data class User(
        val login: String
)

data class Branch(
        val name: String
)

data class Commit(
        val sha: String,
        val commit: CommitInfo
)

data class CommitInfo(
        val message: String,
        val author: Author
)

data class Author(
        val name: String,
        val email: String,
        val date: String
)