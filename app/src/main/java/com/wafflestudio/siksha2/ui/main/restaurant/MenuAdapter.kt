package com.wafflestudio.siksha2.ui.main.restaurant

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.wafflestudio.siksha2.databinding.ItemMenuBinding
import com.wafflestudio.siksha2.models.Menu
import com.wafflestudio.siksha2.utils.toPrettyString
import com.wafflestudio.siksha2.utils.setVisibleOrGone

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

        holder.binding.apply {
            // FIXME: Api 변경 요구 하기 (하드코딩 스트링 싫어요)
            // Context 제대로 파악하기 (no fork? no meat?)
            val noMeat = menu.etc?.contains("No meat") == true
            menuTitleText.text = menu.nameKr
            iconNoFork.setVisibleOrGone(noMeat)
            priceText.text = menu.price.toPrettyString()
            rateText.rate = menu.score ?: 0.0
            likeButton.isSelected = menu.isLiked ?: false
            root.setOnClickListener {
                onMenuItemClickListener.invoke(menu.id)
            }
            likeButton.setOnClickListener {
                onMenuItemToggleLikeClickListener.invoke(menu.id, menu.isLiked ?: false)
            }
        }
    }

    class MenuViewHolder(val binding: ItemMenuBinding) : RecyclerView.ViewHolder(binding.root)

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
