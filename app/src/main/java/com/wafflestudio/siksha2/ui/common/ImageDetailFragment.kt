package com.wafflestudio.siksha2.ui.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.wafflestudio.siksha2.databinding.FragmentImageDetailBinding

class ImageDetailFragment : Fragment() {

    companion object {
        private const val ARG_IMAGES = "ARG_IMAGES"

        fun newInstance(images: ArrayList<String>): ImageDetailFragment =
            ImageDetailFragment().apply {
                arguments = Bundle().apply {
                    putStringArrayList(ARG_IMAGES, images)
                }
            }
    }

    private var _binding: FragmentImageDetailBinding? = null
    private val binding get() = _binding!!

    private val images by lazy { arguments?.getStringArrayList(ARG_IMAGES) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentImageDetailBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        images?.let {
            binding.vpImages.adapter = ImageDetailAdapter(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
