package com.savvy.youtubeplayer.views

import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.loadOrCueVideo
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.savvy.youtubeplayer.Constants
import com.savvy.youtubeplayer.R
import com.savvy.youtubeplayer.model.YoutubeVideo
import kotlinx.android.synthetic.main.activity_play_video.*


class PlayVideoActivity : AppCompatActivity() {

    var id: String = ""
    var listIdVideo: ArrayList<String> = arrayListOf()
    var listTitleVideo: ArrayList<String> = arrayListOf()
    var listChannelVideo: ArrayList<String> = arrayListOf()
    var listDescriptionVideo: ArrayList<String> = arrayListOf()
    var position: Int = 0
    var youtubePlayerVideo: com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer? =
        null
    var isCreate: Boolean = false
    var supportsPIP : Boolean = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_video)
        getListVideo()
        initYouTubePlayerView()
        supportsPIP = packageManager.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)
    }

    private fun initYouTubePlayerView() {
        videoPlayer.enableBackgroundPlayback(true)
        initPictureInPicture(videoPlayer)
        startVideo(position)
        customAction()

    }

    private fun customAction() {
        videoPlayer.getPlayerUiController().showCustomAction1(true)
        videoPlayer.getPlayerUiController().showCustomAction2(true)
        PlayVideoActivity().isChild

        videoPlayer.getPlayerUiController()
            .setCustomAction1(getDrawable(R.drawable.ic_skip_previous)!!, View.OnClickListener {
                if (position == 0) {
                    videoPlayer.getPlayerUiController().showCustomAction1(false)
                } else {
                    position--
                    startVideo(position)
                }
            })

        videoPlayer.getPlayerUiController()
            .setCustomAction2(getDrawable(R.drawable.ic_skip_next)!!, View.OnClickListener {
                if (position == listIdVideo.size - 1) {
                    videoPlayer.getPlayerUiController().showCustomAction2(false)
                } else {
                    position++
                    startVideo(position)
                }
            })


    }


    private fun startVideo(position: Int) {

        if (!isCreate) {
            videoPlayer.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                override fun onReady(youTubePlayer: com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer) {
                    youTubePlayer.loadOrCueVideo(
                        lifecycle,
                        listIdVideo[position], 0f
                    )
                    youtubePlayerVideo = youTubePlayer
                    isCreate = true
                }
            })
        } else {
            youtubePlayerVideo?.loadOrCueVideo(lifecycle, listIdVideo[position], 0f)
        }

        changeVideo(position)

    }

    override fun onPause() {
        videoPlayer.release()
        super.onPause()
    }

    private fun initPictureInPicture(youTubePlayerView: YouTubePlayerView) {
        val pictureInPictureIcon = ImageView(this)
        pictureInPictureIcon.setImageDrawable(
            ContextCompat.getDrawable(
                this,
                R.drawable.ic_picture_in_picture
            )
        )

        pictureInPictureIcon.setOnClickListener { view ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                if (supportsPIP)
                    enterPictureInPictureMode()

            } else {
                AlertDialog.Builder(this)
                    .setTitle("Can't enter picture in picture mode")
                    .setMessage("In order to enter picture in picture mode you need a SDK version >= N.")
                    .show()
            }
        }
        youTubePlayerView.getPlayerUiController().addView(pictureInPictureIcon)

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

    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration
    ) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)

        if (isInPictureInPictureMode) {
            videoPlayer.enterFullScreen()
            videoPlayer.getPlayerUiController().showUi(false)
        } else {
            videoPlayer.exitFullScreen()
            videoPlayer.getPlayerUiController().showUi(true)
        }
    }

    fun changeVideo(position: Int) {
        tvTitlePlay.text = listTitleVideo[position]
        tvChannelPlay.text = listChannelVideo[position]
        tvDescriptionPlay.text = listDescriptionVideo[position]
    }

    override fun onBackPressed() {
        if (videoPlayer.isFullScreen()) {
            videoPlayer.exitFullScreen()
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                if (supportsPIP)
                    enterPictureInPictureMode()
            }
        }
    }
    

}
