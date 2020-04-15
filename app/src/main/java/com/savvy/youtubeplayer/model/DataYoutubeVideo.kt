package com.savvy.youtubeplayer.model

import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("nextPageToken")
    var nextPageToken: String,
    @SerializedName("pageInfo")
    var pageInfo: PageInfo,
    @SerializedName("items")
    var listItems: ArrayList<DataYoutubeVideo> = arrayListOf()
)

data class PageInfo(
    @SerializedName("totalResults")
    var totalResults: Int,
    @SerializedName("resultsPerPage")
    var resultsPerPage: Int
)


data class DataYoutubeVideo(
    @SerializedName("snippet")
    val snippet: Snippet
)


data class Snippet(
    @SerializedName("description")
    var description: String,
    @SerializedName("publishedAt")
    var publishAt: String,
    @SerializedName("title")
    var title: String,
    @SerializedName("channelTitle")
    var channelTitle: String,
    @SerializedName("thumbnails")
    val thumbnails: Thumbnails,
    @SerializedName("resourceId")
    val resourceId: ResourceId
)

data class Thumbnails(
    @SerializedName("medium")
    val imageMedium: UrlImageMedium
)

data class ResourceId(
    @SerializedName("videoId")
    val videoId: String
)

data class UrlImageMedium(
    @SerializedName("url")
    val urlImage: String
)
