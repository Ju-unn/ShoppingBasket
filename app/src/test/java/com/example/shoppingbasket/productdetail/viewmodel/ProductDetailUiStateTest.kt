package com.example.shoppingbasket.productdetail.viewmodel

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class ProductDetailUiStateTest {

    @Test
    @DisplayName("기본 상태는 상품이 없다")
    fun defaultState() {
        assertThat(ProductDetailUiState().product).isNull()
    }
}
