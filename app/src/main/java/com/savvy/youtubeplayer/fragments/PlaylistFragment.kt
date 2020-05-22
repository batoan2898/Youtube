package com.savvy.youtubeplayer.fragments

import android.content.Intent
import android.os.Bundle
import android.widget.PopupMenu
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.savvy.youtubeplayer.Constants
import com.savvy.youtubeplayer.R
import com.savvy.youtubeplayer.YoutubeVideoAdapter
import com.savvy.youtubeplayer.data.MySharedPreferences
import com.savvy.youtubeplayer.model.YoutubeVideo
import com.savvy.youtubeplayer.views.MainActivity
import com.savvy.youtubeplayer.views.PlayVideoActivity
import kotlinx.android.synthetic.main.fragment_playlist.*

class PlaylistFragment : BaseFragment<MainActivity>(), YoutubeVideoAdapter.OnClickVideo {
    private var playlistFirebaseDb: FirebaseDatabase = FirebaseDatabase.getInstance()
    private lateinit var adapter: YoutubeVideoAdapter
    private var dataChange: ArrayList<YoutubeVideo?>? = arrayListOf()

    override fun onVideoItemClick(data: ArrayList<YoutubeVideo?>?, position: Int) {
        val intent = Intent(requireActivity(), PlayVideoActivity::class.java)
        intent.putExtra(Constants.Key.INTENT_POSITION_VIDEO, position)
        intent.putExtra(Constants.Key.INTENT_LIST_VIDEO, data)
        startActivity(intent)

    }

    override fun onVideoItemLongClick(video: YoutubeVideo, position: Int) {
        val popup = PopupMenu(
            context, recyclerPlaylistFragment
                .findViewHolderForAdapterPosition(position)
                ?.itemView
        )
        popup.inflate(R.menu.menu_playlist_fragment)
        popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener {
            removeFromPlaylist(video)
        })
        popup.show()
    }

    private fun removeFromPlaylist(video: YoutubeVideo): Boolean {
        playlistFirebaseDb
            .reference
            .child("playlist")
            .child(MySharedPreferences.getId())
            .child(video.videoId)
            .removeValue()

        Toast.makeText(requireContext(),R.string.successful, Toast.LENGTH_SHORT).show()
        return true
    }


    fun initData() {
        playlistFirebaseDb
            .reference
            .child("playlist")
            .child(MySharedPreferences.getId())
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    for (i in p0.children) {
                        val video = i.getValue(YoutubeVideo::class.java)
                        dataChange?.add(video)
                    }
                    adapter.setData(dataChange)
                    dataChange = null
                    dataChange = arrayListOf()
                }

            })
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_playlist
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        initData()
        initView()
        super.onActivityCreated(savedInstanceState)
    }

    private fun initView() {
        adapter = YoutubeVideoAdapter(context, this)
        recyclerPlaylistFragment.adapter = adapter

    }


    companion object {
        @JvmStatic
        fun newInstance() =
            PlaylistFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}
