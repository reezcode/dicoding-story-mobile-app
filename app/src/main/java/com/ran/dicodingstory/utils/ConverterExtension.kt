package com.ran.dicodingstory.utils

import com.ran.dicodingstory.data.local.entity.StoryResponseItem
import com.ran.dicodingstory.data.remote.response.Story

fun Story.toEntity(): StoryResponseItem {
    return StoryResponseItem(
        id = this.id,
        name = this.name,
        description = this.description,
        photoUrl = this.photoUrl,
        createdAt = this.createdAt,
        lat = this.lat,
        lon = this.lon
    )
}

fun StoryResponseItem.toParcelable(): Story {
    return Story(
        id = this.id,
        name = this.name,
        description = this.description,
        photoUrl = this.photoUrl,
        createdAt = this.createdAt,
        lat = this.lat,
        lon = this.lon
    )
}