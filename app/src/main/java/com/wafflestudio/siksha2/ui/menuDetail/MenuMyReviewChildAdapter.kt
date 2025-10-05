package com.wafflestudio.siksha2.ui.menuDetail

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.wafflestudio.siksha2.R
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
            setRatingStars(binding.starsContainer, item.score.toFloat())

            binding.deleteButton.setOnClickListener {
                showDeleteDialog(binding.root.context)
            }

            val isLast = bindingAdapterPosition == itemCount - 1
            binding.bottomDivider.visibility = if (isLast) View.GONE else View.VISIBLE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val binding = ItemMyReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private fun setRatingStars(container: LinearLayout, rating: Float, maxStars: Int = 5) {
        container.removeAllViews() // 기존 별 제거
        val fullStar = R.drawable.ic_full_star_2
        val emptyStar = R.drawable.ic_empty_star_2

        // 별 폭과 높이는 container의 높이 기준으로 맞추기
        val starSize = container.height.takeIf { it > 0 } ?: LinearLayout.LayoutParams.WRAP_CONTENT

        for (i in 1..maxStars) {
            val imageView = ImageView(container.context).apply {
                setImageResource(
                    if (i <= rating) fullStar else emptyStar
                )
                layoutParams = LinearLayout.LayoutParams(
                    0, // width 0 + weight를 주면 자동으로 나눠서 붙음
                    starSize,
                    1f // weight 1로 균등 배치
                ).apply {
                    marginEnd = 0 // 간격 없음
                }
                scaleType = ImageView.ScaleType.FIT_CENTER
            }
            container.addView(imageView)
        }
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
