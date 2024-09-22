package com.wafflestudio.siksha2.ui.common

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class ImageViewerAdapter(
    private val images: List<String>,
    private val onSingleTapUp: () -> Unit
) : RecyclerView.Adapter<ImageViewerViewHolder>() {
    override fun getItemCount(): Int = images.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewerViewHolder {
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
        return ImageViewerViewHolder(nestedScrollableHost)
    }

    override fun onBindViewHolder(holder: ImageViewerViewHolder, position: Int) {
        val scalableImageView = holder.nestedScrollableHost.getChildAt(0) as? ScalableImageView
        scalableImageView?.setModel(images[position])
        scalableImageView?.setOnSingleTapUpListener(onSingleTapUp)
    }
}

class ImageViewerViewHolder(
    val nestedScrollableHost: NestedScrollableHost
) : RecyclerView.ViewHolder(nestedScrollableHost)
