package com.example.shoppingbasket.productlist.viewmodel

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class ProductListViewModelTest {

    @Test
    @DisplayName("생성 시 저장소의 상품 목록을 상태로 반영한다")
    fun loadsProductsOnCreate() {
        val viewModel = ProductListViewModel()

        // 아직 페이지 단위 로딩 전이라 저장소의 상품 전체가 그대로 올라온다.
        // 다음 작업(ProductListViewModel.loadMore)에서 첫 페이지 20개로 바뀐다.
        assertThat(viewModel.uiState.value.products).hasSize(50)
    }
}
