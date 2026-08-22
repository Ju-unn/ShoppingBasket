package com.example.shoppingbasket.productlist.viewmodel

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class ProductListViewModelTest {

    @Test
    @DisplayName("생성 시 첫 페이지 20개만 상태로 반영한다")
    fun loadsFirstPageOnCreate() {
        val viewModel = ProductListViewModel()

        assertThat(viewModel.uiState.value.products).hasSize(20)
    }

    @Test
    @DisplayName("뒤에 상품이 남아 있으면 더보기가 가능하다")
    fun canLoadMoreOnFirstPage() {
        val viewModel = ProductListViewModel()

        assertThat(viewModel.uiState.value.canLoadMore).isTrue()
    }

    @Test
    @DisplayName("더보기를 누르면 다음 페이지가 기존 목록 뒤에 이어 붙는다")
    fun loadMoreAppendsNextPage() {
        val viewModel = ProductListViewModel()

        viewModel.loadMore()

        assertThat(viewModel.uiState.value.products).hasSize(40)
    }

    @Test
    @DisplayName("더보기로 쌓인 목록의 앞부분은 첫 페이지 그대로 유지된다")
    fun loadMoreKeepsAlreadyLoadedProducts() {
        val viewModel = ProductListViewModel()
        val firstPage = viewModel.uiState.value.products

        viewModel.loadMore()

        assertThat(viewModel.uiState.value.products).startsWith(*firstPage.toTypedArray())
    }

    @Test
    @DisplayName("마지막 페이지까지 읽으면 더보기가 불가능해진다")
    fun cannotLoadMoreOnLastPage() {
        val viewModel = ProductListViewModel()

        viewModel.loadMore()
        viewModel.loadMore()

        assertThat(viewModel.uiState.value.products).hasSize(50)
        assertThat(viewModel.uiState.value.canLoadMore).isFalse()
    }

    @Test
    @DisplayName("마지막 페이지에서 더보기를 더 눌러도 목록이 늘어나지 않는다")
    fun loadMoreOnLastPageDoesNothing() {
        val viewModel = ProductListViewModel()

        repeat(5) { viewModel.loadMore() }

        assertThat(viewModel.uiState.value.products).hasSize(50)
    }

    @Test
    @DisplayName("더보기로 쌓인 목록에 같은 상품이 중복되지 않는다")
    fun loadMoreDoesNotDuplicateProducts() {
        val viewModel = ProductListViewModel()

        viewModel.loadMore()
        viewModel.loadMore()

        val keys = viewModel.uiState.value.products.map { product -> product.key() }
        assertThat(keys).doesNotHaveDuplicates()
    }
}
