package com.savvy.youtubeplayer


class Constants {
    interface Key {
        companion object {
            val YOUTUBE_API_KEY2: String = "AIzaSyCQ4iyP06Z4dw9RGyU-AJodm02-ztc9zI8"
            val YOUTUBE_API_KEY1: String = "AIzaSyAquqYr0eyQYEH62cvja9Ws0xdFF-mStWw"
            val YOUTUBE_API_KEY: String = "AIzaSyCFUoePZKJadLO1HAQUBJMESB7V0vT-h9Y"
            val YOUTUBE_API_KEY3: String = "AIzaSyCIJGJunxgfgZb9nAu04kkCSYZ5wmBMdk4"
            val YOUTUBE_API_KEY4: String = "AIzaSyDQS80DT5v_nqMA-bi_ymJ2HZOD5ifGTHA"
            val GOOGLE_API_KEY: String = "AIzaSyAazMT008mJ78jt0yceOwDiLlmHDIYGogw"
            val ID_PLAYLIST = "PLMC9KNkIncKtPzgY-5rmhvj7fax8fdxoj"
            val ID_PLAYLIST2 = "PLPvHkQHH8sf9R_XL6ScR0dLcC7O4gxMru"
            val URL_GET_JSON: String =
                "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&playlistId=" + ID_PLAYLIST + "&key=" + YOUTUBE_API_KEY
            val INTENT_POSITION_VIDEO ="position-video"
            val INTENT_LIST_VIDEO = "list-video"
            var arrayPlaylist = arrayOf<String>(ID_PLAYLIST, ID_PLAYLIST2,"PLOHoVaTp8R7dfrJW5pumS0iD_dhlXKv17","PLfTkx5YqwQNqsPhFyoKWDAb9Asshgd7K1","PLmZTDWJGfRq2dxTYAAw43AQkkITkTODaa","PLOwSo8kHs4XSY5L-HKoYN6lBa-VyOyckR")

        }
    }

}