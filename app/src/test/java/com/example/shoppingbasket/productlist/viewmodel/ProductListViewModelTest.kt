package com.example.shoppingbasket.productlist.viewmodel

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class ProductListViewModelTest {

    @Test
    @DisplayName("생성 시 저장소의 상품 목록을 상태로 반영한다")
    fun loadsProductsOnCreate() {
        val viewModel = ProductListViewModel()

        assertThat(viewModel.uiState.value.products).hasSize(6)
    }
}
