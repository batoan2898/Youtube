package com.savvy.savvymusic.ultis

import com.savvy.youtubeplayer.model.Data
import com.savvy.youtubeplayer.model.DataSearch
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface DataClient {

    @GET("playlistItems?part=snippet")
    fun firstSelectData(
        @Query("key") key: String,
        @Query("playlistId") id: String,
        @Query("maxResults") maxResult: Int?
    ): Call<Data>

    @GET("playlistItems?part=snippet")
    fun selectData(
        @Query("key") key: String,
        @Query("playlistId") id: String,
        @Query("maxResults") maxResult: Int?,
        @Query("pageToken") pageToken: String
    ): Call<Data>


    @GET("search?part=snippet")
    fun searchData(
        @Query("key") key: String,
        @Query("q") keySearch: String,
        @Query("type") video: String,
        @Query("maxResults") maxResult: Int?
    ): Call<DataSearch>
}


