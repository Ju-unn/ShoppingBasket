package com.example.shoppingbasket.basket.viewmodel

import com.example.shoppingbasket.basket.model.CartItem

data class BasketUiState(
    val cartItems: List<CartItem> = emptyList()
)
