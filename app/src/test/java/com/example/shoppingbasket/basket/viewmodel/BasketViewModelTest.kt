package com.example.shoppingbasket.basket.viewmodel

import com.example.shoppingbasket.basket.model.CartRepository
import com.example.shoppingbasket.common.fixture.clearCartRepository
import com.example.shoppingbasket.common.fixture.testProduct
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class BasketViewModelTest {

    @BeforeEach
    fun clearCart() {
        clearCartRepository()
    }

    @Test
    @DisplayName("생성 시 CartRepository의 담긴 상품을 상태로 반영한다")
    fun loadsCartItemsOnCreate() {
        CartRepository.add(testProduct("p1"))

        val viewModel = BasketViewModel()

        assertThat(viewModel.uiState.value.cartItems).hasSize(1)
    }

    @Test
    @DisplayName("삭제하면 상태와 CartRepository에서 모두 제거된다")
    fun remove() {
        CartRepository.add(testProduct("p1"))
        val viewModel = BasketViewModel()

        viewModel.remove("p1")

        assertThat(viewModel.uiState.value.cartItems).isEmpty()
        assertThat(CartRepository.findAll().isEmpty()).isTrue()
    }
}
