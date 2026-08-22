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

    @Test
    @DisplayName("상품이 5개 이하면 한 페이지에 다 담기고 앞뒤로 이동할 수 없다")
    fun singlePageWhenNotOverflowing() {
        addProducts(5)

        val viewModel = BasketViewModel()

        assertThat(viewModel.uiState.value.cartItems).hasSize(5)
        assertThat(viewModel.uiState.value.canGoPrevious).isFalse()
        assertThat(viewModel.uiState.value.canGoNext).isFalse()
    }

    @Test
    @DisplayName("상품이 5개를 넘으면 첫 페이지에 5개만 담기고 다음으로 갈 수 있다")
    fun firstPageWhenOverflowing() {
        addProducts(8)

        val viewModel = BasketViewModel()

        assertThat(viewModel.uiState.value.cartItems).hasSize(5)
        assertThat(viewModel.uiState.value.pageNumber).isEqualTo(1)
        assertThat(viewModel.uiState.value.canGoNext).isTrue()
    }

    @Test
    @DisplayName("다음 페이지로 가면 남은 상품만 보이고 이전으로 갈 수 있다")
    fun nextPage() {
        addProducts(8)
        val viewModel = BasketViewModel()

        viewModel.nextPage()

        assertThat(viewModel.uiState.value.cartItems).hasSize(3)
        assertThat(viewModel.uiState.value.pageNumber).isEqualTo(2)
        assertThat(viewModel.uiState.value.canGoPrevious).isTrue()
        assertThat(viewModel.uiState.value.canGoNext).isFalse()
    }

    @Test
    @DisplayName("마지막 페이지에서 다음으로 가려 해도 그대로 머문다")
    fun nextPageOnLastPageDoesNothing() {
        addProducts(8)
        val viewModel = BasketViewModel()

        viewModel.nextPage()
        viewModel.nextPage()

        assertThat(viewModel.uiState.value.pageNumber).isEqualTo(2)
    }

    @Test
    @DisplayName("첫 페이지에서 이전으로 가려 해도 그대로 머문다")
    fun previousPageOnFirstPageDoesNothing() {
        addProducts(8)
        val viewModel = BasketViewModel()

        viewModel.previousPage()

        assertThat(viewModel.uiState.value.pageNumber).isEqualTo(1)
    }

    @Test
    @DisplayName("이전 페이지로 돌아가면 첫 페이지 상품이 다시 보인다")
    fun previousPage() {
        addProducts(8)
        val viewModel = BasketViewModel()

        viewModel.nextPage()
        viewModel.previousPage()

        assertThat(viewModel.uiState.value.pageNumber).isEqualTo(1)
        assertThat(viewModel.uiState.value.cartItems).hasSize(5)
    }

    @Test
    @DisplayName("마지막 페이지의 상품을 모두 지우면 이전 페이지로 당겨진다")
    fun removeLastItemOnLastPageMovesToPreviousPage() {
        addProducts(6)
        val viewModel = BasketViewModel()
        viewModel.nextPage()

        viewModel.remove("p6")

        assertThat(viewModel.uiState.value.pageNumber).isEqualTo(1)
        assertThat(viewModel.uiState.value.cartItems).hasSize(5)
        assertThat(viewModel.uiState.value.canGoNext).isFalse()
    }

    @Test
    @DisplayName("현재 페이지에 상품이 남아 있으면 삭제해도 페이지를 유지한다")
    fun removeKeepsCurrentPageWhenItemsRemain() {
        addProducts(8)
        val viewModel = BasketViewModel()
        viewModel.nextPage()

        viewModel.remove("p6")

        assertThat(viewModel.uiState.value.pageNumber).isEqualTo(2)
        assertThat(viewModel.uiState.value.cartItems).hasSize(2)
    }

    private fun addProducts(count: Int) {
        (1..count).forEach { number -> CartRepository.add(testProduct("p$number")) }
    }
}
