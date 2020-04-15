package com.savvy.youtubeplayer.model

import com.google.gson.annotations.SerializedName

data class DataSearch(
    @SerializedName("nextPageToken")
    var searchNextPageToken: String,
    @SerializedName("pageInfo")
    var searchPageInfo: PageInfoSearch,
    @SerializedName("items")
    var searchListItems: ArrayList<DataSearchYoutubeVideo> = arrayListOf()
)

data class PageInfoSearch(
    @SerializedName("totalResults")
    var searchTotalResults: Int,
    @SerializedName("resultsPerPage")
    var searchResultsPerPage: Int
)


data class DataSearchYoutubeVideo(
    @SerializedName("id")
    val searchId: SearchId,
    @SerializedName("snippet")
    val searchSnippet: SearchSnippet
)

data class SearchId(
    @SerializedName("videoId")
    val searchVideoId: String
)


data class SearchSnippet(
    @SerializedName("description")
    var searchDescription: String,
    @SerializedName("publishedAt")
    var searchPublishAt: String,
    @SerializedName("title")
    var searchTitle: String,
    @SerializedName("channelTitle")
    var searchChannelTitle: String,
    @SerializedName("thumbnails")
    val searchThumbnails: SearchThumbnails
)

data class SearchThumbnails(
    @SerializedName("medium")
    val searchImageMedium: SearchUrlImageMedium
)


data class SearchUrlImageMedium(
    @SerializedName("url")
    val searchUrlImage: String
)
