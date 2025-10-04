package com.scherzolambda.horarios.data_transformation.api.models


import com.google.gson.annotations.SerializedName

data class GitHubRelease(
    @SerializedName("id") val id: Long,
    @SerializedName("tag_name") val tagName: String,
    @SerializedName("name") val name: String?,
//    @SerializedName("body") val body: String?,
    @SerializedName("html_url") val htmlUrl: String?,
    @SerializedName("assets") val assets: List<Asset>?,
//    @SerializedName("tarball_url") val tarballUrl: String?,
//    @SerializedName("zipball_url") val zipballUrl: String?
)

data class Asset(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
//    @SerializedName("content_type") val contentType: String?,
//    @SerializedName("size") val size: Long?,
    @SerializedName("browser_download_url") val downloadUrl: String
)


