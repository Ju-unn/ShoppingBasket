package com.example.shoppingbasket.recentlyviewed.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

// Room 저장 전용 DTO. sortOrder 로 RecentlyViewedProducts 의 최신순을 복원한다 (4단계 결정 10번)
@Entity(tableName = "recently_viewed_products")
data class RecentlyViewedProductEntity(
    @PrimaryKey val productId: String,
    val productName: String,
    val productPrice: Int,
    val productImageUrl: String,
    val sortOrder: Int
)
