package com.example.testalldirectionscroll

import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.testalldirectionscroll.AdditionItemsDirection.END
import com.example.testalldirectionscroll.AdditionItemsDirection.START
import com.example.testalldirectionscroll.common.CustomStableIdAdapter
import com.example.testalldirectionscroll.common.DiffItem
import com.example.testalldirectionscroll.databinding.ItemFeedBinding
import com.example.testalldirectionscroll.databinding.ItemInnerFeedBinding
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding

val savedAdapters = mutableMapOf<String, AsyncListDifferDelegationAdapter<DiffItem>>()
val savedScrollX = mutableMapOf<String, Int>()
val savedListStateInitialized = mutableMapOf<String, Boolean>()

fun feedDelegate(
    onItemClick: (FeedItem) -> Unit = {},
    addInnerPost: (FeedItem, AdditionItemsDirection) -> Unit = { item, direction -> },
) = adapterDelegateViewBinding<FeedItem, DiffItem, ItemFeedBinding>(
    { inflater, root -> ItemFeedBinding.inflate(inflater, root, false) }) {

    binding.root.setOnClickListener {
        onItemClick.invoke(item)
    }

    val linearLayoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
    val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            Log.d("wwwwwn:", "Call inner scrollListener $adapterPosition")
            savedScrollX[item.itemId] = binding.rvInner.computeHorizontalScrollOffset()

            if (linearLayoutManager.findLastVisibleItemPosition() + THRESHOLD > item.items.count()) {
                addInnerPost(item, END)
            }
            if (linearLayoutManager.findFirstVisibleItemPosition() < THRESHOLD) {
                addInnerPost(item, START)
            }
        }
    }

    binding.rvInner.apply {
        itemAnimator = null
        layoutManager = linearLayoutManager
    }

    val snapHelper = LinearSnapHelper()
    snapHelper.attachToRecyclerView(binding.rvInner)

    bind {
        binding.rvInner.removeOnScrollListener(scrollListener)

        val feedsAdapter =
            savedAdapters.getOrPut(item.itemId, { CustomStableIdAdapter(innerFeedDelegate()) })

        binding.rvInner.apply {
            adapter = feedsAdapter
            feedsAdapter.items = item.items
        }

        val state = savedScrollX[item.itemId]
        state?.let {
            if (binding.rvInner.computeHorizontalScrollOffset() != it) {
                binding.rvInner.scrollBy(it, 0)
            }
        }

        val isInitialized = savedListStateInitialized.getOrPut(item.itemId, { false })
        if (!isInitialized) {
            binding.rvInner.scrollToPosition(item.items.size / 2)
            savedListStateInitialized[item.itemId] = true
        }

        binding.rvInner.addOnScrollListener(scrollListener)
    }

    /**
     * If we fast scroll while this ViewHolder's RecyclerView is still settling the scroll,
     * the view will be detached and won't be snapped correctly
     *
     * To fix that, we snap again without smooth scrolling.
     */
    onViewDetachedFromWindow {
        if (binding.rvInner.scrollState != RecyclerView.SCROLL_STATE_IDLE) {
            snapHelper.findSnapView(linearLayoutManager)?.let {
                val snapDistance = snapHelper.calculateDistanceToFinalSnap(linearLayoutManager, it)
                if (snapDistance!![0] != 0 || snapDistance[1] != 0) {
                    binding.rvInner.scrollBy(snapDistance[0], snapDistance[1])
                }
            }
        }
    }
}

fun innerFeedDelegate(onItemClick: (InnerFeedItem) -> Unit = {}) =
    adapterDelegateViewBinding<InnerFeedItem, DiffItem, ItemInnerFeedBinding>(
        { inflater, root -> ItemInnerFeedBinding.inflate(inflater, root, false) }) {

        binding.root.setOnClickListener {
            onItemClick.invoke(item)
        }

        bind {
            binding.tvFeed.text = item.itemId
        }
    }