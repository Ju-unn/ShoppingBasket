package com.example.shoppingbasket.productdetail.viewmodel

import com.example.shoppingbasket.basket.model.CartRepository
import com.example.shoppingbasket.common.fixture.clearCartRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class ProductDetailViewModelTest {

    @BeforeEach
    fun clearCart() {
        clearCartRepository()
    }

    @Test
    @DisplayName("존재하는 상품 key로 생성하면 해당 상품을 상태로 반영한다")
    fun loadsExistingProduct() {
        val viewModel = ProductDetailViewModel("p1")

        assertThat(viewModel.uiState.value.product?.key()).isEqualTo("p1")
    }

    @Test
    @DisplayName("존재하지 않는 상품 key면 상태의 상품이 없다")
    fun loadsMissingProduct() {
        val viewModel = ProductDetailViewModel("없는id")

        assertThat(viewModel.uiState.value.product).isNull()
    }

    @Test
    @DisplayName("장바구니 담기를 호출하면 CartRepository에 상품이 추가된다")
    fun addToCart() {
        val viewModel = ProductDetailViewModel("p1")

        viewModel.addToCart()

        assertThat(CartRepository.findAll().items()).hasSize(1)
    }
}
