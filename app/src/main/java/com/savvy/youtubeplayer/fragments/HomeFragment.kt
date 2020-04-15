package com.savvy.youtubeplayer.fragments

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.savvy.savvymusic.ultis.APIUtils
import com.savvy.youtubeplayer.Constants
import com.savvy.youtubeplayer.R
import com.savvy.youtubeplayer.YoutubeVideoAdapter
import kotlinx.android.synthetic.main.fragment_home.*
import retrofit2.Call
import retrofit2.Response
import androidx.recyclerview.widget.LinearLayoutManager
import com.savvy.youtubeplayer.model.*
import com.savvy.youtubeplayer.views.MainActivity
import com.savvy.youtubeplayer.views.PlayVideoActivity
import retrofit2.Callback
import java.util.*
import kotlin.collections.ArrayList
import android.view.*
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.view.isVisible
import com.google.firebase.database.FirebaseDatabase
import com.savvy.youtubeplayer.data.MySharedPreferences


class HomeFragment : BaseFragment<MainActivity>(), YoutubeVideoAdapter.OnClickVideo{

    private var homeFirebaseDb = FirebaseDatabase.getInstance()
    private var dataAll = MutableLiveData<java.util.ArrayList<YoutubeVideo?>>()
    private lateinit var adapter: YoutubeVideoAdapter
    var isLoading = false
    var pageToken: String = ""
    private var dataResponse: ArrayList<DataYoutubeVideo> = arrayListOf()
    private var dataSearchResponse: ArrayList<DataSearchYoutubeVideo> = arrayListOf()
    private var dialogLoading: Dialog? = null
    private var timer = Timer()

    override fun getLayoutId(): Int {
        return R.layout.fragment_home
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        initView()
        getData()
        super.onActivityCreated(savedInstanceState)

    }

    private fun initView() {
        adapter = YoutubeVideoAdapter(context, this)
        recyclerHomeFragment.adapter = adapter

        dataAll.observe(this, Observer {
            adapter.setData(it)
        })
        initScrollView()
        setHasOptionsMenu(true)
        dialogLoading = context?.let { Dialog(it) }
        dialogLoading?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogLoading?.setContentView(R.layout.item_loading)

    }

    private fun initScrollView() {
        recyclerHomeFragment.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?
                if (!isLoading) {
                    if (linearLayoutManager != null
                        && linearLayoutManager
                            .findLastCompletelyVisibleItemPosition() == dataResponse.size - 1
                    ) {
                        getData()
                        isLoading = true
                    }
                }
            }
        })
    }


    fun searchVideo(s: Editable?){
        Thread(object : Runnable, Callback<DataSearch>{
            override fun run() {
                var keySearch: String? = s.toString()
                var apiKey: String = Constants.Key.YOUTUBE_API_KEY
                var type = "video"
                var maxResult = 25
                keySearch?.let {
                    APIUtils.dataClient.searchData(apiKey,
                        it, type, maxResult).enqueue(this)
                }
            }
            override fun onFailure(call: Call<DataSearch>, t: Throwable) {}

            override fun onResponse(call: Call<DataSearch>, response: Response<DataSearch>) {
                if (response.body() == null){
                    tvSearchResults.visibility
                }
                else{
                    dataSearchResponse = response.body()!!.searchListItems

                    dataAll.postValue(dataSearchResponse.map { dataSearchResponse ->
                        YoutubeVideo(
                            dataSearchResponse.searchSnippet.searchTitle,
                            dataSearchResponse.searchSnippet.searchDescription,
                            dataSearchResponse.searchSnippet.searchPublishAt,
                            dataSearchResponse.searchSnippet.searchChannelTitle,
                            dataSearchResponse.searchSnippet.searchThumbnails.searchImageMedium.searchUrlImage,
                            dataSearchResponse.searchId.searchVideoId
                        )
                    } as ArrayList<YoutubeVideo?>)
                }
                isLoading = true
            }
        }).start()

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun getData() {
        dataAll.value?.add(null)
        dataAll.value?.size?.minus(1)?.let { adapter.notifyItemInserted(it) }
        val callback = APIUtils.dataClient
        if (pageToken == null) {
            setData(
                callback.firstSelectData(
                    Constants.Key.YOUTUBE_API_KEY, Constants.Key.ID_PLAYLIST, 15
                )
            )
        } else {
            setData(
                callback.selectData(
                    Constants.Key.YOUTUBE_API_KEY, Constants.Key.ID_PLAYLIST, 15, pageToken
                )
            )
        }
    }

    internal fun setData(t: Call<Data>) {
        t.enqueue(object : Callback<Data?> {
            override fun onFailure(call: Call<Data?>, t: Throwable) {
            }

            override fun onResponse(call: Call<Data?>, response: Response<Data?>) {
                dataAll.value?.removeAt(dataAll.value!!.size - 1)
                response.body()?.let { body ->
                    pageToken = body.nextPageToken
                    dataResponse.addAll(body.listItems)

                    dataAll.postValue(dataResponse.map { dataResponse ->
                        YoutubeVideo(
                            dataResponse.snippet.title,
                            dataResponse.snippet.description,
                            dataResponse.snippet.publishAt,
                            dataResponse.snippet.channelTitle,
                            dataResponse.snippet.thumbnails.imageMedium.urlImage,
                            dataResponse.snippet.resourceId.videoId
                        )
                    } as ArrayList<YoutubeVideo?>)
                }
                isLoading = false
            }
        })
    }
    fun addDataFirebase(video: YoutubeVideo): Boolean {
        homeFirebaseDb
            .reference
            .child("playlist")
            .child(MySharedPreferences.getId())
            .child(video.videoId)
            .setValue(video)
        Toast.makeText(requireContext(),R.string.successful,Toast.LENGTH_SHORT).show()
        return true
    }

    override fun onVideoItemClick(data: ArrayList<YoutubeVideo?>?, position: Int) {
        val intent = Intent(requireActivity(), PlayVideoActivity::class.java)
        intent.putExtra(Constants.Key.INTENT_POSITION_VIDEO, position)
        intent.putExtra(Constants.Key.INTENT_LIST_VIDEO, data)
        startActivity(intent)

    }

    override fun onVideoItemLongClick(video: YoutubeVideo, position: Int) {
        val popup = PopupMenu(
            context, recyclerHomeFragment
                .findViewHolderForAdapterPosition(position)
                ?.itemView
        )
        popup.inflate(R.menu.menu_home_fragment)
        popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener {
            addDataFirebase(video)
        })
        popup.show()
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            HomeFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

}
