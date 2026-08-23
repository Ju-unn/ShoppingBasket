package com.example.shoppingbasket.productlist.viewmodel

data class ProductListUiState(
    val products: List<ProductListItem> = emptyList(),
    // 더보기 버튼을 보여줄지 여부. 마지막 페이지까지 읽었으면 false
    val canLoadMore: Boolean = false,
    // 상단바 장바구니 배지에 표시할 총 수량. 담긴 게 없으면 "0"
    val cartBadgeText: String = "0"
)
