package com.wafflestudio.siksha2.compose.ui.dailyrestaurant

import android.view.LayoutInflater
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.wafflestudio.siksha2.R
import com.wafflestudio.siksha2.databinding.LayoutFilterBinding
import com.wafflestudio.siksha2.models.MealsOfDay
import com.wafflestudio.siksha2.models.Menu
import com.wafflestudio.siksha2.models.MenuGroup
import com.wafflestudio.siksha2.models.RestaurantInfo
import com.wafflestudio.siksha2.ui.SikshaTheme
import com.wafflestudio.siksha2.ui.main.restaurant.DailyRestaurantViewModel
import com.wafflestudio.siksha2.ui.restaurantInfo.model.toRestaurantOperatingTimes
import com.wafflestudio.siksha2.utils.toPrettyString
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun DailyRestaurantRoute(
    vm: DailyRestaurantViewModel,
    isFavorite: Boolean,
    onRestaurantInfoClicked: (Long) -> Unit,
    onToggleFavoriteRestaurant: (Long) -> Unit,
    onRestaurantShareClicked: (Long) -> Unit,
    onClickMenu: (Long) -> Unit,
    onToggleLikeMenu: (Long, Boolean) -> Unit,
    setUpFilter: (LayoutFilterBinding) -> Unit,
    modifier: Modifier = Modifier
) {
    val menuGroupList by vm.getFilteredMenuGroups(isFavorite).collectAsState(initial = emptyList())
    val restaurantsList by vm.allRestaurant.collectAsState(initial = emptyList())
    val mealsOfDay by vm.mealsOfDayFilter.observeAsState()
    val dateFilter by vm.dateFilter.observeAsState()

    MenuGroupList(
        menuGroupList = menuGroupList,
        restaurantsList = restaurantsList,
        mealsOfDay = mealsOfDay ?: MealsOfDay.LU,
        dayOfWeek = (dateFilter ?: LocalDate.now()).dayOfWeek,
        onRestaurantInfoClicked, onToggleFavoriteRestaurant, onRestaurantShareClicked, onClickMenu, onToggleLikeMenu, setUpFilter, modifier
    )
}

@Composable
fun MenuGroupList(
    menuGroupList: List<MenuGroup?>,
    restaurantsList: List<RestaurantInfo>,
    mealsOfDay: MealsOfDay,
    dayOfWeek: DayOfWeek,
    onRestaurantInfoClicked: (Long) -> Unit,
    onToggleFavoriteRestaurant: (Long) -> Unit,
    onRestaurantShareClicked: (Long) -> Unit,
    onClickMenu: (Long) -> Unit,
    onToggleLikeMenu: (Long, Boolean) -> Unit,
    setUpFilter: (LayoutFilterBinding) -> Unit,
    modifier: Modifier = Modifier
) {
    val toolbarHeight = 60.dp
    val toolbarHeightPx = with(LocalDensity.current) { toolbarHeight.toPx() }
    val toolbarOffsetHeightPx = remember { mutableFloatStateOf(0f) }
    val listState = rememberLazyListState()
    val isScrollable by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            layoutInfo.totalItemsCount > 0 &&
                (
                    layoutInfo.visibleItemsInfo.firstOrNull()?.index != 0 ||
                        layoutInfo.visibleItemsInfo.lastOrNull()?.index != layoutInfo.totalItemsCount - 1
                    )
        }
    }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y
                val newOffset = (toolbarOffsetHeightPx.floatValue + delta)
                    .coerceIn(-toolbarHeightPx, 0f)
                toolbarOffsetHeightPx.floatValue = if (isScrollable) newOffset else 0f
                return Offset.Zero
            }
        }
    }

    LaunchedEffect(menuGroupList.size) {
        if (menuGroupList.isEmpty()) {
            toolbarOffsetHeightPx.floatValue = 0f
        }
    }

    Box(
        modifier = modifier.fillMaxSize()
            .background(SikshaTheme.colors.BackgroundMain)
            .nestedScroll(nestedScrollConnection)
    ) {
        if (menuGroupList.isNotEmpty()) {
            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(top = toolbarHeight),
                modifier = Modifier.fillMaxSize()
                    .padding(start = 8.dp, end = 8.dp, bottom = 17.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                items(menuGroupList) { menuGroup ->
                    if (menuGroup != null) {
                        val restaurant = restaurantsList.find { it.id == menuGroup.id }
                        val operatingHours = restaurant?.etc?.operatingHours?.toRestaurantOperatingTimes()
                        val dailyOperatingTime = when (dayOfWeek) {
                            DayOfWeek.SATURDAY -> operatingHours?.saturday
                            DayOfWeek.SUNDAY -> operatingHours?.holiday
                            else -> operatingHours?.weekdays
                        }
                        val operatingTime = when (mealsOfDay) {
                            MealsOfDay.BR -> dailyOperatingTime?.breakfast
                            MealsOfDay.LU -> dailyOperatingTime?.lunch
                            MealsOfDay.DN -> dailyOperatingTime?.dinner
                        }

                        RestaurantMenu(
                            menuGroup = menuGroup,
                            operatingTime = operatingTime?.toString() ?: "정보 없음",
                            mealsOfDay = mealsOfDay,
                            onRestaurantInfoClicked = { onRestaurantInfoClicked(menuGroup.id) },
                            onToggleFavoriteRestaurant = { onToggleFavoriteRestaurant(menuGroup.id) },
                            onRestaurantShareClicked = { onRestaurantShareClicked(menuGroup.id) },
                            onClickMenu = onClickMenu,
                            onToggleLikeMenu = onToggleLikeMenu
                        )
                    }
                }
            }
        } else {
            Text(
                text = stringResource(R.string.daily_restaurant_no_menus),
                color = SikshaTheme.colors.Gray700,
                fontSize = 14.sp,
                fontWeight = FontWeight.Light,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        AndroidView(
            factory = { context ->
                val binding = LayoutFilterBinding.inflate(LayoutInflater.from(context), null, false)
                setUpFilter(binding)
                binding.root
            },
            modifier = Modifier
                .height(toolbarHeight)
                .align(Alignment.TopCenter)
                .offset { IntOffset(x = 0, y = toolbarOffsetHeightPx.floatValue.roundToInt()) }
        )
    }
}

@Composable
fun RestaurantMenu(
    menuGroup: MenuGroup,
    operatingTime: String,
    mealsOfDay: MealsOfDay,
    onRestaurantInfoClicked: () -> Unit,
    onToggleFavoriteRestaurant: () -> Unit,
    onRestaurantShareClicked: () -> Unit,
    onClickMenu: (Long) -> Unit,
    onToggleLikeMenu: (Long, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(SikshaTheme.colors.White, shape = RoundedCornerShape(8.dp))
            .border(width = 1.dp, color = SikshaTheme.colors.Gray200, shape = RoundedCornerShape(8.dp))
            .padding(horizontal = 14.dp, vertical = 18.dp),
        horizontalAlignment = Alignment.Start
    ) {
        RestaurantInfoRow(
            restaurantName = menuGroup.nameKr,
            isFavorite = menuGroup.isFavorite,
            onRestaurantInfoClicked = onRestaurantInfoClicked,
            onToggleFavoriteRestaurant = onToggleFavoriteRestaurant,
            onRestaurantShareClicked = onRestaurantShareClicked
        )
        Spacer(modifier = Modifier.height(11.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(
                        when (mealsOfDay) {
                            MealsOfDay.BR -> R.drawable.ic_menu_breakfast
                            MealsOfDay.LU -> R.drawable.ic_menu_lunch
                            MealsOfDay.DN -> R.drawable.ic_menu_dinner
                        }
                    ),
                    colorFilter = ColorFilter.tint(SikshaTheme.colors.Gray600),
                    contentDescription = null
                )
                Text(
                    text = operatingTime,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = SikshaTheme.colors.Gray600
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.menu_group_price),
                    fontSize = 12.sp,
                    color = SikshaTheme.colors.Orange500
                )
                Text(
                    text = stringResource(R.string.menu_group_rate),
                    fontSize = 12.sp,
                    color = SikshaTheme.colors.Orange500
                )
                Text(
                    text = stringResource(R.string.menu_group_like),
                    fontSize = 12.sp,
                    color = SikshaTheme.colors.Orange500
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Divider(
            modifier = Modifier.fillMaxWidth(),
            color = SikshaTheme.colors.Orange500,
            thickness = (1.5).dp
        )
        Spacer(modifier = Modifier.height(14.dp))
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            menuGroup.menus.forEach { menu ->
                MenuRow(
                    menu = menu,
                    onClickMenu = { onClickMenu(menu.id) },
                    onToggleLikeMenu = { menu.isLiked?.let { onToggleLikeMenu(menu.id, it) } }
                )
            }
        }
    }
}

@Composable
fun RestaurantInfoRow(
    restaurantName: String?,
    isFavorite: Boolean,
    onRestaurantInfoClicked: () -> Unit,
    onToggleFavoriteRestaurant: () -> Unit,
    onRestaurantShareClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = restaurantName ?: "식당 이름 없음",
            color = SikshaTheme.colors.Black,
            fontSize = 16.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Image(
                modifier = Modifier.size(20.dp)
                    .clickable { onRestaurantInfoClicked() },
                painter = painterResource(R.drawable.ic_info),
                contentDescription = "식당 정보"
            )
            Image(
                modifier = Modifier.size(20.dp)
                    .clickable { onToggleFavoriteRestaurant() },
                painter = if (isFavorite) painterResource(R.drawable.ic_favorite_full) else painterResource(R.drawable.ic_favorite_empty),
                contentDescription = "식당 즐겨찾기"
            )
            Image(
                modifier = Modifier.size(20.dp)
                    .clickable { onRestaurantShareClicked() },
                painter = painterResource(R.drawable.ic_share),
                contentDescription = "식단 공유"
            )
        }
    }
}

@Composable
fun MenuRow(
    menu: Menu,
    onClickMenu: () -> Unit,
    onToggleLikeMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            modifier = Modifier.weight(1f)
                .clickable { onClickMenu() },
            text = menu.nameKr ?: "정보 없음",
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
                text = menu.price?.toPrettyString() ?: "0.0",
                color = SikshaTheme.colors.Black,
                fontSize = 14.sp
            )
            Text(
                text = menu.score?.let { String.format(Locale.getDefault(), "%.1f", it) } ?: "0.0",
                color = SikshaTheme.colors.Black,
                fontSize = 14.sp
            )
            Image(
                modifier = Modifier.size(24.dp)
                    .clickable { onToggleLikeMenu() },
                painter = painterResource(
                    if (menu.isLiked == true) {
                        R.drawable.ic_heart_filled
                    } else {
                        R.drawable.ic_heart_outline
                    }
                ),
                contentDescription = "메뉴 찜하기"
            )
        }
    }
}

@Preview
@Composable
fun MenuGroupListPreview() {
    RestaurantMenu(
        menuGroup = MenuGroup(
            id = 0L,
            restaurantCode = "",
            nameKr = "학생회관 식당",
            nameEn = null,
            address = null,
            longitude = null,
            latitude = null,
            menus = listOf(
                Menu(
                    id = 0L,
                    code = "code",
                    date = LocalDate.MIN,
                    type = MealsOfDay.LU,
                    restaurantId = 0L,
                    nameKr = "불고기 불고기 불고기 불고기 불고기 불고기 불고기 불고기",
                    nameEn = null,
                    price = 4000L,
                    score = 4.5,
                    etc = null,
                    reviewCount = 3,
                    likeCount = 10,
                    isLiked = true,
                    category = null
                ),
                Menu(
                    id = 0L,
                    code = "code",
                    date = LocalDate.MIN,
                    type = MealsOfDay.LU,
                    restaurantId = 0L,
                    nameKr = "불고기 불고기 불고기 불고기 불고기 불고기 불고기 불고기",
                    nameEn = null,
                    price = 4000L,
                    score = 4.5,
                    etc = null,
                    reviewCount = 3,
                    likeCount = 10,
                    isLiked = false,
                    category = null
                )
            ),
            isFavorite = false
        ),
        operatingTime = "11:00 - 13:00",
        mealsOfDay = MealsOfDay.LU,
        onRestaurantShareClicked = {},
        onRestaurantInfoClicked = {},
        onToggleFavoriteRestaurant = {},
        onClickMenu = {},
        onToggleLikeMenu = { _, _ -> }
    )
}
