package com.wafflestudio.siksha2.ui.main.setting.favoriteMenu

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wafflestudio.siksha2.R
import com.wafflestudio.siksha2.compose.ui.dailyrestaurant.RestaurantInfoRow
import com.wafflestudio.siksha2.network.dto.FavoriteMenuDto
import com.wafflestudio.siksha2.network.dto.FavoriteRestaurantDto
import com.wafflestudio.siksha2.ui.SikshaTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row

@Composable
fun FavoriteMenuRoute(
    restaurants: List<FavoriteRestaurantDto>,
    onClickMenu: (Long) -> Unit,
    onToggleLikeMenu: (Long, Boolean) -> Unit,
    onRestaurantInfoClicked: (Long) -> Unit,
    onToggleFavoriteRestaurant: (Long) -> Unit,
    onRestaurantShareClicked: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    if (restaurants.isNotEmpty()) {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            items(restaurants) { restaurant ->
                RestaurantMenuFavorite(
                    restaurant = restaurant,
                    onClickMenu = onClickMenu,
                    onToggleLikeMenu = onToggleLikeMenu,
                    onRestaurantInfoClicked = { onRestaurantInfoClicked(restaurant.id) },
                    onToggleFavoriteRestaurant = { onToggleFavoriteRestaurant(restaurant.id) },
                    onRestaurantShareClicked = { onRestaurantShareClicked(restaurant.id) }
                )
            }
        }
    }
}

@Composable
fun RestaurantMenuFavorite(
    restaurant: FavoriteRestaurantDto,
    onClickMenu: (Long) -> Unit,
    onToggleLikeMenu: (Long, Boolean) -> Unit,
    onRestaurantInfoClicked: () -> Unit,
    onToggleFavoriteRestaurant: () -> Unit,
    onRestaurantShareClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .background(SikshaTheme.colors.White, shape = RoundedCornerShape(8.dp))
            .border(width = 1.dp, color = SikshaTheme.colors.Gray200, shape = RoundedCornerShape(8.dp))
            .padding(horizontal = 14.dp, vertical = 18.dp),
        horizontalAlignment = Alignment.Start
    ) {
        // 상단: 식당 이름 + 버튼들
        RestaurantInfoRow(
            restaurantName = restaurant.name_kr,
            isFavorite = false, // 즐겨찾기 여부는 FavoriteRestaurantDto에 따라 세팅
            onRestaurantInfoClicked = onRestaurantInfoClicked,
            onToggleFavoriteRestaurant = onToggleFavoriteRestaurant,
            onRestaurantShareClicked = onRestaurantShareClicked
        )
        Spacer(modifier = Modifier.height(8.dp))
        Divider(color = SikshaTheme.colors.Orange500, thickness = 1.5.dp)
        Spacer(modifier = Modifier.height(14.dp))

        // 메뉴 리스트
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            restaurant.menus.forEach { menu ->
                MenuRow(
                    menu = menu,
                    onClickMenu = { onClickMenu(menu.id) },
                    onToggleLikeMenu = { onToggleLikeMenu(menu.id, menu.is_liked) }
                )
            }
        }
    }
}

@Composable
fun MenuRow(
    menu: FavoriteMenuDto,
    onClickMenu: () -> Unit,
    onToggleLikeMenu: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            modifier = Modifier.weight(1f)
                .clickable { onClickMenu() },
            text = menu.name_kr,
            color = SikshaTheme.colors.Black,
            fontSize = 15.sp
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (menu.etc?.contains("No meat") == true) {
                Image(
                    painter = painterResource(R.drawable.ic_no_fork),
                    contentDescription = "No meat"
                )
            }
            Text(
                text = "${menu.price ?: 0}원",
                color = SikshaTheme.colors.Black,
                fontSize = 14.sp
            )
            Text(
                text = "${menu.score ?: 0.0}",
                color = SikshaTheme.colors.Black,
                fontSize = 14.sp
            )
            Image(
                modifier = Modifier.size(24.dp).clickable { onToggleLikeMenu() },
                painter = painterResource(
                    if (menu.is_liked) R.drawable.ic_heart_filled
                    else R.drawable.ic_heart_outline
                ),
                contentDescription = "메뉴 찜하기"
            )
        }
    }
}
