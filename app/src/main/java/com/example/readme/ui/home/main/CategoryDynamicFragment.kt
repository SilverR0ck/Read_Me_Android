package com.example.readme.ui.home.main

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.readme.R
import com.example.readme.databinding.FragmentDynamicBinding
import com.example.readme.ui.MainActivity
import com.example.readme.ui.data.entities.inithome.FeedInfo
import com.example.readme.ui.data.entities.inithome.ShortsInfo
import com.example.readme.ui.home.Feed.Feed2Adapter
import com.example.readme.ui.home.Feed.FeedAdapter
import com.example.readme.ui.home.Feed.FeedViewModel
import com.example.readme.ui.home.Feed.ShortsAdapter
import com.example.readme.ui.home.shortsdetail.ShortsDetailFragment
import com.example.whashow.base.BaseFragment

class CategoryDynamicFragment : BaseFragment<FragmentDynamicBinding>(R.layout.fragment_dynamic) {

    private lateinit var feedAdapter: FeedAdapter
    private lateinit var feedListManager: LinearLayoutManager
    private lateinit var feed2Adapter: Feed2Adapter
    private lateinit var feed2ListManager: LinearLayoutManager
    private lateinit var shortsAdapter: ShortsAdapter
    private lateinit var shortsListManager: LinearLayoutManager

    // ViewModel 초기화
    private val feedViewModel: FeedViewModel by viewModels()
    private var category: String? = null

    override fun initStartView() {
        super.initStartView()
        (activity as MainActivity).ShowHome()
    }

    override fun initDataBinding() {
        super.initDataBinding()
        (activity as MainActivity).binding.bottomNavigationView.visibility = View.VISIBLE

        category = arguments?.getString(ARG_CATEGORY)
        Log.d("CategoryDynamicFragment", "Category: $category")

        if (category == "추천") {
            feedViewModel.fetchFeeds()
        } else {
            feedViewModel.fetchCategoryFeeds(category!!)
        }


        feedViewModel.combinedData.observe(viewLifecycleOwner) { (feeds, shorts) ->
            Log.d("FeedViewModel", "Combined Data - Feeds: $feeds")
            Log.d("FeedViewModel", "Combined Data - Shorts: $shorts")
            setupRecyclerView(feeds, shorts)
        }

        feedViewModel.categoryFeeds.observe(viewLifecycleOwner) { categoryFeeds ->
            setupCategoryRecyclerView(categoryFeeds)
        }
    }

    private fun setupRecyclerView(feeds: List<FeedInfo>, shorts: List<ShortsInfo>) {
        if (!::feedAdapter.isInitialized) {
            feedListManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            feedAdapter = FeedAdapter(ArrayList(feeds))
            binding.rvPost.apply {
                setHasFixedSize(true)
                layoutManager = feedListManager
                adapter = feedAdapter
                // feedAdapter 초기화 완료 후 클릭 리스너 설정
                feedAdapter.setMyItemClickListener(object : FeedAdapter.MyItemClickListener {
                    override fun onItemClick(feed: FeedInfo) {
                        // 아이템 전체 클릭 시의 동작 (기존 코드)
                    }

                    override fun onImageClick(feed: FeedInfo) {
                        // 이미지 클릭 시 ShortsDetailFragment로 이동
                        val fragment = ShortsDetailFragment().apply {
                            arguments = Bundle().apply {
                                putInt("shortsId", feed.shorts_id)
                                Log.d("shortId", feed.shorts_id.toString())
                                putString("start", "main")
                                // 필요한 경우 추가 데이터도 함께 전달
                            }
                        }
                        (context as? MainActivity)?.addFragment(fragment)
                    }

                    override fun onLikeClick(feed: FeedInfo, isLiked: Boolean) {
                        feedViewModel.likeShorts(feed)
                    }
                })
            }
        } else {
            feedAdapter.updateData(feeds)  // 데이터 갱신 메서드
        }

        if (!::shortsAdapter.isInitialized) {
            shortsListManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            shortsAdapter = ShortsAdapter(ArrayList(shorts))
            binding.rvExtra.apply {
                setHasFixedSize(true)
                layoutManager = shortsListManager
                adapter = shortsAdapter
            }
        } else {
            shortsAdapter.updateData(shorts)
        }

        // Show or hide rvExtra based on category
        binding.rvExtra.visibility = if (category == "추천") View.VISIBLE else View.GONE
    }

    private fun setupCategoryRecyclerView(categoryFeeds: List<com.example.readme.ui.data.entities.category.FeedInfo>) {
        if (!::feed2Adapter.isInitialized) {
            feed2ListManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
//            Log.d("categoryFeeds", "${categoryFeeds}")
            feed2Adapter = Feed2Adapter(ArrayList(categoryFeeds))
            binding.rvPost.apply {
                setHasFixedSize(true)
                layoutManager = feed2ListManager
                adapter = feed2Adapter

                feed2Adapter.setMyItemClickListener(object : Feed2Adapter.MyItemClickListener {
                    override fun onItemClick(categoryFeeds: com.example.readme.ui.data.entities.category.FeedInfo) {
                        // 아이템 전체 클릭 시의 동작 (기존 코드)
                    }

                    override fun onImageClick(categoryFeeds: com.example.readme.ui.data.entities.category.FeedInfo) {
                        val fragment = ShortsDetailFragment().apply {
                            arguments = Bundle().apply {
                                putInt("shortsId", categoryFeeds.shortsId)
                                Log.d("shortId", categoryFeeds.shortsId.toString())
                                putString("start", "main")
                            }
                        }
                        (context as? MainActivity)?.addFragment(fragment)
                    }

                    override fun onLikeClick(feed: com.example.readme.ui.data.entities.category.FeedInfo, isLiked: Boolean) {
                        feedViewModel.likeShorts2(feed)
                    }
                })
            }
        } else {
            feed2Adapter.updateData(categoryFeeds)

        }
    }


    override fun initAfterBinding() {
        super.initAfterBinding()
        (activity as MainActivity).binding.bottomNavigationView.visibility = View.VISIBLE
    }

    companion object {
        private const val ARG_CATEGORY = "category"

        fun newInstance(category: String) = CategoryDynamicFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_CATEGORY, category)
            }
        }
    }
}
