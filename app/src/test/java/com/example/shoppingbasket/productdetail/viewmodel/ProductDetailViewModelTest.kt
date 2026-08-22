package com.example.shoppingbasket.productdetail.viewmodel

import com.example.shoppingbasket.basket.model.CartRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class ProductDetailViewModelTest {

    // 테스트마다 새 저장소를 만들어 쓰므로 앞선 테스트의 장바구니가 남지 않는다
    private val cartRepository = CartRepository()

    @Test
    @DisplayName("존재하는 상품 key로 생성하면 해당 상품을 상태로 반영한다")
    fun loadsExistingProduct() {
        val viewModel = ProductDetailViewModel("p1", cartRepository)

        assertThat(viewModel.uiState.value.product?.key()).isEqualTo("p1")
    }

    @Test
    @DisplayName("존재하지 않는 상품 key면 상태의 상품이 없다")
    fun loadsMissingProduct() {
        val viewModel = ProductDetailViewModel("없는id", cartRepository)

        assertThat(viewModel.uiState.value.product).isNull()
    }

    @Test
    @DisplayName("장바구니 담기를 호출하면 저장소에 상품이 추가된다")
    fun addToCart() {
        val viewModel = ProductDetailViewModel("p1", cartRepository)

        viewModel.addToCart()

        assertThat(cartRepository.findAll().items()).hasSize(1)
    }
}
