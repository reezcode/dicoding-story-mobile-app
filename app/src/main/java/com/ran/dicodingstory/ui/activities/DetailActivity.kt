package com.ran.dicodingstory.ui.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.ran.dicodingstory.R
import com.ran.dicodingstory.data.remote.response.Story
import com.ran.dicodingstory.databinding.ActivityDetailBinding
import com.ran.dicodingstory.utils.DateHelper
import com.ran.dicodingstory.utils.GeoLocationHelper
import com.ran.dicodingstory.utils.NotifBarHelper

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val story: Story? = intent.getParcelableExtra(EXTRA_STORY)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        NotifBarHelper(window, this).setTransparent()
        with(binding){
            story?.let {
                Glide.with(this@DetailActivity)
                    .load(it.photoUrl)
                    .into(imgDetail)
                tvName.text = it.name
                tvLocation.text = GeoLocationHelper.getAddressFromLocation(story.lat ?: 0.0, story.lon ?: 0.0, this@DetailActivity)
                tvDescription.text = it.description
                tvDate.text = it.createdAt?.let { date-> DateHelper.formatDate(date) }
            }
        }
    }

    companion object {
        const val EXTRA_STORY = "extra_story"
    }
}