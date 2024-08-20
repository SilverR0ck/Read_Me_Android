package com.example.readme.ui.search.shorts

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.readme.R
import com.example.readme.data.repository.SearchRepository
import com.example.readme.databinding.FragmentSearchShortsBinding
import com.example.readme.ui.base.BaseFragment
import com.example.readme.ui.home.shortsdetail.ShortsDetailFragment
import com.example.readme.ui.search.SearchFragment

class SearchShortsFragment : BaseFragment<FragmentSearchShortsBinding>(R.layout.fragment_search_shorts) {
    private val viewModel: SearchShortsViewModel by viewModels {
        SearchShortsViewModelFactory(SearchRepository)
    }

    override fun initDataBinding() {
        super.initDataBinding()
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        // RecyclerView에 LinearLayoutManager 설정
        binding.searchShortsRecyclerView.layoutManager = LinearLayoutManager(context)
    }

    override fun initAfterBinding() {
        super.initAfterBinding()

        // RecyclerView에 어댑터 설정
        val adapter = SearchShortsAdaptor(
            viewModel,
            onShortsClick = { shortsId ->
                // 검색 결과 화면으로 전환
                val parentFragment = parentFragment
                if (parentFragment is SearchFragment) {
                    val shortsDetailFragment = ShortsDetailFragment()
                    val bundle = Bundle().apply {
                        putInt("shortsId", shortsId)
                    }
                    shortsDetailFragment.arguments = bundle
                    parentFragment.setFragment(shortsDetailFragment)
                }
            },)
        binding.searchShortsRecyclerView.adapter = adapter

        // Bundle로 전달된 검색어를 가져와서 사용
        val keyword = arguments?.getString("keyword") ?: ""
        if (keyword.isNotEmpty()) {
            viewModel.searchShorts(keyword) // 초기 검색 수행

            viewModel.searchShortsItems.observe(viewLifecycleOwner) {
                adapter.submitList(it)
            }
        }

        // RecyclerView의 스크롤 상태를 감지하여 페이지네이션 적용
        binding.searchShortsRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                // 3/4 시점에 도달하면 다음 페이지를 로드
                val threshold = (totalItemCount * 3) / 4
                if ((visibleItemCount + firstVisibleItemPosition) >= threshold &&
                    firstVisibleItemPosition >= 0
                ) {
                    viewModel.loadNextPage()
                }
            }
        })
    }
}
