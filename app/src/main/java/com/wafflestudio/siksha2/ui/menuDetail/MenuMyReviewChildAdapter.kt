package com.wafflestudio.siksha2.ui.menuDetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.wafflestudio.siksha2.databinding.ItemMyReviewBinding
import com.wafflestudio.siksha2.models.Review

class MenuMyReviewChildAdapter  : ListAdapter<Review, MenuMyReviewChildAdapter.ReviewViewHolder>(DiffCallback) {

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Review>() {
            override fun areItemsTheSame(oldItem: Review, newItem: Review): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Review, newItem: Review): Boolean {
                return oldItem == newItem
            }
        }
    }

    inner class ReviewViewHolder(private val binding: ItemMyReviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Review) {
            binding.reviewContent.text = item.comment ?: "내용 없음"
            binding.reviewDate.text = item.createdAt
            binding.reviewScore.text = "⭐ ${item.score}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val binding = ItemMyReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
