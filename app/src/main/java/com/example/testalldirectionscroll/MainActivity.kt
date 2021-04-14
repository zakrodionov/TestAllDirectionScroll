package com.example.testalldirectionscroll

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.testalldirectionscroll.adapter.FixedGridLayoutManager
import com.example.testalldirectionscroll.adapter.InsetDecoration
import com.example.testalldirectionscroll.adapter.SimpleAdapter

class MainActivity : AppCompatActivity() {

    private val adapter: SimpleAdapter = SimpleAdapter(arrayListOf())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.rvFeed)
        val layoutManager = FixedGridLayoutManager()
        layoutManager.mTotalColumnCount = 3

        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(InsetDecoration(this))

        adapter.itemCount = 50
    }
}