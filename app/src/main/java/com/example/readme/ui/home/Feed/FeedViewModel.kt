package com.example.readme.ui.home.Feed

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.readme.ui.data.entities.like.LikeResponse
import com.example.readme.data.entities.category.CategoryFeedResponse
import com.example.readme.data.entities.inithome.FeedInfo
import com.example.readme.data.entities.inithome.MainInfoResponse
import com.example.readme.data.entities.inithome.ShortsInfo
import com.example.readme.utils.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FeedViewModel : ViewModel() {

    private val _feeds = MutableLiveData<List<FeedInfo>>()
    val feeds: LiveData<List<FeedInfo>> get() = _feeds

    private val _shorts = MutableLiveData<List<ShortsInfo>>()
    val shorts: LiveData<List<ShortsInfo>> get() = _shorts

    private val _categoryFeeds = MutableLiveData<List<com.example.readme.data.entities.category.FeedInfo>>()
    val categoryFeeds: LiveData<List<com.example.readme.data.entities.category.FeedInfo>> get() = _categoryFeeds

    private val _categories = MutableLiveData<List<String>>()
    val categories: LiveData<List<String>> get() = _categories

    private val _combinedData = MediatorLiveData<Pair<List<FeedInfo>, List<ShortsInfo>>>()
    val combinedData: LiveData<Pair<List<FeedInfo>, List<ShortsInfo>>> get() = _combinedData

    private var cachedFeeds: List<FeedInfo>? = null
    private var cachedShorts: List<ShortsInfo>? = null
    private var cachedCategories: List<String>? = null

    init {
        _combinedData.value = Pair(emptyList(), emptyList())

        _combinedData.addSource(feeds) { feeds ->
            val shorts = _shorts.value
            _combinedData.value = Pair(feeds ?: emptyList(), shorts ?: emptyList())
        }

        _combinedData.addSource(shorts) { shorts ->
            val feeds = _feeds.value
            _combinedData.value = Pair(feeds ?: emptyList(), shorts ?: emptyList())
        }
    }

    fun fetchFeeds() {
        viewModelScope.launch {
            try {
                if (cachedFeeds == null || cachedShorts == null || cachedCategories == null) {
                    val response = withContext(Dispatchers.IO) {
                        RetrofitClient.getMainInfoService().getMainInfo()
                    }
                    if (response.body()?.isSuccess == true) {
                        val result = response.body()?.result
                        Log.d("FeedViewModel", "Fetched feeds: ${result?.feeds}")

                        val newCategories = result?.categories ?: emptyList()
                        if (_categories.value != newCategories) {
                            _categories.value = newCategories
                        }

                        val newFeeds = result?.feeds ?: emptyList()
                        if (cachedFeeds != newFeeds) {
                            cachedFeeds = newFeeds
                            _feeds.value = newFeeds
                        }

                        val newShorts = result?.shorts ?: emptyList()
                        if (cachedShorts != newShorts) {
                            cachedShorts = newShorts
                            _shorts.value = newShorts
                        }
                    } else {
                        Log.d("FeedViewModel", "Response not successful")
                    }
                } else {
                    _feeds.value = cachedFeeds!!
                    _shorts.value = cachedShorts!!
                    _categories.value = cachedCategories!!
                }
            } catch (e: Exception) {
                Log.d("FeedViewModel", "Failed to fetch data: ${e.message}")
            }
        }
    }

    fun fetchCategoryFeeds(category: String) {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.getMainInfoService().getCategoryFeeds(1, 20, category)
                }
                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    val feedList = response.body()?.result ?: emptyList()
                    if (_categoryFeeds.value != feedList) {
                        _categoryFeeds.value = feedList
                    }
                    Log.d("anothor", "${feedList}")
                } else {
                    Log.d("FeedViewModel", "Response not successful")
                }
            } catch (e: Exception) {
                Log.d("FeedViewModel", "Failed to fetch data: ${e.message}")
            }
        }
    }

    fun likeShorts(feed: FeedInfo) {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.getMainInfoService().likeShorts(feed.shortsId)
                }
                if (response.body()?.isSuccess == true) {
                    val updatedFeed = _feeds.value?.find { it.shortsId == feed.shortsId }
                    updatedFeed?.let {
                        val newFeed = it.copy(isLike = !it.isLike, likeCnt = response.body()?.result ?: it.likeCnt)
                        val updatedFeeds = _feeds.value?.map { if (it.shortsId == feed.shortsId) newFeed else it } ?: emptyList()
                        _feeds.value = updatedFeeds
                        Log.d("FeedViewModel", "Like updated for feed: ${feed.shortsId}")
                    }
                } else {
                    Log.d("FeedViewModel", "Like update failed: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.d("FeedViewModel", "Failed to update like status: ${e.message}")
            }
        }
    }

    fun likeShorts2(categoryFeeds: com.example.readme.data.entities.category.FeedInfo) {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.getMainInfoService().likeShorts(categoryFeeds.shortsId)
                }
                if (response.body()?.isSuccess == true) {
                    val updatedFeed = _categoryFeeds.value?.find { it.shortsId == categoryFeeds.shortsId }
                    updatedFeed?.let {
                        val newFeed = it.copy(isLike = !it.isLike, likeCnt = response.body()?.result ?: it.likeCnt)
                        val updatedCategoryFeeds = _categoryFeeds.value?.map { if (it.shortsId == categoryFeeds.shortsId) newFeed else it } ?: emptyList()
                        _categoryFeeds.value = updatedCategoryFeeds
                        Log.d("FeedViewModel", "Like updated for feed2: ${categoryFeeds.shortsId}")
                    }
                } else {
                    Log.d("FeedViewModel", "Like update failed: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.d("FeedViewModel", "Failed to update like status: ${e.message}")
            }
        }
    }
}
