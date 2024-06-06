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
        val context = holder.scalableImageView.context
        val placeHolderDrawable = CircularProgressDrawable(context).apply {
            setColorSchemeColors(context.getColor(R.color.orange_main))
            strokeWidth = 10f
            centerRadius = 60f
            start()
        }
        Glide.with(context)
            .load(images[position])
            .placeholder(placeHolderDrawable)
            .fallback(R.drawable.frame_black)
            .error(R.drawable.frame_black)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                    context.showToast(context.getString(R.string.common_network_error))
                    return false
                }

                override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    return false
                }
            })
            .into(holder.scalableImageView)
    }
}

class ImageDetailViewHolder(val scalableImageView: ScalableImageView) : RecyclerView.ViewHolder(scalableImageView)
