package com.wafflestudio.siksha2.ui.common

import android.graphics.drawable.Drawable
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.wafflestudio.siksha2.R
import com.wafflestudio.siksha2.utils.showToast

class ImageDetailAdapter(private val images: List<String>) : RecyclerView.Adapter<ImageDetailViewHolder>() {
    override fun getItemCount(): Int = images.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageDetailViewHolder {
        return ImageDetailViewHolder(
            ScalableImageView(parent.context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
        )
    }

    override fun onBindViewHolder(holder: ImageDetailViewHolder, position: Int) {
        holder.scalableImageView.setModel(images[position])
    }
}

class ImageDetailViewHolder(val scalableImageView: ScalableImageView) : RecyclerView.ViewHolder(scalableImageView)
