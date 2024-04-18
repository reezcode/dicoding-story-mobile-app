package com.ran.dicodingstory.ui.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ran.dicodingstory.R
import androidx.core.util.Pair
import com.ran.dicodingstory.data.local.entity.StoryResponseItem
import com.ran.dicodingstory.ui.activities.DetailActivity
import com.ran.dicodingstory.utils.DateHelper
import com.ran.dicodingstory.utils.GeoLocationHelper
import com.ran.dicodingstory.utils.toParcelable

class StoryAdapter : PagingDataAdapter<StoryResponseItem, StoryAdapter.StoryViewHolder>(DIFF_CALLBACK){
    override fun onBindViewHolder(holder: StoryAdapter.StoryViewHolder, position: Int) {
        val story = getItem(position)
        with(holder){
            if (story != null) {
                Glide.with(holder.itemView.context)
                    .load(story.photoUrl)
                    .into(holder.imgPhoto)
                tvName.text = story.name
                tvLocation.text = if (story.lat != null && story.lon != null) {
                    GeoLocationHelper.getAddressFromLocation(story.lat, story.lon, holder.itemView.context)
                } else {
                    "Location not available"
                }
                tvDescription.text = story.description
                tvDate.text = story.createdAt?.let { DateHelper.formatDate(it) }
                itemView.setOnClickListener {
                    val intent = Intent(itemView.context, DetailActivity::class.java)
                    val storyParcelable = story.toParcelable()
                    intent.putExtra(DetailActivity.EXTRA_STORY, storyParcelable)
                    val optionsCompat: ActivityOptionsCompat =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                            itemView.context as Activity,
                            Pair(imgPhoto, "image"),
                            Pair(tvName, "name"),
                            Pair(tvLocation, "location"),
                            Pair(tvDescription, "description"),
                            Pair(tvDate, "date")
                        )
                    itemView.context.startActivity(intent, optionsCompat.toBundle())
                }
            }
        }
    }

    inner class StoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgPhoto: ImageView = itemView.findViewById(R.id.imageView)
        val tvName:TextView = itemView.findViewById(R.id.tv_name)
        val tvLocation: TextView = itemView.findViewById(R.id.tv_location)
        val tvDescription: TextView = itemView.findViewById(R.id.tv_description)
        val tvDate: TextView = itemView.findViewById(R.id.tv_date)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): StoryAdapter.StoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_row_story, parent, false)
        return StoryViewHolder(view)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StoryResponseItem>() {
            override fun areItemsTheSame(oldItem: StoryResponseItem, newItem: StoryResponseItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: StoryResponseItem, newItem: StoryResponseItem): Boolean {
                return oldItem == newItem
            }

        }
    }
}