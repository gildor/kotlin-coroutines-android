package ru.gildor.coroutines.android.sample

import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Url

interface GitHub {
    @GET("/users/{owner}/repos")
    fun repositories(@Path("owner") owner: String): Call<List<Repository>>

    @GET("/users/{username}")
    fun user(@Path("username") username: String): Call<User>

    @GET("/repos/{owner}/{repo}/branches")
    fun branches(@Path("owner") owner: String, @Path("repo") repo: String): Call<List<Branch>>

    @GET("/repos/{owner}/{repo}/commits")
    fun commits(@Path("owner") owner: String, @Path("repo") repo: String): Call<List<Commit>>

    @GET
    fun image(@Url url: String): Call<RequestBody>

}


data class Repository(val name: String, val owner: User)
data class User(val login: String, val name: String)
data class Branch(val name: String)
data class Commit(val sha: String, val commit: CommitInfo)
data class CommitInfo(val message: String, val author: Author)
data class Author(val name: String, val email: String, val date: String)