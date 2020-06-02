package com.savvy.youtubeplayer

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.savvy.youtubeplayer.model.YoutubeVideo
import kotlinx.android.synthetic.main.item_video_relative.view.*

class YoutubeVideoAdapter(context: Context?, private val onClickListener: OnClickVideo) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private val VIEW_TYPE_LOADING = 1
        private val VIEW_TYPE_NORMAL = 0
    }

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var data: ArrayList<YoutubeVideo?>? = null


    fun setData(data: ArrayList<YoutubeVideo?>?) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            VIEW_TYPE_NORMAL -> return YoutubeVideoHolder(
                inflater.inflate(
                    R.layout.item_video_relative,
                    parent,
                    false
                )
            )
            VIEW_TYPE_LOADING -> return LoadingVideoHolder(
                inflater.inflate(
                    R.layout.item_loading,
                    parent,
                    false
                )
            )

        }
        return YoutubeVideoHolder(inflater.inflate(R.layout.item_video_constraint, parent, false))

    }

    override fun getItemViewType(position: Int): Int {
        return if (data?.get(position) == null) VIEW_TYPE_LOADING else VIEW_TYPE_NORMAL
    }

    override fun getItemCount(): Int {
        return (data?.size ?: 0)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val video = this.data?.get(position)
        if (holder is YoutubeVideoHolder) {
            showVideoView(holder, position)
        } else if (holder is LoadingVideoHolder) {
            showLoadingView(holder, position)
        }
        if (onClickListener != null) {
            if (video != null) {
                holder.itemView.setOnClickListener {
                    onClickListener.onVideoItemClick(data, position)
                }

                holder.itemView.imgMenu.setOnClickListener {
                    onClickListener.onVideoItemLongClick(video, position)
                    true
                }

            }
        }

    }

    private fun showLoadingView(holder: Any, position: Int) {

    }

    private fun showVideoView(holder: YoutubeVideoHolder, position: Int) {
        val item = data?.get(position)
        item?.let { holder.bindData(it) }
    }

    open class YoutubeVideoHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindData(video: YoutubeVideo) {
            itemView.tvTitleRela.text = video.title
            itemView.tvChannelRela.text = video.channel
//            itemView.tvPublishAtCons.text = video.publishAt.substring(0, 10)
            Glide.with(itemView.context)
                .load(video.url)
                .into(itemView.imThumbRela)
        }

    }

    inner class LoadingVideoHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)
        }
    }


    interface OnClickVideo {
        fun onVideoItemClick(data: ArrayList<YoutubeVideo?>?, position: Int)
        fun onVideoItemLongClick(video: YoutubeVideo, position: Int)
    }
}