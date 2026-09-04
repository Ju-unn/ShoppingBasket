package com.example.shoppingbasket.basket.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

// Room 저장 전용 DTO. sortOrder 로 CartItems 의 담긴 순서를 복원한다 (4단계 결정 10번)
@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey val productId: String,
    val productName: String,
    val productPrice: Int,
    val productImageUrl: String,
    val quantity: Int,
    val sortOrder: Int
)
