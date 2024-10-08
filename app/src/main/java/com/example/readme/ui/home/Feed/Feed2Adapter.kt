package com.example.readme.ui.home.Feed

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.readme.databinding.FeedItemBinding
import com.example.readme.ui.data.entities.category.FeedInfo
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone


class Feed2Adapter(var list: ArrayList<FeedInfo>) : RecyclerView.Adapter<Feed2Adapter.Feed2Holder>() {

    // 인터페이스 정의 (아이템 클릭 리스너)
    interface MyItemClickListener {
        fun onItemClick(feed: FeedInfo)
        fun onImageClick(feed: FeedInfo)
        fun onLikeClick(feed: FeedInfo, isLiked: Boolean)
    }

    // 리스너를 설정하는 함수
    private lateinit var myItemClickListener: MyItemClickListener
    fun setMyItemClickListener(itemClickListener: MyItemClickListener) {
        myItemClickListener = itemClickListener
    }

    // ViewHolder 정의
    inner class Feed2Holder(val binding: FeedItemBinding) : RecyclerView.ViewHolder(binding.root) {

        private var isLiked = false

        init {
            binding.likeIcon.setOnClickListener {
                toggleLikeState()
            }

            binding.likefillIcon.setOnClickListener {
                toggleLikeState()
            }
        }

        // 좋아요 상태 토글
        private fun toggleLikeState() {
            isLiked = !isLiked
            updateLikeIcon()
            myItemClickListener.onLikeClick(list[adapterPosition], isLiked)
            Log.d("Feed2Adapter", "toggleLikeState: isLiked = $isLiked, likeCnt = ${list[adapterPosition].likeCnt}")
        }

        // 아이콘 업데이트
        private fun updateLikeIcon() {
            if (isLiked) {
                binding.likeIcon.visibility = View.GONE
                binding.likefillIcon.visibility = View.VISIBLE
            } else {
                binding.likeIcon.visibility = View.VISIBLE
                binding.likefillIcon.visibility = View.GONE
            }
        }
        fun bind(feed: FeedInfo) {

            isLiked = feed.isLike
            Log.d("feed.isLike", "${isLiked}")
            updateLikeIcon()

            Glide.with(binding.root.context)
                .load(feed.profileImg)
                .into(binding.feedProfile)

            binding.username.text = feed.nickname

            Glide.with(binding.root.context)
                .load(feed.shortsImg)
                .into(binding.shortsImage)

            binding.mainTitle.text = feed.title
            binding.content.text = feed.content
            binding.likeCount.text = feed.likeCnt.toString()
            binding.commentCount.text = feed.commentCnt.toString()
            binding.timestamp.text = calculateDaysAgo(feed.postingDate)


            adjustViewPosition(binding.feedSentence, feed.phraseX, feed.phraseY)

            binding.shortsImage.setOnClickListener {
                myItemClickListener.onImageClick(feed)  // 이미지 클릭 시 호출
            }
        }
    }

    // ViewHolder 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Feed2Holder {
        val binding = FeedItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return Feed2Holder(binding)
    }


    // ViewHolder에 데이터 바인딩
    override fun onBindViewHolder(holder: Feed2Holder, position: Int) {
        val feed = list[position]
        holder.bind(feed)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    // 데이터를 업데이트하는 함수
    fun updateData(newList: List<FeedInfo>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }

    private fun adjustViewPosition(view: View, x: Double, y: Double) {
        view.x = x.toFloat()
        view.y = y.toFloat()
    }

    private fun calculateDaysAgo(postingDate: String): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        formatter.timeZone = TimeZone.getTimeZone("UTC")

        val postDate: Date? = formatter.parse(postingDate)
        val currentDate = Date()

        postDate?.let {
            val diffInMillis = currentDate.time - postDate.time
            val daysBetween = (diffInMillis / (1000 * 60 * 60 * 24)).toInt()

            return if (daysBetween == 0) {
                "오늘"
            } else {
                "${daysBetween}일 전"
            }
        }

        return "날짜 불명"
    }

}



