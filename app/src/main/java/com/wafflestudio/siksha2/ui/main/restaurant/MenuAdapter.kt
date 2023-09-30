package com.wafflestudio.siksha2.ui.main.restaurant

import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.wafflestudio.siksha2.databinding.ItemMenuBinding
import com.wafflestudio.siksha2.models.Menu
import com.wafflestudio.siksha2.utils.toPrettyString
import com.wafflestudio.siksha2.utils.visibleOrGone

class MenuAdapter(
    private val onMenuItemClickListener: (Long) -> Unit,
    private val onMenuItemToggleLikeClickListener: (menuId: Long, isCurrentlyLiked: Boolean) -> Unit
) : ListAdapter<Menu, MenuAdapter.MenuViewHolder>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        return MenuViewHolder(
            ItemMenuBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val menu = getItem(position)

        val gestureDetector = GestureDetector(
            holder.binding.root.context,
            object : GestureDetector.OnGestureListener {
                override fun onDown(e1: MotionEvent): Boolean {
                    return false
                }

                override fun onShowPress(e1: MotionEvent) {}
                override fun onSingleTapUp(e1: MotionEvent): Boolean {
                    onMenuItemClickListener.invoke(menu.id)
                    return true
                }

                override fun onScroll(
                    e1: MotionEvent,
                    p1: MotionEvent,
                    p2: Float,
                    p3: Float
                ): Boolean {
                    return false
                }

                override fun onLongPress(e1: MotionEvent) {}
                override fun onFling(
                    e1: MotionEvent,
                    e2: MotionEvent,
                    velocityX: Float,
                    velocityY: Float
                ): Boolean {
                    return false
                }
            }
        )

        holder.binding.apply {
            // FIXME: Api 변경 요구 하기 (하드코딩 스트링 싫어요)
            // Context 제대로 파악하기 (no fork? no meat?)
            val noMeat = menu.etc?.contains("No meat") == true
            menuTitleText.text = menu.nameKr
            iconNoFork.visibleOrGone(noMeat)
            priceText.text = menu.price.toPrettyString()
            rateText.rate = menu.score ?: 0.0
            likeButton.isSelected = menu.isLiked ?: false
            likeButton.setOnClickListener {
                onMenuItemToggleLikeClickListener.invoke(menu.id, menu.isLiked ?: false)
                true
            }
            root.setOnTouchListener { _, event ->
                gestureDetector.onTouchEvent(event)
                true
            }
        }
    }

    class MenuViewHolder(val binding: ItemMenuBinding) : RecyclerView.ViewHolder(binding.root)

    fun refreshMenuItem(updatedMenuItem: Menu) {
        val updatedMenus = currentList.map { menu ->
            if (menu.id == updatedMenuItem.id) updatedMenuItem else menu
        }

        submitList(updatedMenus)
    }


    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<Menu>() {
            override fun areItemsTheSame(oldItem: Menu, newItem: Menu): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Menu, newItem: Menu): Boolean {
                return oldItem == newItem
            }

            override fun getChangePayload(oldItem: Menu, newItem: Menu): Any {
                // FIXME: change animation 없애려고 만든 임시코드
                return 1
            }
        }
    }
}
