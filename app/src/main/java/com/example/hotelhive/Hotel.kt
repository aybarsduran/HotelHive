package com.example.hotelhive



data class Hotel(
    val id: String = "",
    val name: String = "",
    val region: String = "",
    val imageUrl: String = "",
    val priceInfo: String = "",
    var isFavorite: Boolean = false,
    var isReserved: Boolean = false,
    val reviewScore: String = "",
    val totalReviewCount: String = "",
    val numberOfDays: Int = 0
)

data class ImageHotel(
    val imageUrlEach: String,
)