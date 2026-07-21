package com.wafflestudio.siksha2.ui.main.setting.reorder

import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.wafflestudio.siksha2.R
import com.wafflestudio.siksha2.compose.ui.settings.ReorderRestaurantNameCard
import com.wafflestudio.siksha2.databinding.ItemSettingRestaurantReorderBinding
import com.wafflestudio.siksha2.ui.SikshaTheme
import com.wafflestudio.siksha2.utils.getInflater
import com.woxthebox.draglistview.DragItemAdapter

class ReorderItemAdapter(
    private val onFavoriteClick: (Long) -> Unit,
    private val onVisibleClick: (Long) -> Unit
) : DragItemAdapter<ReorderRestaurant, ReorderItemAdapter.ViewHolder>() {
    fun submitList(list: List<ReorderRestaurant>) {
        itemList = list.toMutableList()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemSettingRestaurantReorderBinding.inflate(parent.getInflater(), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val item = mItemList[position]

        // 식당 이름 (Compose 호출)
        holder.binding.restaurantName.setContent {
            SikshaTheme {
                ReorderRestaurantNameCard(item.name)
            }
        }

        // --- 즐겨찾기(별) 아이콘 상태 및 로직 ---
        val starIcon = if (item.isFavorite) R.drawable.ic_star_on else R.drawable.ic_star_off
        holder.binding.ivFavorite.setImageResource(starIcon) // XML에 ivFavorite이 있다고 가정

        holder.binding.ivFavorite.setOnClickListener {
            onFavoriteClick(item.id)
        }

        // --- 눈(노출 여부) 아이콘 상태 및 로직 ---
        val eyeIcon = if (item.visible) R.drawable.ic_visibility else R.drawable.ic_visibility_off
        holder.binding.ivVisible.setImageResource(eyeIcon) // XML에 ivVisible이 있다고 가정

        holder.binding.ivVisible.setOnClickListener {
            onVisibleClick(item.id)
        }

        val handleBg = when (position) {
            0 -> R.drawable.frame_top_right_corner_radius_12
            itemCount - 1 -> R.drawable.frame_bottom_right_corner_radius_12
            else -> R.color.background_secondary
        }

        holder.binding.reorderHandleBackground.background = ContextCompat.getDrawable(holder.itemView.context, handleBg)

        // 투명도 처리 (눈 아이콘이 OFF인 경우 흐리게 표시)
        holder.binding.root.alpha = if (item.visible) 1.0f else 0.4f
    }

    override fun getUniqueItemId(position: Int): Long {
        return mItemList[position].id
    }

    class ViewHolder(val binding: ItemSettingRestaurantReorderBinding) :
        DragItemAdapter.ViewHolder(binding.root, R.id.reorder_handle, false)
}
