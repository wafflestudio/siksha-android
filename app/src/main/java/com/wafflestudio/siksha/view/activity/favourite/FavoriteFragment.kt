package com.wafflestudio.siksha.view.activity.favourite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wafflestudio.siksha.R
import com.wafflestudio.siksha.util.visible
import com.wafflestudio.siksha.view.activity.main.MainFragment
import kotlinx.android.synthetic.main.fragment_main.*

class FavoriteFragment : MainFragment() {

  companion object {
    fun newInstance(): FavoriteFragment = FavoriteFragment()
  }

  override fun refresh() {
    super.refresh()
    initViews()
  }

  override val onlyFavorites: Boolean get() = true

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
      inflater.inflate(R.layout.fragment_main, container, false)

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initViews()
  }

  private fun initViews() {
    preference.favorite.isEmpty().let {
      layout_no_favorite.visible = it
      layout_main.visible = !it
    }
  }
}