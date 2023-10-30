package com.wafflestudio.siksha2.ui.menuDetail

import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.wafflestudio.siksha2.databinding.ItemReviewBinding
import com.wafflestudio.siksha2.models.Review
import com.wafflestudio.siksha2.utils.getInflater
import com.wafflestudio.siksha2.utils.toKoreanDate
import com.wafflestudio.siksha2.utils.toLocalDateTime
import com.wafflestudio.siksha2.utils.setVisibleOrGone

class MenuReviewsAdapter constructor(
    private val showImage: Boolean = true,
    private val fragmentManager: FragmentManager? = null
) : PagingDataAdapter<Review, MenuReviewsAdapter.ViewHolder>(diffCallback) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.binding.apply {
            stars.rating = item?.score?.toFloat() ?: 0.0f
            reviewText.text = item?.comment
            date.text = item?.createdAt?.toLocalDateTime()?.toLocalDate()?.toKoreanDate() ?: "-"
            idText.text = "ID " + item?.userId.toString()
            if (showImage) {
                item?.etc?.images?.let {
                    if (it.isNotEmpty()) {
                        val imageViewList = listOf(this.reviewImageView1, this.reviewImageView2, this.reviewImageView3)
                        this.reviewImageLayout.setVisibleOrGone(true)

                        for (i in 0 until 3) {
                            if (i < it.size) {
                                imageViewList[i].run {
                                    setImage(it[i])
                                    fragmentManager?.let {
                                        setImageClickListener { url ->
                                            val dialog = ReviewImageDialog(url)
                                            dialog.show(fragmentManager, "review_image_$url")
                                        }
                                    }
                                    setVisibleOrGone(true)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemReviewBinding.inflate(parent.getInflater(), parent, false)
        return ViewHolder(binding)
    }

    class ViewHolder(val binding: ItemReviewBinding) : RecyclerView.ViewHolder(binding.root)

    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<Review>() {
            override fun areItemsTheSame(oldItem: Review, newItem: Review): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Review, newItem: Review): Boolean {
                return oldItem == newItem
            }
        }
    }
}
