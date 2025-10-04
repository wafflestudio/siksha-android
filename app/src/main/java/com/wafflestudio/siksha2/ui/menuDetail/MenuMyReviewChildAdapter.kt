package com.wafflestudio.siksha2.ui.menuDetail

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.ui.unit.dp
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.wafflestudio.siksha2.compose.ui.menudetail.MenuRatingStars
import com.wafflestudio.siksha2.databinding.DialogDefaultBinding
import com.wafflestudio.siksha2.databinding.ItemMyReviewBinding
import com.wafflestudio.siksha2.models.Review
import com.wafflestudio.siksha2.utils.toLocalDateTime
import com.wafflestudio.siksha2.utils.toParsedTimeString

class MenuMyReviewChildAdapter : ListAdapter<Review, MenuMyReviewChildAdapter.ReviewViewHolder>(DiffCallback) {

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
            binding.reviewDate.text = item.createdAt.toLocalDateTime().toParsedTimeString()
            binding.stars.setContent {
                MenuRatingStars(
                    initialRating = item.score.toFloat() ?: 0.0f,
                    width = 61.dp,
                    height = 10.dp
                )
            }

            binding.deleteButton.setOnClickListener {
                showDeleteDialog(binding.root.context)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val binding = ItemMyReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private fun showDeleteDialog(context: android.content.Context) {
        val dialogBinding = DialogDefaultBinding.inflate(LayoutInflater.from(context))

        val dialog = AlertDialog.Builder(context)
            .setView(dialogBinding.root)
            .create()

        // 타이틀, 내용
        dialogBinding.dialogTitle.text = "평가 삭제"
        dialogBinding.dialogContent.text = "평가를 정말 삭제하시겠습니까?"

        // 버튼 이벤트
        dialogBinding.tvPositiveButton.text = "삭제"
        dialogBinding.tvNegativeButton.text = "취소"

        dialogBinding.tvPositiveButton.setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.tvNegativeButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
    }
}
