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
            Log.d("wwwww: ", "Call inner scrollListener $adapterPosition")
            savedScrollX[item.itemId] = binding.vpInner.computeHorizontalScrollOffset()

            if (linearLayoutManager.findLastVisibleItemPosition() + THRESHOLD > item.items.count()) {
                addInnerPost(item, END)
            }
            if (linearLayoutManager.findFirstVisibleItemPosition() < THRESHOLD) {
                addInnerPost(item, START)
            }
        }
    }

    binding.vpInner.apply {
        itemAnimator = null
        layoutManager = linearLayoutManager
        addOnScrollListener(scrollListener)
    }

    val snapHelper = LinearSnapHelper()
    snapHelper.attachToRecyclerView(binding.vpInner)

    bind {
        val feedsAdapter =
            savedAdapters.getOrPut(item.itemId, { CustomStableIdAdapter(innerFeedDelegate()) })

        binding.vpInner.apply {
            adapter = feedsAdapter
            feedsAdapter.items = item.items
        }

        val state = savedScrollX[item.itemId]
        state?.let {
            if (binding.vpInner.computeHorizontalScrollOffset() != it) {
                binding.vpInner.scrollBy(it, 0)
            }
        }

        val isInitialized = savedListStateInitialized.getOrPut(item.itemId, { false })
        if (!isInitialized) {
            binding.vpInner.scrollToPosition(item.items.size / 2)
            savedListStateInitialized[item.itemId] = true
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