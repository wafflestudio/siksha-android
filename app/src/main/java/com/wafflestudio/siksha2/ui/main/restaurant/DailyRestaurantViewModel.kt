package com.wafflestudio.siksha2.ui.main.restaurant

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.*
import com.wafflestudio.siksha2.models.MealsOfDay
import com.wafflestudio.siksha2.models.Menu
import com.wafflestudio.siksha2.models.MenuGroup
import com.wafflestudio.siksha2.models.RestaurantInfo
import com.wafflestudio.siksha2.repositories.MenuRepository
import com.wafflestudio.siksha2.repositories.RestaurantRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
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

    // TODO: Network Error (Timeout, 연걸 없음) 시 Toast?
    // 현재 앱 시작시에 Network 연결 없을 때 노티하는 중
    // 앱 사용 중에도 Network 연결 없어질 시 인지 할 수 있어야함.
    private val _networkError = MutableLiveData(false)
    val networkError: LiveData<Boolean> = _networkError

    private val _favoriteRestaurantExists = MutableLiveData(false)
    val favoriteRestaurantExists: LiveData<Boolean> = _favoriteRestaurantExists

    private val _updatedMenuItemStream = MutableLiveData<Menu?>(null)
    val updatedMenuItemStream: LiveData<Menu?> = _updatedMenuItemStream

    private val showEmptyRestaurant = restaurantRepository.showEmptyRestaurant.asFlow()
    private val restaurantOrder = restaurantRepository.restaurantsOrder.asFlow()
    private val favoriteRestaurantOrder = restaurantRepository.favoriteRestaurantsOrder.asFlow()
    private val allRestaurant = restaurantRepository.getAllRestaurantsFlow()

    fun toggleFavorite(id: Long) {
        viewModelScope.launch {
            restaurantRepository.toggleRestaurantFavoriteById(id)
        }
    }

    fun toggleLike(id: Long, isCurrentlyLiked: Boolean) {
        viewModelScope.launch {
            val menuItem = menuRepository.getMenuById(id)
            menuItem.isLiked = !isCurrentlyLiked
            _updatedMenuItemStream.postValue(menuItem)
            val serverMenuItem = menuRepository.toggleLike(id, isCurrentlyLiked)
            if (serverMenuItem.isLiked != menuItem.isLiked) {
                Log.d(TAG, "server sync inconsistent")
                _updatedMenuItemStream.postValue(serverMenuItem)
            }
        }
    }

    fun setMealsOfDayFilter(mealsOfDay: MealsOfDay) {
        viewModelScope.launch {
            _mealsOfDayFilter.value = mealsOfDay
            Log.d(TAG, "vm/ We'll start refreshing data!")
            startRefreshingData()
        }
    }

    fun addDateOffset(offset: Long) {
        viewModelScope.launch {
            _dateFilter.value = _dateFilter.value?.plusDays(offset)
            Log.d(TAG, "vm/ We'll start refreshing data!")
            startRefreshingData()
        }
    }

    fun setDateFilter(date: LocalDate) {
        viewModelScope.launch {
            _dateFilter.value = date
            Log.d(TAG, "vm/ We'll start refreshing data!")
            startRefreshingData()
        }
    }

    fun toggleCalendarVisibility() {
        viewModelScope.launch {
            _isCalendarVisible.value = _isCalendarVisible.value?.not()
        }
    }

    fun setCalendarVisibility(visibility: Boolean) {
        viewModelScope.launch {
            _isCalendarVisible.value = visibility
        }
    }

    fun startRefreshingData() {
        viewModelScope.launch {
            try {
                restaurantRepository.syncWithServer()
                dateFilter.asFlow()
                    .conflate()
                    .collect {
                        menuRepository.syncWithServer(_dateFilter.value ?: LocalDate.now())
                    }
            } catch (e: Exception) {
                _networkError.value = true
            }
        }
    }

    fun checkFavoriteRestaurantExists() {
        viewModelScope.launch {
            _favoriteRestaurantExists.value =
                restaurantRepository.getOrderedFavoriteRestaurants().isNotEmpty()
        }
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
                menuGroups.map { item ->
                    item.copy(
                        isFavorite = allRes.find { item.id == it.id }?.isFavorite ?: false
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
    }

    suspend fun getRestaurantInfo(id: Long): RestaurantInfo? {
        return restaurantRepository.getRestaurantById(id)
    }
}
