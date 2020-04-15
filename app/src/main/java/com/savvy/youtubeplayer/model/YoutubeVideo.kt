package com.savvy.youtubeplayer.model

import java.io.Serializable

data class YoutubeVideo(
    val title: String,
    val description: String,
    val publishAt: String,
    val channel: String,
    val url: String,
    val videoId: String
) : Serializable{
    constructor(): this("","","","","","")

}
