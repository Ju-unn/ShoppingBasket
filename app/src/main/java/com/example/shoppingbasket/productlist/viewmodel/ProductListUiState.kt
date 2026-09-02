package com.example.shoppingbasket.productlist.viewmodel

import com.example.shoppingbasket.productlist.model.Product

data class ProductListUiState(
    val products: List<ProductListItem> = emptyList(),
    // 더보기 버튼을 보여줄지 여부. 마지막 페이지까지 읽었으면 false
    val canLoadMore: Boolean = false,
    // 상단바 장바구니 배지에 표시할 총 수량. 담긴 게 없으면 "0"
    val cartBadgeText: String = "0",
    // 최근 본 상품 섹션. 비어 있으면 화면에서 섹션 자체를 그리지 않는다
    val recentlyViewed: List<Product> = emptyList(),
    // 서버에서 상품 목록을 받아오는 중인지 여부. true 면 화면에는 로딩 인디케이터만 보여준다
    val isLoading: Boolean = false
)
