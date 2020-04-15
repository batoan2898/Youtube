package com.savvy.youtubeplayer

import android.app.Application
import com.savvy.youtubeplayer.data.MySharedPreferences

class YoutubePlayerApp : Application() {
    override fun onCreate() {
        super.onCreate()
        MySharedPreferences.init(this)
    }
}