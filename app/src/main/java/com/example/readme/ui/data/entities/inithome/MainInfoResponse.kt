package com.example.readme.ui.data.entities.inithome
import com.google.gson.annotations.SerializedName

data class MainInfoResponse(
    @SerializedName("isSuccess") val isSuccess: Boolean,
    @SerializedName("code") val code: String,
    @SerializedName("message") val message: String,
    @SerializedName("pageInfo") val pageInfo: PageInfo,
    @SerializedName("result") val result: ResultData // Result 객체 추가
)

// 페이지 정보 데이터 클래스
data class PageInfo(
    @SerializedName("page") val page: Int,
    @SerializedName("size") val size: Int,
    @SerializedName("hasNext") val hasNext: Boolean
)

// 결과 데이터 클래스 (result 안에 있는 데이터를 포함)
data class ResultData(
    @SerializedName("categories") val categories: List<String>,
    @SerializedName("shorts") val shorts: List<ShortsInfo>,
    @SerializedName("feeds") val feeds: List<FeedInfo>
)

// Shorts 정보 데이터 클래스
data class ShortsInfo(
    @SerializedName("shorts_id") val shorts_id: Int,
    @SerializedName("shortsImg") val shortsImg: String,
    @SerializedName("phrase") val phrase: String,
    @SerializedName("bookTitle") val bookTitle: String,
    @SerializedName("author") val author: String,
    @SerializedName("translator") val translator: String?,
    @SerializedName("category") val category: String
)

// Feeds 정보 데이터 클래스
data class FeedInfo(
    @SerializedName("userId") val userId: Int,
    @SerializedName("profileImg") val profileImg: String,
    @SerializedName("nickname") val nickname: String,
    @SerializedName("shorts_id") val shorts_id: Int,
    @SerializedName("shortsImg") val shortsImg: String,
    @SerializedName("phrase") val phrase: String,
    @SerializedName("phraseX") val phraseX: Double,
    @SerializedName("phraseY") val phraseY: Double,
    @SerializedName("title") val title: String,
    @SerializedName("content") val content: String,
    @SerializedName("tags") val tags: List<String>,
    @SerializedName("isLike") val isLike: Boolean,
    @SerializedName("likeCnt") val likeCnt: Int,
    @SerializedName("commentCnt") val commentCnt: Int,
    @SerializedName("postingDate") val postingDate: String
)