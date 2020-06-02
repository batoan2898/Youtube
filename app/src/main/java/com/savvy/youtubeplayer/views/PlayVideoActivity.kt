package com.savvy.youtubeplayer.views

import android.app.PendingIntent
import android.app.PictureInPictureParams
import android.app.RemoteAction
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.drawable.Icon
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener
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
    var supportsPIP: Boolean = true
    var isRelease: Boolean = false
    private val ACTION_MEDIA_CONTROL = "media_control"
    private val EXTRA_CONTROL_TYPE = "control_type"
    private val REQUEST_PLAY = 1
    private val REQUEST_PAUSE = 2
    private val REQUEST_PREV = 0
    private val REQUEST_NEXT = 3
    private val CONTROL_TYPE_PLAY = 1
    private val CONTROL_TYPE_PAUSE = 2
    private val CONTROL_TYPE_PREV = 0
    private val CONTROL_TYPE_NEXT = 3

    @RequiresApi(Build.VERSION_CODES.O)
    private val mPictureInPictureParamsBuilder = PictureInPictureParams.Builder()

    private val mReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            intent?.let { intent ->
                if (intent.action != ACTION_MEDIA_CONTROL) {
                    return
                }

                val controlType = intent.getIntExtra(EXTRA_CONTROL_TYPE, 0)
                when (controlType) {
                    CONTROL_TYPE_PLAY -> youtubePlayerVideo?.play()
                    CONTROL_TYPE_PAUSE -> {
                        youtubePlayerVideo?.pause()
                        Log.e("aa", "pause")
                    }

                    CONTROL_TYPE_PREV ->
                        if (position > 0) {
                            Log.e("aa", "prev")
                            position--
                            startVideo(position)
                        } else return

                    CONTROL_TYPE_NEXT ->
                        if (position < listIdVideo.size - 1) {
                            Log.e("aa", "next")
                            position++
                            startVideo(position)
                        } else return

                    else -> return@let
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_video)
        getListVideo(intent)
        initYouTubePlayerView()
        supportsPIP = packageManager.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)
    }

    override fun onNewIntent(intent: Intent?) {
        var videoId = listIdVideo[position]
        getListVideo(intent)
        if (videoId == listIdVideo[position]) {
            videoPlayer.getPlayerUiController().showUi(true)
        } else {
            startVideo(position)
        }
        super.onNewIntent(intent)
    }

    private fun initYouTubePlayerView() {
        videoPlayer.enableBackgroundPlayback(true)
        initPiPIcon(videoPlayer)
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

    @RequiresApi(Build.VERSION_CODES.M)
    internal fun updatePictureInPictureActions(
        @DrawableRes iconId1: Int,
        @DrawableRes iconId: Int,
        @DrawableRes iconId2: Int,
        controlType: Int,
        controlType1: Int,
        controlType2: Int,
        requestCode: Int,
        requestCode1: Int,
        requestCode2: Int
    ) {
        val actions = ArrayList<RemoteAction>()

        val intent = PendingIntent.getBroadcast(
            this,
            requestCode, Intent(ACTION_MEDIA_CONTROL)
                .putExtra(EXTRA_CONTROL_TYPE, controlType), PendingIntent.FLAG_UPDATE_CURRENT
        )

        val intent1 = PendingIntent.getBroadcast(
            this,
            requestCode1, Intent(ACTION_MEDIA_CONTROL)
                .putExtra(EXTRA_CONTROL_TYPE, controlType1), PendingIntent.FLAG_UPDATE_CURRENT
        )

        val intent2 = PendingIntent.getBroadcast(
            this,
            requestCode2, Intent(ACTION_MEDIA_CONTROL)
                .putExtra(EXTRA_CONTROL_TYPE, controlType2), PendingIntent.FLAG_UPDATE_CURRENT
        )

        val icon = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Icon.createWithResource(this, iconId)
        } else {
            TODO("VERSION.SDK_INT < M")
        }
        val icon1 = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Icon.createWithResource(this, iconId1)
        } else {
            TODO("VERSION.SDK_INT < M")
        }
        val icon2 = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Icon.createWithResource(this, iconId2)
        } else {
            TODO("VERSION.SDK_INT < M")
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            actions.add(RemoteAction(icon1, title, title, intent1))
            actions.add(RemoteAction(icon, title, title, intent))
            actions.add(RemoteAction(icon2, title, title, intent2))

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mPictureInPictureParamsBuilder.setActions(actions)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setPictureInPictureParams(mPictureInPictureParamsBuilder.build())
        }
    }


    private fun initPiPIcon(youTubePlayerView: YouTubePlayerView) {
        val pictureInPictureIcon = ImageView(this)
        pictureInPictureIcon.setImageDrawable(
            ContextCompat.getDrawable(
                this,
                R.drawable.ic_picture_in_picture
            )
        )
        youTubePlayerView.getPlayerUiController().addView(pictureInPictureIcon)

        pictureInPictureIcon.setOnClickListener { view ->
            initPictureInPicture()
        }
    }


    private fun initPictureInPicture() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (supportsPIP)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    updatePictureInPictureActions(
                        R.drawable.ic_skip_previous, R.drawable.ic_pause
                        , R.drawable.ic_skip_next, CONTROL_TYPE_PAUSE, CONTROL_TYPE_PREV
                        , CONTROL_TYPE_NEXT, REQUEST_PAUSE, REQUEST_PREV, REQUEST_NEXT
                    )

                    enterPictureInPictureMode(mPictureInPictureParamsBuilder.build())
                }

        } else {
            AlertDialog.Builder(this)
                .setTitle("Can't enter picture in picture mode")
                .setMessage("In order to enter picture in picture mode you need a SDK version >= N.")
                .show()
        }
    }


    private fun getListVideo(intent: Intent?) {
        position = intent?.getSerializableExtra(Constants.Key.INTENT_POSITION_VIDEO) as Int
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
            isRelease = true
            registerReceiver(mReceiver, IntentFilter(ACTION_MEDIA_CONTROL))
            videoPlayer.enterFullScreen()
            videoPlayer.getPlayerUiController().showUi(false)

            youtubePlayerVideo?.addListener(object : YouTubePlayerListener {
                override fun onApiChange(youTubePlayer: YouTubePlayer) {
                }

                override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
                }

                override fun onError(
                    youTubePlayer: YouTubePlayer,
                    error: PlayerConstants.PlayerError
                ) {
                }

                override fun onPlaybackQualityChange(
                    youTubePlayer: YouTubePlayer,
                    playbackQuality: PlayerConstants.PlaybackQuality
                ) {
                }

                override fun onPlaybackRateChange(
                    youTubePlayer: YouTubePlayer,
                    playbackRate: PlayerConstants.PlaybackRate
                ) {
                }

                override fun onReady(youTubePlayer: YouTubePlayer) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    }
                }

                override fun onStateChange(
                    youTubePlayer: YouTubePlayer,
                    state: PlayerConstants.PlayerState
                ) {

                    if (state == PlayerConstants.PlayerState.PLAYING) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            updatePictureInPictureActions(
                                R.drawable.ic_skip_previous, R.drawable.ic_pause
                                , R.drawable.ic_skip_next, CONTROL_TYPE_PAUSE, CONTROL_TYPE_PREV
                                , CONTROL_TYPE_NEXT, REQUEST_PAUSE, REQUEST_PREV, REQUEST_NEXT
                            )

                        }
                    } else if (state == PlayerConstants.PlayerState.PAUSED) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            updatePictureInPictureActions(
                                R.drawable.ic_skip_previous, R.drawable.ic_play
                                , R.drawable.ic_skip_next, CONTROL_TYPE_PLAY, CONTROL_TYPE_PREV
                                , CONTROL_TYPE_NEXT, REQUEST_PLAY, REQUEST_PREV, REQUEST_NEXT
                            )

                        }

                    }
                }

                override fun onVideoDuration(youTubePlayer: YouTubePlayer, duration: Float) {
                }

                override fun onVideoId(youTubePlayer: YouTubePlayer, videoId: String) {
                }

                override fun onVideoLoadedFraction(
                    youTubePlayer: YouTubePlayer,
                    loadedFraction: Float
                ) {
                }

            })
        } else {
            isRelease = false
            unregisterReceiver(mReceiver)
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
            initPictureInPicture()
        }
    }

    override fun onStop() {
        val pm: PowerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        val isScreenOn: Boolean = pm.isInteractive

        if (isScreenOn) {
            if (isRelease) {
                youtubePlayerVideo?.pause()
            } else initPictureInPicture()
        }

        super.onStop()
    }


}
