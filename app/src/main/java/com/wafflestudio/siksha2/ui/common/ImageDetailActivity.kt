package com.wafflestudio.siksha2.ui.common

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.wafflestudio.siksha2.R
import com.wafflestudio.siksha2.databinding.ActivityImageDetailBinding
import com.wafflestudio.siksha2.utils.setImageUrl
import com.wafflestudio.siksha2.utils.showToast
import java.util.ArrayList

class ImageDetailActivity: Activity() {

    companion object {
        private const val EXTRA_IMAGES = "EXTRA_IMAGES"

        fun createIntent(context: Context, images: List<String>): Intent = Intent(
            context, ImageDetailActivity::class.java
        ).apply {
            putStringArrayListExtra(EXTRA_IMAGES, ArrayList(images))
        }
    }

    lateinit var binding: ActivityImageDetailBinding

    private val images: List<String>? by lazy {
        intent.getStringArrayListExtra(EXTRA_IMAGES)
    }

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

        images?.let {
            binding.vpImages.adapter = ImageDetailAdapter(it)
            setPageText(binding.vpImages.currentItem + 1, it.size)
        }
        binding.vpImages.registerOnPageChangeCallback(onPageChangedCallback)
    }

    private fun setPageText(currentPage: Int, pageCount: Int) {
        binding.tvCurrentPage.text = getString(R.string.image_detail_current_page, currentPage, pageCount)
    }
}
