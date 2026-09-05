package com.example.shoppingbasket.basket.viewmodel

import com.example.shoppingbasket.basket.model.CartRepository
import com.example.shoppingbasket.common.fixture.clearViewModel
import com.example.shoppingbasket.common.fixture.testProduct
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class BasketViewModelTest {

    // 테스트마다 새 저장소를 만들어 쓰므로 앞선 테스트의 장바구니가 남지 않는다
    private val cartRepository = CartRepository()

    @Test
    @DisplayName("생성 시 저장소의 담긴 상품을 상태로 반영한다")
    fun loadsCartItemsOnCreate() {
        cartRepository.add(testProduct("p1"))

        val viewModel = BasketViewModel(cartRepository)

        assertThat(viewModel.uiState.value.cartItems).hasSize(1)
    }

    @Test
    @DisplayName("삭제하면 상태와 저장소에서 모두 제거된다")
    fun remove() {
        cartRepository.add(testProduct("p1"))
        val viewModel = BasketViewModel(cartRepository)

        viewModel.remove("p1")

        assertThat(viewModel.uiState.value.cartItems).isEmpty()
        assertThat(cartRepository.findAll().isEmpty()).isTrue()
    }

    @Test
    @DisplayName("상품이 5개 이하면 한 페이지에 다 담기고 앞뒤로 이동할 수 없다")
    fun singlePageWhenNotOverflowing() {
        addProducts(5)

        val viewModel = BasketViewModel(cartRepository)

        assertThat(viewModel.uiState.value.cartItems).hasSize(5)
        assertThat(viewModel.uiState.value.canGoPrevious).isFalse()
        assertThat(viewModel.uiState.value.canGoNext).isFalse()
    }

    @Test
    @DisplayName("상품이 5개를 넘으면 첫 페이지에 5개만 담기고 다음으로 갈 수 있다")
    fun firstPageWhenOverflowing() {
        addProducts(8)

        val viewModel = BasketViewModel(cartRepository)

        assertThat(viewModel.uiState.value.cartItems).hasSize(5)
        assertThat(viewModel.uiState.value.pageNumber).isEqualTo(1)
        assertThat(viewModel.uiState.value.canGoNext).isTrue()
    }

    @Test
    @DisplayName("다음 페이지로 가면 남은 상품만 보이고 이전으로 갈 수 있다")
    fun nextPage() {
        addProducts(8)
        val viewModel = BasketViewModel(cartRepository)

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
        val viewModel = BasketViewModel(cartRepository)

        viewModel.nextPage()
        viewModel.nextPage()

        assertThat(viewModel.uiState.value.pageNumber).isEqualTo(2)
    }

    @Test
    @DisplayName("첫 페이지에서 이전으로 가려 해도 그대로 머문다")
    fun previousPageOnFirstPageDoesNothing() {
        addProducts(8)
        val viewModel = BasketViewModel(cartRepository)

        viewModel.previousPage()

        assertThat(viewModel.uiState.value.pageNumber).isEqualTo(1)
    }

    @Test
    @DisplayName("이전 페이지로 돌아가면 첫 페이지 상품이 다시 보인다")
    fun previousPage() {
        addProducts(8)
        val viewModel = BasketViewModel(cartRepository)

        viewModel.nextPage()
        viewModel.previousPage()

        assertThat(viewModel.uiState.value.pageNumber).isEqualTo(1)
        assertThat(viewModel.uiState.value.cartItems).hasSize(5)
    }

    @Test
    @DisplayName("마지막 페이지의 상품을 모두 지우면 이전 페이지로 당겨진다")
    fun removeLastItemOnLastPageMovesToPreviousPage() {
        addProducts(6)
        val viewModel = BasketViewModel(cartRepository)
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
        val viewModel = BasketViewModel(cartRepository)
        viewModel.nextPage()

        viewModel.remove("p6")

        assertThat(viewModel.uiState.value.pageNumber).isEqualTo(2)
        assertThat(viewModel.uiState.value.cartItems).hasSize(2)
    }

    @Test
    @DisplayName("수량을 늘리면 상태와 저장소에 반영된다")
    fun increase() {
        cartRepository.add(testProduct("p1"))
        val viewModel = BasketViewModel(cartRepository)

        viewModel.increase("p1")

        assertThat(viewModel.uiState.value.cartItems.first().displayQuantity()).isEqualTo("2")
    }

    @Test
    @DisplayName("수량이 2 이상일 때 줄이면 상품은 그대로 남는다")
    fun decrease() {
        cartRepository.add(testProduct("p1"))
        cartRepository.increase("p1")
        val viewModel = BasketViewModel(cartRepository)

        viewModel.decrease("p1")

        assertThat(viewModel.uiState.value.cartItems.first().displayQuantity()).isEqualTo("1")
    }

    @Test
    @DisplayName("수량 1에서 줄이면 항목이 제거되고 마지막 페이지로 당겨진다")
    fun decreaseAtMinimumRemovesItemAndCoercesPage() {
        addProducts(6)
        val viewModel = BasketViewModel(cartRepository)
        viewModel.nextPage()

        viewModel.decrease("p6")

        assertThat(viewModel.uiState.value.pageNumber).isEqualTo(1)
        assertThat(viewModel.uiState.value.cartItems).hasSize(5)
    }

    private fun addProducts(count: Int) {
        (1..count).forEach { number -> cartRepository.add(testProduct("p$number")) }
    }

    // I9: onCleared() 가 하는 일(구독 해제)에 그동안 테스트가 없었다
    @Test
    @DisplayName("정리되면 장바구니 변경 구독을 해제해 더는 상태를 갱신하지 않는다")
    fun clearedViewModelStopsListeningToCart() {
        val viewModel = BasketViewModel(cartRepository)
        clearViewModel(viewModel)
        val stateBefore = viewModel.uiState.value

        cartRepository.add(testProduct("p1"))

        assertThat(viewModel.uiState.value).isSameAs(stateBefore)
    }
}
