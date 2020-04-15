package com.savvy.savvymusic.ultis

object APIUtils {
        const val BASE_URL = "https://www.googleapis.com/youtube/v3/"

    val dataClient: DataClient
        get() = RetrofitClient.getClient(BASE_URL).create(DataClient::class.java)
}
