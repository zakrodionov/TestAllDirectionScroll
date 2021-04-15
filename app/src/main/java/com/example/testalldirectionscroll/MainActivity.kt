package com.example.testalldirectionscroll

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.testalldirectionscroll.AdditionItemsDirection.END
import com.example.testalldirectionscroll.AdditionItemsDirection.START
import com.example.testalldirectionscroll.common.CustomStableIdAdapter
import com.example.testalldirectionscroll.databinding.ActivityMainBinding
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

const val THRESHOLD = 5

class MainActivity : AppCompatActivity() {

    private val feedsAdapter = CustomStableIdAdapter(
        feedDelegate(
            addInnerPost = { feedItem, direction -> addInnerPost(feedItem, direction) }
        )
    )

    private val linearLayoutManager by lazy { LinearLayoutManager(this) }

    private val binding: ActivityMainBinding by viewBinding(ActivityMainBinding::bind)

    private var job: Job = Job()
    private var innerJob: Job = Job()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val snapHelper = PagerSnapHelper()
        binding.rvMain.apply {
            itemAnimator = null
            adapter = feedsAdapter
            layoutManager = linearLayoutManager
            snapHelper.attachToRecyclerView(this)
        }

        job = lifecycleScope.launch { } // TODO
        innerJob = lifecycleScope.launch { } // TODO

        val items = generateItems()
        feedsAdapter.items = items

        binding.rvMain.apply {
            scrollToPosition(items.size / 2) // TODO

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (linearLayoutManager.findFirstVisibleItemPosition() < THRESHOLD) {
                        addPost(START)
                    }

                    if (linearLayoutManager.findLastVisibleItemPosition() + THRESHOLD > linearLayoutManager.itemCount) {
                        addPost(END)
                    }
                }
            })

            post {
                snapHelper.findSnapView(linearLayoutManager)?.let {
                    val snapDistance =
                        snapHelper.calculateDistanceToFinalSnap(linearLayoutManager, it)
                    if (snapDistance!![0] != 0 || snapDistance[1] != 0) {
                        scrollBy(snapDistance[0], snapDistance[1])
                    }
                }
            }
        }
    }

    // Simulate two-way pagination
    private fun addPost(direction: AdditionItemsDirection) {
        if (job.isCompleted) {
            job = lifecycleScope.launch {
                delay(500)

                val itemsSnapshot = feedsAdapter.items

                if (direction == START) {
                    feedsAdapter.items = generateItems() + itemsSnapshot
                } else {
                    feedsAdapter.items = itemsSnapshot + generateItems()
                }

                Log.d("wwwww:", "Add additional posts to: $direction")
            }
        }
    }

    // Simulate two-way pagination
    private fun addInnerPost(item: FeedItem, direction: AdditionItemsDirection) {
        Log.d("wwwww:", "InnerJob: ${innerJob.isCompleted}")
        if (innerJob.isCompleted) {
            innerJob = lifecycleScope.launch {
                delay(500)
                val itemsSnapshot = feedsAdapter.items.filterIsInstance<FeedItem>()
                feedsAdapter.items = itemsSnapshot.map {
                    if (it == item) {
                        if (direction == START) {
                            it.copy(items = generateInnerItems(item.itemId) + it.items)
                        } else {
                            it.copy(items = it.items + generateInnerItems(item.itemId))
                        }
                    } else {
                        it
                    }
                }

                Log.d("wwwww:", "Add additional inner posts to: ${item.itemId} --- $direction")
            }
        }
    }
}