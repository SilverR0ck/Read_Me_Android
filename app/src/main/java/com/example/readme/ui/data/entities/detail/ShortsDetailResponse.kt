package com.example.readme.ui.data.entities.detail

import com.google.gson.annotations.SerializedName

data class ShortsDetailResponse(
    @SerializedName("isSuccess") val isSuccess: Boolean,
    @SerializedName("code") val code: String,
    @SerializedName("message") val message: String,
    @SerializedName("pageInfo") val pageInfo: PageInfo,
    @SerializedName("result") val result: List<ShortsDetailInfo>
)

data class ShortsDetailInfo(
    @SerializedName("userId") val userId: Int,
    @SerializedName("userAccount") val userAccount: String,
    @SerializedName("profileImg") val profileImg: String,
    @SerializedName("isFollow") val isFollow: Boolean,
    @SerializedName("shortsId") val shortsId: Int,
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
    @SerializedName("bookId") val bookId: Int
)

data class PageInfo(
    @SerializedName("page") val page: Int,
    @SerializedName("size") val size: Int,
    @SerializedName("hasNext") val hasNext: Boolean
)
