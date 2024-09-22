package com.wafflestudio.siksha2.ui.common

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.wafflestudio.siksha2.R
import com.wafflestudio.siksha2.databinding.ActivityImageViewerBinding
import com.wafflestudio.siksha2.utils.setVisibleOrGone
import java.util.ArrayList

class ImageViewerActivity : Activity() {

    companion object {
        private const val EXTRA_IMAGES = "EXTRA_IMAGES"
        private const val EXTRA_INITIAL_PAGE = "EXTRA_INITIAL_PAGE"

        fun createIntent(context: Context, images: List<String>, initialPage: Int): Intent = Intent(
            context,
            ImageViewerActivity::class.java
        ).apply {
            putStringArrayListExtra(EXTRA_IMAGES, ArrayList(images))
            putExtra(EXTRA_INITIAL_PAGE, initialPage)
        }
    }

    lateinit var binding: ActivityImageViewerBinding

    private val images: List<String>? by lazy {
        intent.getStringArrayListExtra(EXTRA_IMAGES)
    }

    private val initialPage: Int by lazy {
        intent.getIntExtra(EXTRA_INITIAL_PAGE, 0)
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
        binding = ActivityImageViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        images?.let {
            binding.vpImages.adapter = ImageViewerAdapter(
                images = it,
                onSingleTapUp = {
                    toggleHeaderVisibility()
                }
            )
            binding.vpImages.setCurrentItem(initialPage, false)
            setPageText(binding.vpImages.currentItem + 1, it.size)
        }
        binding.vpImages.registerOnPageChangeCallback(onPageChangedCallback)
        binding.ivClose.setOnClickListener {
            finish()
        }
    }

    private fun setPageText(currentPage: Int, pageCount: Int) {
        binding.tvCurrentPage.text = getString(R.string.image_viewer_current_page, currentPage, pageCount)
    }

    private fun toggleHeaderVisibility() {
        binding.gHeader.setVisibleOrGone(binding.gHeader.isVisible.not())
    }
}
