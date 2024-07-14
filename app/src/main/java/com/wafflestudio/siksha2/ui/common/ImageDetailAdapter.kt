package com.wafflestudio.siksha2.ui.common

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class ImageDetailAdapter(private val images: List<String>) : RecyclerView.Adapter<ImageDetailViewHolder>() {
    override fun getItemCount(): Int = images.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageDetailViewHolder {
        val nestedScrollableHost = NestedScrollableHost(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            addView(
                ScalableImageView(parent.context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
            )
        }
        return ImageDetailViewHolder(nestedScrollableHost)
    }

    override fun onBindViewHolder(holder: ImageDetailViewHolder, position: Int) {
        val scalableImageView = holder.nestedScrollableHost.getChildAt(0) as? ScalableImageView
        scalableImageView?.setModel(images[position])
    }
}

class ImageDetailViewHolder(
    val nestedScrollableHost: NestedScrollableHost
) : RecyclerView.ViewHolder(nestedScrollableHost)
