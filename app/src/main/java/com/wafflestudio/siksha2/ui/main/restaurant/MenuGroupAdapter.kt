package com.wafflestudio.siksha2.ui.main.restaurant

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.wafflestudio.siksha2.databinding.ItemMenuGroupBinding
import com.wafflestudio.siksha2.models.MenuGroup
import com.wafflestudio.siksha2.utils.getInflater
import com.wafflestudio.siksha2.utils.visibleOrGone

class MenuGroupAdapter(
    private val onMenuGroupInfoClickListener: (Long) -> Unit,
    private val onMenuGroupToggleFavoriteClickListener: (Long) -> Unit,
    private val onMenuItemClickListener: (Long) -> Unit,
    private val onMenuItemToggleLikeClickListener: (menuId: Long, isCurrentlyLiked: Boolean) -> Unit
) :
    ListAdapter<MenuGroup, MenuGroupAdapter.MenuGroupViewHolder>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuGroupViewHolder {
        val binding = ItemMenuGroupBinding.inflate(parent.getInflater(), parent, false)
        val adapter = MenuAdapter(onMenuItemClickListener, onMenuItemToggleLikeClickListener)
        binding.menuList.also {
            it.adapter = adapter
            it.layoutManager = LinearLayoutManager(parent.context)
        }
        return MenuGroupViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MenuGroupViewHolder, position: Int) {
        val menuGroup = getItem(position)
        holder.binding.apply {
            restaurantTitle.text = menuGroup.nameKr
            infoButton.setOnClickListener {
                onMenuGroupInfoClickListener.invoke(menuGroup.id)
            }
            restaurantTitle.setOnClickListener {
                onMenuGroupInfoClickListener.invoke(menuGroup.id)
            }
            favoriteToggle.setOnClickListener {
                onMenuGroupToggleFavoriteClickListener.invoke(menuGroup.id)
            }
            favoriteToggle.isSelected = menuGroup.isFavorite

            menuList.visibleOrGone(menuGroup.menus.isEmpty().not())
            menuEmpty.visibleOrGone(menuGroup.menus.isEmpty())
            (menuList.adapter as? MenuAdapter)?.submitList(menuGroup.menus)
        }
    }

    class MenuGroupViewHolder(val binding: ItemMenuGroupBinding) :
        RecyclerView.ViewHolder(binding.root)

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<MenuGroup>() {
            override fun areItemsTheSame(oldItem: MenuGroup, newItem: MenuGroup): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: MenuGroup, newItem: MenuGroup): Boolean {
                return oldItem == newItem
            }

            override fun getChangePayload(oldItem: MenuGroup, newItem: MenuGroup): Any? {
                // Favorite 토글 시 애니메이션 생기는 것 방지
                return if (oldItem.isFavorite != newItem.isFavorite) {
                    1
                } else {
                    null
                }
            }
        }
    }
}
