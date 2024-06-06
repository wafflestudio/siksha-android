package com.wafflestudio.siksha2.ui.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.wafflestudio.siksha2.databinding.FragmentImageDetailBinding

class ImageDetailFragment : Fragment() {
    private var _binding: FragmentImageDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentImageDetailBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Glide.with(this)
            .load("https://picsum.photos/200/300")
            .into(binding.sivImage)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
