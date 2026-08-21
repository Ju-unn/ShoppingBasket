package com.example.shoppingbasket.productlist.viewmodel

import com.example.shoppingbasket.productlist.model.Product

data class ProductListUiState(
    val products: List<Product> = emptyList()
)
