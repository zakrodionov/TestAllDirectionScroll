package com.example.testalldirectionscroll

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper

class EndlessSnapScrollListener(
    private val snapHelper: SnapHelper,
    private val onSnapPositionChangeListener: OnSnapPositionChangeListener
) : RecyclerView.OnScrollListener() {

    private var snapPosition = RecyclerView.NO_POSITION

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            val snapPosition = snapHelper.getSnapPosition(recyclerView)
            val snapPositionChanged = this.snapPosition != snapPosition
            if (snapPositionChanged) {
                onSnapPositionChangeListener.onSnapPositionChange(snapPosition)
                this.snapPosition = snapPosition
            }
        }
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        val horizontalScrollOffset = recyclerView.computeHorizontalScrollOffset()
        onSnapPositionChangeListener.onHorizontalScrollOffsetChange(horizontalScrollOffset)

        if (getLayoutManager(recyclerView).findFirstVisibleItemPosition() < THRESHOLD) {
            onSnapPositionChangeListener.loadToStart()
        }

        if (getLayoutManager(recyclerView).findLastVisibleItemPosition() + THRESHOLD > getLayoutManager(recyclerView).itemCount) {
            onSnapPositionChangeListener.loadToEnd()
        }
    }

    private fun getLayoutManager(recyclerView: RecyclerView) = recyclerView.layoutManager as LinearLayoutManager

    private fun SnapHelper.getSnapPosition(recyclerView: RecyclerView): Int {
        val layoutManager = recyclerView.layoutManager ?: return RecyclerView.NO_POSITION
        val snapView = findSnapView(layoutManager) ?: return RecyclerView.NO_POSITION
        return layoutManager.getPosition(snapView)
    }
}

interface OnSnapPositionChangeListener {
    fun onSnapPositionChange(position: Int)
    fun onHorizontalScrollOffsetChange(offset: Int)
    fun loadToStart()
    fun loadToEnd()
}