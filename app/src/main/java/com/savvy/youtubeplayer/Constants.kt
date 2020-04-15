package com.savvy.youtubeplayer


class Constants {
    interface Key {
        companion object {
            val YOUTUBE_API_KEY2: String = "AIzaSyAGj9MqijpjFl1zHASTKQ8NSvy4ooSSbu8"
            val YOUTUBE_API_KEY1: String = "AIzaSyAquqYr0eyQYEH62cvja9Ws0xdFF-mStWw"
            val YOUTUBE_API_KEY: String = "AIzaSyCFUoePZKJadLO1HAQUBJMESB7V0vT-h9Y"
            val GOOGLE_API_KEY: String = "AIzaSyAazMT008mJ78jt0yceOwDiLlmHDIYGogw"
            val ID_PLAYLIST = "PLMC9KNkIncKtPzgY-5rmhvj7fax8fdxoj"
            val URL_GET_JSON: String =
                "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&playlistId=" + ID_PLAYLIST + "&key=" + YOUTUBE_API_KEY
            val INTENT_POSITION_VIDEO ="position-video"
            val INTENT_LIST_VIDEO = "list-video"
        }
    }

}