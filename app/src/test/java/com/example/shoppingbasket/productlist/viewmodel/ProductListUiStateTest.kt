package com.example.shoppingbasket.productlist.viewmodel

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class ProductListUiStateTest {

    @Test
    @DisplayName("기본 상태는 빈 상품 목록이다")
    fun defaultState() {
        assertThat(ProductListUiState().products).isEmpty()
    }
}
