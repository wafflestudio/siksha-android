package com.wafflestudio.siksha2.ui.menuDetail

import android.app.AlertDialog
import android.view.LayoutInflater
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

            binding.editButton.setOnClickListener {
                // Todo : 메뉴 수정 화면으로 넘어가기
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

    private fun setRatingStars(container: LinearLayout, rating: Float, maxStars: Int = 5) {
        container.removeAllViews() // 기존 별 제거
        val fullStar = R.drawable.ic_full_star
        val halfStar = R.drawable.ic_half_star
        val emptyStar = R.drawable.ic_empty_star

        // 별 폭과 높이는 container의 높이 기준으로 맞추기
        val starSize = container.height.takeIf { it > 0 } ?: LinearLayout.LayoutParams.WRAP_CONTENT

        for (i in 1..maxStars) {
            val imageView = ImageView(container.context).apply {
                val starRes = when {
                    i <= rating.toInt() -> fullStar // 정수 부분: 꽉 찬 별
                    i == rating.toInt() + 1 && rating % 1 >= 0.5 -> halfStar // 소수점 0.5 이상이면 반 별
                    else -> emptyStar // 나머지는 빈 별
                }

                setImageResource(starRes)

                layoutParams = LinearLayout.LayoutParams(
                    0,
                    starSize,
                    1f
                ).apply {
                    marginEnd = 0
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
            // Todo : 메뉴 삭제 API 추가
        }

        dialogBinding.tvNegativeButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
    }
}
