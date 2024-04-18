package com.ran.dicodingstory

import com.ran.dicodingstory.data.local.entity.StoryResponseItem

object DataDummy {

    fun generateDummyStoryResponse(): List<StoryResponseItem> {
        val items: MutableList<StoryResponseItem> = arrayListOf()
        for (i in 0..100) {
            val story = StoryResponseItem(
                i.toString(),
                "name + $i",
                "description $i",
                "photoUrl $i",
                "createdAt $i",
                0.0,
                0.0
            )
            items.add(story)
        }
        return items
    }
}