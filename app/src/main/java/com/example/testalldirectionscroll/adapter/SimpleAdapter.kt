package com.example.testalldirectionscroll.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.testalldirectionscroll.R
import com.example.testalldirectionscroll.adapter.SimpleAdapter.VerticalItemHolder
import java.util.*

class SimpleAdapter(val items: ArrayList<GameItem>) : RecyclerView.Adapter<VerticalItemHolder>() {
    private var mOnItemClickListener: OnItemClickListener? = null

    /*
     * A common adapter modification or reset mechanism. As with ListAdapter,
     * calling notifyDataSetChanged() will trigger the RecyclerView to update
     * the view. However, this method will not trigger any of the RecyclerView
     * animation features.
     */
    fun setItemCount(count: Int) {
        items.clear()
        items.addAll(generateDummyData(count))
        notifyDataSetChanged()
    }

    /*
     * Inserting a new item at the head of the list. This uses a specialized
     * RecyclerView method, notifyItemInserted(), to trigger any enabled item
     * animations in addition to updating the view.
     */
    fun addItem(position: Int) {
        if (position > items.size) return
        items.add(position, generateDummyItem())
        notifyItemInserted(position)
    }

    /*
     * Inserting a new item at the head of the list. This uses a specialized
     * RecyclerView method, notifyItemRemoved(), to trigger any enabled item
     * animations in addition to updating the view.
     */
    fun removeItem(position: Int) {
        if (position >= items.size) return
        items.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun onCreateViewHolder(container: ViewGroup, viewType: Int): VerticalItemHolder {
        val inflater = LayoutInflater.from(container.context)
        val root = inflater.inflate(R.layout.view_match_item, container, false)
        return VerticalItemHolder(root, this)
    }

    override fun onBindViewHolder(itemHolder: VerticalItemHolder, position: Int) {
        val item = items[position]
        itemHolder.setAwayScore(item.awayScore.toString())
        itemHolder.setHomeScore(item.homeScore.toString())
        itemHolder.setAwayName(item.awayTeam)
        itemHolder.setHomeName(item.homeTeam)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener?) {
        mOnItemClickListener = onItemClickListener
    }

    private fun onItemHolderClick(itemHolder: VerticalItemHolder) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener!!.onItemClick(
                null, itemHolder.itemView,
                itemHolder.adapterPosition, itemHolder.itemId
            )
        }
    }

    class GameItem(
        var homeTeam: String,
        var awayTeam: String,
        var homeScore: Int,
        var awayScore: Int
    )

    class VerticalItemHolder(itemView: View, adapter: SimpleAdapter) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private val mHomeScore: TextView
        private val mAwayScore: TextView
        private val mHomeName: TextView
        private val mAwayName: TextView
        private val mAdapter: SimpleAdapter
        override fun onClick(v: View) {
            mAdapter.onItemHolderClick(this)
        }

        fun setHomeScore(homeScore: CharSequence?) {
            mHomeScore.text = homeScore
        }

        fun setAwayScore(awayScore: CharSequence?) {
            mAwayScore.text = awayScore
        }

        fun setHomeName(homeName: CharSequence?) {
            mHomeName.text = homeName
        }

        fun setAwayName(awayName: CharSequence?) {
            mAwayName.text = awayName
        }

        init {
            itemView.setOnClickListener(this)
            mAdapter = adapter
            mHomeScore = itemView.findViewById<View>(R.id.text_score_home) as TextView
            mAwayScore = itemView.findViewById<View>(R.id.text_score_away) as TextView
            mHomeName = itemView.findViewById<View>(R.id.text_team_home) as TextView
            mAwayName = itemView.findViewById<View>(R.id.text_team_away) as TextView
        }
    }

    companion object {
        fun generateDummyItem(): GameItem {
            val random = Random()
            return GameItem(
                "Upset Home", "Upset Away",
                random.nextInt(100),
                random.nextInt(100)
            )
        }

        fun generateDummyData(count: Int): List<GameItem> {
            val items = ArrayList<GameItem>()
            for (i in 0 until count) {
                items.add(GameItem("Losers", "Winners", i, i + 5))
            }
            return items
        }
    }


}