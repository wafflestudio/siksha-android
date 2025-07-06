package com.wafflestudio.siksha2.ui.main.restaurant

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.wafflestudio.siksha2.FeatureChecker
import com.wafflestudio.siksha2.models.MealsOfDay
import com.wafflestudio.siksha2.models.Menu
import com.wafflestudio.siksha2.models.MenuGroup
import com.wafflestudio.siksha2.models.RestaurantInfo
import com.wafflestudio.siksha2.network.result.NetworkResult
import com.wafflestudio.siksha2.preferences.SikshaPrefObjects
import com.wafflestudio.siksha2.repositories.MenuRepository
import com.wafflestudio.siksha2.repositories.MixpanelManager
import com.wafflestudio.siksha2.repositories.RestaurantRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class DailyRestaurantViewModel @Inject constructor(
    private val menuRepository: MenuRepository,
    private val restaurantRepository: RestaurantRepository,
    private val sikshaPrefObjects: SikshaPrefObjects
) : ViewModel() {

    @Inject
    lateinit var mixpanelManager: MixpanelManager

    private val _dateFilter = MutableLiveData(LocalDate.now())
    val dateFilter: LiveData<LocalDate> = _dateFilter

    private val _mealsOfDayFilter = MutableLiveData(MealsOfDay.LU)
    val mealsOfDayFilter: LiveData<MealsOfDay> = _mealsOfDayFilter

    private val _isCalendarVisible = MutableLiveData(false)
    val isCalendarVisible: LiveData<Boolean> = _isCalendarVisible

    private val _currentLocation = MutableLiveData<Location?>(null)
    val currentLocation: LiveData<Location?> = _currentLocation

    private val _menuFilterCondition = MutableStateFlow(sikshaPrefObjects.menuFilterCondition.getValue())
    val menuFilterCondition: StateFlow<MenuFilterCondition> = _menuFilterCondition
        .stateIn(viewModelScope, SharingStarted.Eagerly, sikshaPrefObjects.menuFilterCondition.getValue())
    private val default = MenuFilterCondition.DEFAULT

    // TODO: Network Error (Timeout, 연걸 없음) 시 Toast?
    // 현재 앱 시작시에 Network 연결 없을 때 노티하는 중
    // 앱 사용 중에도 Network 연결 없어질 시 인지 할 수 있어야함.
    private val _networkError = MutableLiveData(false)
    val networkError: LiveData<Boolean> = _networkError

    private val _favoriteRestaurantExists = MutableLiveData(false)
    val favoriteRestaurantExists: LiveData<Boolean> = _favoriteRestaurantExists

    private val showEmptyRestaurant = restaurantRepository.showEmptyRestaurant.asFlow()
    private val restaurantOrder = restaurantRepository.restaurantsOrder.asFlow()
    private val favoriteRestaurantOrder = restaurantRepository.favoriteRestaurantsOrder.asFlow()
    private val allRestaurant = restaurantRepository.getAllRestaurantsFlow()

    @Inject
    lateinit var featureChecker: FeatureChecker

    private val _showFestival = MutableStateFlow(false)
    val showFestival: StateFlow<Boolean> = _showFestival

    init {
        startRefreshingMenus()
    }

    private fun startRefreshingMenus() {
        viewModelScope.launch {
            _dateFilter.asFlow()
                .conflate()
                .collect {
                    try {
                        menuRepository.syncWithServer(_dateFilter.value ?: LocalDate.now())
                    } catch (e: Exception) {
                        _networkError.value = true
                    }
                }
        }
    }

    fun toggleRestaurantFavorite(id: Long) {
        viewModelScope.launch {
            restaurantRepository.toggleRestaurantFavoriteById(id)
        }
    }

    suspend fun toggleMenuLike(id: Long, isCurrentlyLiked: Boolean): NetworkResult<Menu> {
        return when (isCurrentlyLiked) {
            true -> menuRepository.unlikeMenuById(id)
            false -> menuRepository.likeMenuById(id)
        }
    }

    fun setMealsOfDayFilter(mealsOfDay: MealsOfDay) {
        _mealsOfDayFilter.value = mealsOfDay
    }

    fun addDateOffset(offset: Long) {
        _dateFilter.value = _dateFilter.value?.plusDays(offset)
    }

    fun setDateFilter(date: LocalDate) {
        _dateFilter.value = date
    }

    fun toggleCalendarVisibility() {
        _isCalendarVisible.value = _isCalendarVisible.value?.not()
    }

    fun setCalendarVisibility(visibility: Boolean) {
        _isCalendarVisible.value = visibility
    }

    fun checkFavoriteRestaurantExists() {
        viewModelScope.launch {
            _favoriteRestaurantExists.value =
                restaurantRepository.getOrderedFavoriteRestaurants().isNotEmpty()
        }
    }

    fun updateLocation(location: Location?) {
        _currentLocation.value = location
    }

    fun toggleFestival() {
        _showFestival.value = !showFestival.value
    }

    private fun getDistance(menuGroup: MenuGroup): Float? {
        val result = FloatArray(1)
        val location = _currentLocation.value

        if (menuGroup.latitude == null || menuGroup.longitude == null || location == null) {
            return null
        }

        Location.distanceBetween(
            menuGroup.latitude,
            menuGroup.longitude,
            location.latitude,
            location.longitude,
            result
        )
        return result[0]
    }

    fun getCurrentCondition(): MenuFilterCondition {
        return _menuFilterCondition.value
    }

    fun setMenuFilterCondition(condition: MenuFilterCondition) {
        _menuFilterCondition.value = condition
        sikshaPrefObjects.menuFilterCondition.setValue(condition) // ✅ SharedPreferences에 저장
    }

    fun toggleOpenFilter() {
        val currentCondition = _menuFilterCondition.value
        val newCondition = currentCondition.copy(
            isOpen = !currentCondition.isOpen
        )
        setMenuFilterCondition(newCondition)
        trackInstantToggle("is_open_now", !currentCondition.isOpen)
    }

    fun toggleReviewFilter() {
        val currentCondition = _menuFilterCondition.value
        val newCondition = currentCondition.copy(
            hasReview = !currentCondition.hasReview
        )
        setMenuFilterCondition(newCondition)
        trackInstantToggle("has_reviews", !currentCondition.hasReview)
    }

    private fun trackInstantToggle(filterType: String, value: Boolean) {
        val pageName = if (favoriteRestaurantExists.value == true) {
            "favorites_list_page"
        } else {
            "store_list_page"
        }

        val props = JSONObject().apply {
            put("filter_type", filterType)
            put("filter_value", value)
            put("page_name", pageName)
        }

        mixpanelManager.track("instant_filter_toggled", props)
    }

    fun getFilteredMenuGroups(showOnlyFavorite: Boolean): Flow<List<MenuGroup>> {
        val menuBase = _dateFilter.asFlow()
            .flatMapLatest {
                menuRepository.getDailyMenuFlow(it)
            }
            .combine(_mealsOfDayFilter.asFlow()) { dailyMenuGroups, mealsOfDay ->
                if (dailyMenuGroups == null) {
                    emptyList()
                } else {
                    when (mealsOfDay!!) {
                        MealsOfDay.BR -> dailyMenuGroups.data.breakfast
                        MealsOfDay.LU -> dailyMenuGroups.data.lunch
                        MealsOfDay.DN -> dailyMenuGroups.data.dinner
                    }
                }
            }
            .combine(allRestaurant) { menuGroups, allRes ->
                val dateTime = LocalDateTime.now()
                val date = dateTime.toLocalDate()
                val time = dateTime.toLocalTime()
                menuGroups.map { menuGroup ->
                    menuGroup.copy(
                        isFavorite = allRes.find { menuGroup.id == it.id }?.isFavorite ?: false
                    )
                }.filter { menuGroup ->
                    val restaurantInfo = allRes.find { menuGroup.id == it.id }
                    val operatingHour = restaurantInfo?.etc?.operatingHours?.let {
                        when (date.dayOfWeek) {
                            DayOfWeek.SATURDAY -> it.saturday
                            DayOfWeek.SUNDAY -> it.holiday
                            else -> it.weekdays
                        }
                    }
                    !_menuFilterCondition.value.isOpen ||
                        (
                            !operatingHour.isNullOrEmpty() &&
                                operatingHour.any { interval ->
                                    val (start, end) = interval.split("-").map { LocalTime.parse(it) }
                                    time in start..end
                                }
                            )
                }
            }
            .map { it.filter { item -> item.isFavorite || showOnlyFavorite.not() } }
            .combine(if (showOnlyFavorite) favoriteRestaurantOrder else restaurantOrder) { menuGroups, (order) ->
                val result = mutableListOf<MenuGroup>()
                val sortedMenuGroups = menuGroups.sortedByDescending { it.id }
                order.forEach {
                    sortedMenuGroups.find { item -> item.id == it }?.also { result.add(it) }
                }
                result.addAll(sortedMenuGroups.filterNot { item -> item.id in order })
                result
            }
        val menuFestivalApplied = menuBase.map {
            it.filter { item ->
                item.nameKr!!.startsWith("[축제]") == showFestival.value
            }
        }
        val menuFilterApplied = if (featureChecker.isFeatureEnabled("filterFeatureEnabled")) {
            menuFestivalApplied
                // 사용자 필터
                .map { menuGroupList ->
                    menuGroupList.filter { item ->
                        _menuFilterCondition.value.distance == default.distance ||
                            getDistance(item)?.let {
                                it <= _menuFilterCondition.value.distance
                            } ?: true
                    }
                }
                .map { menuGroupList ->
                    menuGroupList.map { restaurant ->
                        val newRestaurant = restaurant.copy(
                            menus = restaurant.menus.filter { menu ->
                                menu.price?.let { menuPrice ->
                                    val minPrice = _menuFilterCondition.value.minPrice
                                    val maxPrice = _menuFilterCondition.value.maxPrice
                                    ((menuPrice >= minPrice) || (minPrice == default.minPrice)) &&
                                        ((menuPrice <= maxPrice) || (maxPrice == default.maxPrice))
                                } ?: true
                            }.filter { menu ->
                                when (menu.score) {
                                    null -> {
                                        !_menuFilterCondition.value.hasReview &&
                                            _menuFilterCondition.value.minRating == default.minRating
                                    }
                                    else -> {
                                        _menuFilterCondition.value.minRating <= menu.score
                                    }
                                }
                            }
                        )
                        newRestaurant
                    }
                }
                .map { menuGroupList ->
                    menuGroupList.map { restaurant ->
                        val newRestaurant = restaurant.copy(
                            menus = restaurant.menus.filter { menu ->
                                _menuFilterCondition.value.categories.let { selectedCategories ->
                                    selectedCategories.isEmpty() || selectedCategories.contains(menu.category)
                                }
                            }
                        )
                        newRestaurant
                    }
                }
                .combine(showEmptyRestaurant) { menuGroups, showEmpty ->
                    menuGroups.filter { it.menus.isNotEmpty() || showEmpty }
                }
        } else {
            menuFestivalApplied
        }
        return menuFilterApplied
    }

    suspend fun getRestaurantInfo(id: Long): RestaurantInfo? {
        return restaurantRepository.getRestaurantById(id)
    }

    suspend fun getMenuGroupById(menuGroupId: Long): MenuGroup? {
        return getFilteredMenuGroups(false)
            .map { menuGroups -> menuGroups.find { it.id == menuGroupId } }
            .firstOrNull()
    }
}
