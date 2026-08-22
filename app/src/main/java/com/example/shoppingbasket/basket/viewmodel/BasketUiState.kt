package com.example.shoppingbasket.basket.viewmodel

import com.example.shoppingbasket.basket.model.CartItem

data class BasketUiState(
    val cartItems: List<CartItem> = emptyList(),
    // 화면에 표시할 1부터 시작하는 페이지 번호
    val pageNumber: Int = 1,
    val canGoPrevious: Boolean = false,
    val canGoNext: Boolean = false
)
