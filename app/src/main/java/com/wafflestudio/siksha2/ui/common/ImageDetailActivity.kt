package com.wafflestudio.siksha2.ui.common

import android.app.Activity
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.wafflestudio.siksha2.R
import com.wafflestudio.siksha2.databinding.ActivityImageDetailBinding
import com.wafflestudio.siksha2.utils.showToast

class ImageDetailActivity: Activity() {
    lateinit var binding: ActivityImageDetailBinding

    private val images = listOf(
        "https://picsum.photos/400/200"
    )

    private val onPageChangedCallback = object : OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            images?.let {
                setPageText(position + 1, it.size)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        images?.let {
//            binding.vpImages.adapter = ImageDetailAdapter(it)
//            setPageText(binding.vpImages.currentItem + 1, it.size)
//        }
//        binding.vpImages.registerOnPageChangeCallback(onPageChangedCallback)
//        binding.vpImages.requestDisallowInterceptTouchEvent(true)
        Glide.with(this)
            .load("https://picsum.photos/400/200")
            .fallback(R.drawable.frame_black)
            .error(R.drawable.frame_black)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                    showToast(getString(R.string.common_network_error))
                    return false
                }

                override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    return false
                }
            })
            .into(binding.scalableImageView)
    }

    private fun setPageText(currentPage: Int, pageCount: Int) {
        binding.tvCurrentPage.text = getString(R.string.image_detail_current_page, currentPage, pageCount)
    }
}
