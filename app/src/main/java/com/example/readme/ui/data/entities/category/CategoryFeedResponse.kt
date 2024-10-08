package com.example.readme.ui.data.entities.category

import com.google.gson.annotations.SerializedName

data class CategoryFeedResponse(
    @SerializedName("isSuccess") val isSuccess: Boolean,
    @SerializedName("code") val code: String,
    @SerializedName("message") val message: String,
    @SerializedName("pageInfo") val pageInfo: PageInfo,
    @SerializedName("result") val result: List<FeedInfo> // List<FeedInfo>로 수정
)

// 페이지 정보 데이터 클래스
data class PageInfo(
    @SerializedName("page") val page: Int,
    @SerializedName("size") val size: Int,
    @SerializedName("hasNext") val hasNext: Boolean
)

// Feed 정보 데이터 클래스 (리스트로 변경)
data class FeedInfo(
    @SerializedName("userId") val userId: Int,
    @SerializedName("profileImg") val profileImg: String,
    @SerializedName("nickname") val nickname: String,
    @SerializedName("shortsId") val shortsId: Int,
    @SerializedName("bookId") val bookId: Int,
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