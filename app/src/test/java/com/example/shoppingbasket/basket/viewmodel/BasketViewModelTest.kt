package com.example.shoppingbasket.basket.viewmodel

import com.example.shoppingbasket.basket.model.CartRepository
import com.example.shoppingbasket.common.model.Money
import com.example.shoppingbasket.productlist.model.Product
import com.example.shoppingbasket.productlist.model.ProductId
import com.example.shoppingbasket.productlist.model.ProductImageUrl
import com.example.shoppingbasket.productlist.model.ProductName
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class BasketViewModelTest {

    @BeforeEach
    fun clearCart() {
        CartRepository.findAll().items().forEach { item -> CartRepository.remove(item.key()) }
    }

    @Test
    @DisplayName("생성 시 CartRepository의 담긴 상품을 상태로 반영한다")
    fun loadsCartItemsOnCreate() {
        CartRepository.add(product("p1"))

        val viewModel = BasketViewModel()

        assertThat(viewModel.uiState.value.cartItems).hasSize(1)
    }

    @Test
    @DisplayName("삭제하면 상태와 CartRepository에서 모두 제거된다")
    fun remove() {
        CartRepository.add(product("p1"))
        val viewModel = BasketViewModel()

        viewModel.remove("p1")

        assertThat(viewModel.uiState.value.cartItems).isEmpty()
        assertThat(CartRepository.findAll().isEmpty()).isTrue()
    }

    private fun product(id: String): Product {
        return Product(
            ProductId(id),
            ProductName("상품-$id"),
            Money(10_000),
            ProductImageUrl("https://picsum.photos/seed/$id/600")
        )
    }
}
