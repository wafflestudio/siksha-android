package com.wafflestudio.siksha2.ui.main.setting.favoriteMenu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material.MaterialTheme
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wafflestudio.siksha2.preferences.SikshaPrefObjects
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

        binding.menuGroupList.setContent {
            SikshaTheme {
                val restaurants by vm.restaurants.collectAsState()

                if (restaurants.isEmpty()) {
                    binding.emptyText.visibility = View.VISIBLE
                } else {
                    binding.emptyText.visibility = View.GONE

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(restaurants) { restaurant ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                // 식당명
                                Text(
                                    text = restaurant.name_kr,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )

                                // 메뉴 리스트
                                restaurant.menus.forEach { menu ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = menu.name_kr,
                                            fontSize = 15.sp,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Text(
                                            text = "${menu.price ?: 0}원",
                                            fontSize = 14.sp
                                        )
                                    }
                                }
                                Divider(modifier = Modifier.padding(top = 8.dp))
                            }
                        }
                    }
                }
            }
        }

        // 뒤로가기 버튼
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}

