package com.wafflestudio.siksha.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.util.Pair
import com.wafflestudio.siksha.R
import com.woxthebox.draglistview.DragItemAdapter
import java.util.ArrayList

internal class RestaurantAdapter(val list: ArrayList<Pair<Long, String>>,
                                 private val mLayoutId: Int,
                                 private val mGrabHandleId: Int,
                                 private val mDragOnLongPress: Boolean)
    : DragItemAdapter<Pair<Long, String>, RestaurantAdapter.ViewHolder>()
{

    init {
        itemList = list
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(mLayoutId, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.mText.text = mItemList[position].second
        holder.itemView.tag = mItemList[position]
    }

    override fun getUniqueItemId(position: Int): Long {
        return mItemList[position].first ?: -1
    }

    internal inner class ViewHolder(itemView: View) : DragItemAdapter.ViewHolder(itemView, mGrabHandleId, mDragOnLongPress) {
        var mText: TextView

        init {
            mText = itemView.findViewById(R.id.text_setting_restaurant) as TextView
        }
    }
}