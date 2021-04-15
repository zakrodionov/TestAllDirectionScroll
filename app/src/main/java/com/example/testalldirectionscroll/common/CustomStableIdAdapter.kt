package com.example.testalldirectionscroll.common

import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter

class CustomStableIdAdapter(vararg delegates: AdapterDelegate<List<DiffItem>>) :
    AsyncListDifferDelegationAdapter<DiffItem>(DiffCallback, *delegates) {

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long {
        return items[position].itemId.hashCode().toLong()
    }
}