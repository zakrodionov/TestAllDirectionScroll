package com.example.testalldirectionscroll

import android.os.Parcelable
import com.example.testalldirectionscroll.common.DiffItem
import kotlinx.parcelize.Parcelize
import kotlin.random.Random

@Parcelize
data class FeedItem(
    override val itemId: String,
    val items: List<InnerFeedItem>
) : DiffItem, Parcelable

@Parcelize
data class InnerFeedItem(override val itemId: String) : DiffItem, Parcelable

enum class AdditionItemsDirection {
    START,
    END
}

fun generateItems(): List<FeedItem> =
    (0..15).mapIndexed { index, i ->
        FeedItem(
            "$index-${Random.nextInt()}",
            generateInnerItems("$index")
        )
    }

fun generateInnerItems(prefix: String): List<InnerFeedItem> =
    (0..15).mapIndexed { index, i -> InnerFeedItem("$prefix --- $index --- ${Random.nextInt()}") }