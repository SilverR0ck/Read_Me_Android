package com.example.readme.ui.home.Feed

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.readme.ui.data.entities.like.LikeResponse
import com.example.readme.data.entities.category.CategoryFeedResponse
import com.example.readme.data.entities.inithome.FeedInfo
import com.example.readme.data.entities.inithome.MainInfoResponse
import com.example.readme.data.entities.inithome.ShortsInfo
import com.example.readme.utils.RetrofitClient
import retrofit2.Response
import retrofit2.Call
import retrofit2.Callback

// ViewModel에서 데이터 요청
class FeedViewModel : ViewModel() {
    private val _feeds = MutableLiveData<List<FeedInfo>>()
    val feeds: LiveData<List<FeedInfo>> get() = _feeds

    private val _shorts = MutableLiveData<List<ShortsInfo>>()
    val shorts: LiveData<List<ShortsInfo>> get() = _shorts

    private val _categoryFeeds = MutableLiveData<List<com.example.readme.ui.data.entities.category.FeedInfo>>()
    val categoryFeeds: LiveData<List<com.example.readme.ui.data.entities.category.FeedInfo>> get() = _categoryFeeds


    private val _categories = MutableLiveData<List<String>>()
    val categories: LiveData<List<String>> get() = _categories

    private val _combinedData = MediatorLiveData<Pair<List<FeedInfo>, List<ShortsInfo>>>()
    val combinedData: LiveData<Pair<List<FeedInfo>, List<ShortsInfo>>> get() = _combinedData

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
        RetrofitClient.getMainInfoService().getMainInfo().enqueue(object : Callback<MainInfoResponse> {
            override fun onResponse(call: Call<MainInfoResponse>, response: Response<MainInfoResponse>) {
                if (response.body()?.isSuccess == true) {
                    val result = response.body()?.result
                    // 로그 추가
                    Log.d("FeedViewModel", "Fetched feeds: ${result?.feeds}")

                    // 서버에서 받아온 카테고리 정보를 LiveData에 저장
                    val categories = result?.categories ?: emptyList()
                    _categories.postValue(categories)

                    // 필터링 없이 전체 feeds 리스트를 사용
                    _feeds.postValue(result?.feeds ?: emptyList())
                    Log.d("FeedViewModel", "Feeds posted to LiveData: ${_feeds.value}")

                    _shorts.postValue(result?.shorts ?: emptyList())
                    Log.d("FeedViewModel", "shorts posted to LiveData: ${_shorts.value}")

                } else {
                    // 오류 처리
                    Log.d("FeedViewModel", "Response not successful")
                }
            }

            override fun onFailure(call: Call<MainInfoResponse>, t: Throwable) {
                // 오류 처리
                Log.d("FeedViewModel", "Failed to fetch data: ${t.message}")
            }
        })
    }

    fun fetchCategoryFeeds(category: String) {
        RetrofitClient.getMainInfoService().getCategoryFeeds(1, 20, category).enqueue(object : Callback<CategoryFeedResponse> {
            override fun onResponse(call: Call<CategoryFeedResponse>, response: Response<CategoryFeedResponse>) {
                Log.d("anothor", "fetchCatrgory")
                if (response.body()?.isSuccess == true) {
                    val feedList: List<com.example.readme.ui.data.entities.category.FeedInfo> = response.body()?.result ?: emptyList()
                    _categoryFeeds.postValue(feedList)
                    Log.d("anothor", "${feedList}")
                } else {
                    Log.d("FeedViewModel", "Response not successful")
                }
            }

            override fun onFailure(call: Call<CategoryFeedResponse>, t: Throwable) {
                // 오류 처리
                Log.d("FeedApi", "통신 실패: ${t.message}")
            }
        })
    }

    fun likeShorts(feed: FeedInfo) {
        RetrofitClient.getMainInfoService().likeShorts(feed.shorts_id).enqueue(object : Callback<LikeResponse> {
            override fun onResponse(call: Call<LikeResponse>, response: Response<LikeResponse>) {
                if (response.body()?.isSuccess == true) {
                    val currentFeeds = _feeds.value ?: emptyList()

                    // updatedFeeds 리스트 생성
                    val updatedFeeds = currentFeeds.map {
                        if (it.shorts_id == feed.shorts_id) {
                            it.copy(isLike = !it.isLike, likeCnt = response.body()?.result ?: it.likeCnt)
                        } else {
                            it
                        }
                    }

                    // updatedFeeds를 LiveData에 반영
                    _feeds.postValue(updatedFeeds)
                    Log.d("FeedViewModel", "Like updated for feed: ${feed.shorts_id}")
                } else {
                    // 오류 처리
                    Log.d("FeedViewModel", "Like update failed: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<LikeResponse>, t: Throwable) {
                // 오류 처리
                Log.d("FeedViewModel", "Failed to update like status: ${t.message}")
            }
        })
    }

    fun likeShorts2(categoryFeeds: com.example.readme.ui.data.entities.category.FeedInfo) {
        RetrofitClient.getMainInfoService().likeShorts(categoryFeeds.shortsId).enqueue(object : Callback<LikeResponse> {
            override fun onResponse(call: Call<LikeResponse>, response: Response<LikeResponse>) {
                if (response.body()?.isSuccess == true) {
                    val currentFeeds = _categoryFeeds.value ?: emptyList()

                    // updatedFeeds 리스트 생성
                    val updatedFeeds = currentFeeds.map {
                        if (it.shortsId == categoryFeeds.shortsId) {
                            it.copy(isLike = !it.isLike, likeCnt = response.body()?.result ?: it.likeCnt)
                        } else {
                            it
                        }
                    }

                    // updatedFeeds를 LiveData에 반영
                    _categoryFeeds.postValue(updatedFeeds)
                    Log.d("FeedViewModel", "Like updated for feed2: ${categoryFeeds.shortsId} ${response.body()}")
                } else {
                    // 오류 처리
                    Log.d("FeedViewModel", "Like update failed: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<LikeResponse>, t: Throwable) {
                // 오류 처리
                Log.d("FeedViewModel", "Failed to update like status: ${t.message}")
            }
        })
    }
}

