package com.wafflestudio.siksha2.ui.main.setting.favoriteMenu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.wafflestudio.siksha2.databinding.FragmentFavoriteMenuBinding
import com.wafflestudio.siksha2.ui.SikshaTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.wafflestudio.siksha2.preferences.SikshaPrefObjects
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class FavoriteMenuFragment : Fragment() {
    private lateinit var binding: FragmentFavoriteMenuBinding
    private val vm: FavoriteMenuViewModel by viewModels()
    @Inject
    lateinit var sikshaPrefObjects: SikshaPrefObjects

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoriteMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val token = sikshaPrefObjects.accessToken.getValue()

        vm.loadFavoriteMenus(token)
        //vm.loadMockData()

        binding.menuGroupList.setContent {
            SikshaTheme {
                val restaurants by vm.restaurants.collectAsState()
                FavoriteMenuRoute(
                    restaurants = restaurants,
                    onClickMenu = { /* 메뉴 클릭 */ },
                    onToggleLikeMenu = { id, liked -> vm.toggleLike(id, liked) },
                    onToggleFavoriteRestaurant = { /* 식당 즐겨찾기 */ }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            vm.restaurants.collectLatest { restaurants ->
                val isEmpty = restaurants.isEmpty()
                binding.emptyText.isVisible = isEmpty
                binding.menuGroupList.isGone = isEmpty
            }
        }

        // 뒤로가기 버튼
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        // 알림 설정 이동 버튼
        binding.alarmButton.setOnClickListener {
            val action = FavoriteMenuFragmentDirections.actionFavoriteMenuFragmentToNotifyMenuFragment()
            findNavController().navigate(action)
        }
    }
}

