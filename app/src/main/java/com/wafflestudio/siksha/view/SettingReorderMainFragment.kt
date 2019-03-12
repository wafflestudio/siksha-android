package com.wafflestudio.siksha.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.wafflestudio.siksha.R
import com.wafflestudio.siksha.adapter.RestaurantAdapter
import com.wafflestudio.siksha.preference.SikshaPreference
import com.woxthebox.draglistview.DragItem
import com.woxthebox.draglistview.DragListView
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_setting_version.view.*
import java.util.ArrayList
import javax.inject.Inject

open class SettingReorderMainFragment : Fragment() {

    @Inject
    lateinit var preference: SikshaPreference

    private var mItemArray: ArrayList<Pair<Long, String>>? = null
    private lateinit var mDragListView: DragListView
    lateinit var mRefreshLayout: MySwipeRefreshLayout

    open val onlyFavorites = false

    companion object {
        fun newInstance(): SettingReorderMainFragment = SettingReorderMainFragment()
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_setting_reorder, container, false)
        mRefreshLayout = view.findViewById(R.id.swipe_refresh_layout)
        mDragListView = view.findViewById(R.id.drag_list_view)
        mDragListView.recyclerView.isVerticalScrollBarEnabled = true
        mDragListView.setDragListListener(object : DragListView.DragListListenerAdapter() {
            override fun onItemDragStarted(position: Int) {
                mRefreshLayout.isEnabled = false
                //Toast.makeText(mDragListView.context, "Start - position: $position", Toast.LENGTH_SHORT).show()
            }

            override fun onItemDragEnded(fromPosition: Int, toPosition: Int) {
                mRefreshLayout.isEnabled = true
                //Toast.makeText(mDragListView.context, "Strat - Position: $fromPosition\nEnd - position: $toPosition", Toast.LENGTH_SHORT).show()
                val diff = toPosition - fromPosition
                val restaurantCodeList = preference.restaurantCodeList
                if (diff < 0) {
                    //get higher priority
                    if (onlyFavorites) {
                        restaurantCodeList.forEachIndexed { _, code ->
                            val priority = preference.getFavoriteRestaurantPriority(code)
                            if (priority == fromPosition) preference.setFavoriteRestaurantPriority(code, toPosition)
                            else if (priority in toPosition..(fromPosition - 1)) preference.setFavoriteRestaurantPriority(code, priority + 1)
                        }
                    } else {
                        restaurantCodeList.forEachIndexed { _, code ->
                            val priority = preference.getRestaurantPriority(code)
                            if (priority == fromPosition) preference.setRestaurantPriority(code, toPosition)
                            else if (priority in toPosition..(fromPosition - 1)) preference.setRestaurantPriority(code, priority + 1)
                        }
                    }
                } else if (diff > 0) {
                    //get lower priority
                    if (onlyFavorites) {
                        restaurantCodeList.forEachIndexed { _, code ->
                            val priority = preference.getFavoriteRestaurantPriority(code)
                            if (priority == fromPosition) preference.setFavoriteRestaurantPriority(code, toPosition)
                            else if (priority in (fromPosition + 1)..toPosition) preference.setFavoriteRestaurantPriority(code, priority - 1)
                        }
                    } else {
                        restaurantCodeList.forEachIndexed { _, code ->
                            val priority = preference.getRestaurantPriority(code)
                            if (priority == fromPosition) preference.setRestaurantPriority(code, toPosition)
                            else if (priority in (fromPosition + 1)..toPosition) preference.setRestaurantPriority(code, priority - 1)
                        }
                    }
                }
            }
        })
        val codeList = preference.restaurantCodeList
                .filter { !onlyFavorites || preference.favorite.contains(it) }
                .sortedWith(Comparator { p0, p1 ->
                    if (onlyFavorites) preference.getFavoriteRestaurantPriority(p0) -
                        preference.getFavoriteRestaurantPriority(p1)
                    else
                        preference.getRestaurantPriority(p0) -
                            preference.getRestaurantPriority(p1)
                })
        mItemArray = ArrayList()
        if (onlyFavorites) preference.restaurantCodeList.forEachIndexed { index, code ->
            if (!preference.favorite.contains(code)) {
                preference.setFavoriteRestaurantPriority(code, 100 + index)
            }
        }
        codeList.forEachIndexed { index, code ->
            (mItemArray as ArrayList<Pair<Long, String>>).add(Pair(index.toLong(), preference.getNameWithCode(code)))
            if (onlyFavorites) {
                preference.setFavoriteRestaurantPriority(code, index)
            }
        }
        mRefreshLayout.setScrollingView(mDragListView.recyclerView)
        mRefreshLayout.setOnRefreshListener { mRefreshLayout.postDelayed({ mRefreshLayout.isRefreshing = false }, 0) }
        setupListRecyclerView()
        view.img_back.setOnClickListener {
            fragmentManager?.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
        view.text_back.setOnClickListener {
            fragmentManager?.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
        return view
    }

    private fun setupListRecyclerView() {
        mDragListView.setLayoutManager(LinearLayoutManager(context))
        val listAdapter = RestaurantAdapter(mItemArray ?: ArrayList(), R.layout.item_restaurant,
                R.id.icon_drag, false)
        mDragListView.setAdapter(listAdapter, true)
        mDragListView.setCanDragHorizontally(false)
        mDragListView.setCustomDragItem(MyDragItem(context!!, R.layout.item_restaurant))
    }

    private class MyDragItem internal constructor(context: Context, layoutId: Int) : DragItem(context, layoutId) {

        override fun onBindDragView(clickedView: View, dragView: View) {
            (dragView.findViewById(R.id.text_setting_restaurant) as TextView).text =
                    (clickedView.findViewById(R.id.text_setting_restaurant) as TextView).text
        }
    }
}