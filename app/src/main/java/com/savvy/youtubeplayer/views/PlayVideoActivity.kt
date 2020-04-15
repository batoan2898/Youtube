package com.savvy.youtubeplayer.views

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.savvy.youtubeplayer.Constants
import com.savvy.youtubeplayer.R
import com.savvy.youtubeplayer.model.YoutubeVideo
import kotlinx.android.synthetic.main.activity_play_video.*



class PlayVideoActivity : YouTubeBaseActivity(), YouTubePlayer.OnInitializedListener{

    var id: String = ""
    var listIdVideo: ArrayList<String> = arrayListOf()
    var listTitleVideo: ArrayList<String> = arrayListOf()
    var listChannelVideo: ArrayList<String> = arrayListOf()
    var listDescriptionVideo: ArrayList<String> = arrayListOf()
    var position: Int = 0
    var youtubePlayer: YouTubePlayer? = null
    var isFullscreen: Boolean = false

    companion object {
        private var REQUEST_CODE_VIDEO: Int = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_video)
        getListVideo()
        videoPlayer.initialize(Constants.Key.YOUTUBE_API_KEY, this)

    }



    private fun getListVideo() {
        position = intent.getSerializableExtra(Constants.Key.INTENT_POSITION_VIDEO) as Int
        val listVideo =
            intent.getSerializableExtra(Constants.Key.INTENT_LIST_VIDEO) as ArrayList<YoutubeVideo>
        for (i in 0 until listVideo.size) {
            listIdVideo.add(listVideo[i].videoId)
            listChannelVideo.add(listVideo[i].channel)
            listTitleVideo.add(listVideo[i].title)
            listDescriptionVideo.add(listVideo[i].description)
        }
    }


    override fun onInitializationSuccess(
        p0: YouTubePlayer.Provider?,
        p1: YouTubePlayer?,
        p2: Boolean
    ) {
        youtubePlayer = p1
        youtubePlayer?.loadVideos(listIdVideo,position,0)
        changeVideo(position)
        youtubePlayer?.setPlaylistEventListener(object : YouTubePlayer.PlaylistEventListener {
            override fun onPlaylistEnded() {
                finishActivity(REQUEST_CODE_VIDEO)
            }

            override fun onPrevious() {
                if (position ==0) return
                position--
                changeVideo(position)
            }

            override fun onNext() {
                if (position == listTitleVideo.size -1) return
                position++
                changeVideo(position)
                Log.e("toan",tvTitlePlay.text.toString())
            }
        })

        youtubePlayer?.setOnFullscreenListener { p0 -> isFullscreen = p0 }

    }

    override fun onBackPressed() {
        youtubePlayer
        if (youtubePlayer != null && isFullscreen) {
            youtubePlayer!!.setFullscreen(false)

        } else {
            super.onBackPressed()
        }
    }


    override fun onInitializationFailure(
        p0: YouTubePlayer.Provider?,
        p1: YouTubeInitializationResult?
    ) {
        if (p1!!.isUserRecoverableError) {
            p1.getErrorDialog(this, REQUEST_CODE_VIDEO)
        } else {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_VIDEO) {
            videoPlayer.initialize(Constants.Key.YOUTUBE_API_KEY, this)

        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun changeVideo(position: Int){
        tvTitlePlay.text = listTitleVideo[position]
        tvChannelPlay.text = listChannelVideo[position]
        tvDescriptionPlay.text = listDescriptionVideo[position]
    }
}
