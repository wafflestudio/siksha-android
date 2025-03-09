package com.wafflestudio.siksha2.ui.main.restaurant

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.wafflestudio.siksha2.models.MealsOfDay
import com.wafflestudio.siksha2.models.Menu
import com.wafflestudio.siksha2.models.MenuGroup
import com.wafflestudio.siksha2.models.RestaurantInfo
import com.wafflestudio.siksha2.network.result.NetworkResult
import com.wafflestudio.siksha2.repositories.MenuRepository
import com.wafflestudio.siksha2.repositories.RestaurantRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class DailyRestaurantViewModel @Inject constructor(
    private val menuRepository: MenuRepository,
    private val restaurantRepository: RestaurantRepository
) : ViewModel() {

    private val _dateFilter = MutableLiveData<LocalDate>(LocalDate.now())
    val dateFilter: LiveData<LocalDate> = _dateFilter

    private val _mealsOfDayFilter = MutableLiveData<MealsOfDay>(MealsOfDay.LU)
    val mealsOfDayFilter: LiveData<MealsOfDay> = _mealsOfDayFilter

    private val _isCalendarVisible = MutableLiveData<Boolean>(false)
    val isCalendarVisible: LiveData<Boolean> = _isCalendarVisible

    private val _currentLocation = MutableLiveData<Location?>(null)
    val currentLocation: LiveData<Location?> = _currentLocation

    private val _menuFilterCondition = MutableLiveData<MenuFilterCondition>(
        MenuFilterCondition(null, null, null, false, false, null, null)
    )
    val menuFilterCondition: LiveData<MenuFilterCondition> = _menuFilterCondition

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
        Timber.d("(${_currentLocation.value?.latitude}, ${_currentLocation.value?.longitude})")
    }

    fun setDistance(distance: Float?) {
        _menuFilterCondition.value = _menuFilterCondition.value?.copy(distance = distance)
    }

    fun getDistance(menuGroup: MenuGroup): Float? {
        val result = FloatArray(1)
        if (menuGroup.latitude == null || menuGroup.longitude == null) return null
        Location.distanceBetween(menuGroup.latitude, menuGroup.longitude, _currentLocation.value!!.latitude, _currentLocation.value!!.longitude, result)
        return result[0]
    }

    fun setMinPrice(minPrice: Float?) {
        _menuFilterCondition.value = _menuFilterCondition.value?.copy(minPrice = minPrice)
    }

    fun setMaxPrice(maxPrice: Float?) {
        _menuFilterCondition.value = _menuFilterCondition.value?.copy(maxPrice = maxPrice)
    }

    fun setIsOpen(isOpen: Boolean) {
        _menuFilterCondition.value = _menuFilterCondition.value?.copy(isOpen = isOpen)
    }

    fun toggleOpenFilter() {
        _menuFilterCondition.value = _menuFilterCondition.value?.copy(
            isOpen = _menuFilterCondition.value?.isOpen?.not() ?: false
        )
    }

    fun setHasReview(hasReview: Boolean) {
        _menuFilterCondition.value = _menuFilterCondition.value?.copy(hasReview = hasReview)
    }

    fun toggleReviewFilter() {
        _menuFilterCondition.value = _menuFilterCondition.value?.copy(
            hasReview = _menuFilterCondition.value?.hasReview?.not() ?: false
        )
    }

    fun setMinRating(minRating: Float?) {
        _menuFilterCondition.value = _menuFilterCondition.value?.copy(minRating = minRating)
    }

    fun setCategories(categories: List<String>?) {
        _menuFilterCondition.value = _menuFilterCondition.value?.copy(categories = categories)
    }

    fun getFilteredMenuGroups(showOnlyFavorite: Boolean): Flow<List<MenuGroup>> {
        return _dateFilter.asFlow()
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
            .combine(showEmptyRestaurant) { menuGroups, showEmpty ->
                menuGroups.filter { it.menus.isNotEmpty() || showEmpty }
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
                    if (operatingHour.isNullOrEmpty()) {
                        true
                    } else {
                        operatingHour.any { interval ->
                            val (start, end) = interval.split("-").map { LocalTime.parse(it) }
                            time in start..end
                        }
                    }
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
            // 사용자 필터
            .map { menuGroupList ->
                _menuFilterCondition.value?.distance?.let {
                    menuGroupList.filter { item ->
                        getDistance(item)?.let {
                            it <= _menuFilterCondition.value?.distance!!
                        } ?: true
                    }
                } ?: menuGroupList
            }
            .map { menuGroupList ->
                menuGroupList.map { restaurant ->
                    val newRestaurant = restaurant.copy(
                        menus = restaurant.menus.filter { menu ->
                            menu.price?.let { menuPrice ->
                                (
                                    _menuFilterCondition.value?.maxPrice?.let {
                                        menuPrice <= it
                                    } ?: true
                                    ) &&
                                    (
                                        _menuFilterCondition.value?.minPrice?.let {
                                            menuPrice >= it
                                        } ?: true
                                        )
                            } ?: true
                        }.filter { menu ->
                            when (menu.score) {
                                null -> {
                                    _menuFilterCondition.value?.hasReview?.let { !it } ?: true
                                }
                                else -> {
                                    _menuFilterCondition.value?.hasReview ?: true &&
                                        _menuFilterCondition.value?.minRating?.let {
                                            menu.score >= it
                                        } ?: true
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
                            val priceCheck = menu.price?.let { menuPrice ->
                                (_menuFilterCondition.value?.maxPrice?.let { menuPrice <= it } ?: true) &&
                                    (_menuFilterCondition.value?.minPrice?.let { menuPrice >= it } ?: true)
                            } ?: true

                            val categoryCheck = _menuFilterCondition.value?.categories?.let { selectedCategories ->
                                selectedCategories.isEmpty() || selectedCategories.contains(menu.category)
                            } ?: true

                            priceCheck && categoryCheck
                        }
                    )
                    newRestaurant
                }
            }
    }

    suspend fun getRestaurantInfo(id: Long): RestaurantInfo? {
        return restaurantRepository.getRestaurantById(id)
    }

    suspend fun getMenuGroupById(menuGroupId: Long): MenuGroup? {
        return getFilteredMenuGroups(false)
            .map { menuGroups -> menuGroups.find { it.id == menuGroupId } }
            .firstOrNull()
    }

    data class MenuFilterCondition(
        val distance: Float?,
        val minPrice: Float?,
        val maxPrice: Float?,
        val isOpen: Boolean,
        val hasReview: Boolean,
        val minRating: Float?,
        val categories: List<String>?
    )
}
