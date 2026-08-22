package com.example.shoppingbasket.productlist.viewmodel

import com.example.shoppingbasket.productlist.model.Product

data class ProductListUiState(
    val products: List<Product> = emptyList(),
    // 더보기 버튼을 보여줄지 여부. 마지막 페이지까지 읽었으면 false
    val canLoadMore: Boolean = false
)
