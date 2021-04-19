package com.example.testalldirectionscroll

import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.testalldirectionscroll.AdditionItemsDirection.END
import com.example.testalldirectionscroll.AdditionItemsDirection.START
import com.example.testalldirectionscroll.common.CustomStableIdAdapter
import com.example.testalldirectionscroll.common.DiffItem
import com.example.testalldirectionscroll.databinding.ItemFeedBinding
import com.example.testalldirectionscroll.databinding.ItemInnerFeedBinding
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding

// Todo перенести во fragment/activity/adapter
val savedAdapters = mutableMapOf<String, AsyncListDifferDelegationAdapter<DiffItem>>()
val savedScrollX = mutableMapOf<String, Int>()
val savedListStateInitialized = mutableMapOf<String, Boolean>()
val viewPool = RecyclerView.RecycledViewPool()

fun feedDelegate(
    onItemClick: (FeedItem) -> Unit = {},
    addInnerPost: (FeedItem, AdditionItemsDirection) -> Unit = { item, direction -> },
) = adapterDelegateViewBinding<FeedItem, DiffItem, ItemFeedBinding>(
    { inflater, root -> ItemFeedBinding.inflate(inflater, root, false) }) {

    binding.root.setOnClickListener {
        onItemClick.invoke(item)
    }

    binding.rvInner.setRecycledViewPool(viewPool)

    val snapHelper = PagerSnapHelper()
    snapHelper.attachToRecyclerView(binding.rvInner)

    val linearLayoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
    val scrollListener = EndlessSnapScrollListener(snapHelper, object : OnSnapPositionChangeListener {
        override fun onSnapPositionChange(position: Int) {
            Log.d("wwwwwn: pos-del", "${item.items.getOrNull(position)?.itemId}")
        }

        override fun onHorizontalScrollOffsetChange(offset: Int) {
            Log.d("wwwwwn:", "Call inner scrollListener $adapterPosition")
            savedScrollX[item.itemId] = offset
        }

        override fun loadToStart() {
            addInnerPost(item, START)
        }

        override fun loadToEnd() {
            addInnerPost(item, END)
        }
    })

    binding.rvInner.apply {
        itemAnimator = null
        layoutManager = linearLayoutManager
    }

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

        binding.rvInner.post {
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
            binding.tvFeed.text = item.title
        }
    }