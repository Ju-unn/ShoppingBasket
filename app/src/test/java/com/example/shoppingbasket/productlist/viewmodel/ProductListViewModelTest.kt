package com.example.shoppingbasket.productlist.viewmodel

import com.example.shoppingbasket.basket.model.CartRepository
import com.example.shoppingbasket.common.fixture.ManualProductApi
import com.example.shoppingbasket.common.fixture.fakeProductRepository
import com.example.shoppingbasket.common.fixture.fakeProductResponses
import com.example.shoppingbasket.common.fixture.testProduct
import com.example.shoppingbasket.productdetail.viewmodel.ProductDetailViewModel
import com.example.shoppingbasket.productlist.data.repository.ProductRepositoryImpl
import com.example.shoppingbasket.recentlyviewed.model.RecentlyViewedRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class ProductListViewModelTest {

    private val cartRepository = CartRepository()
    private val recentlyViewedRepository = RecentlyViewedRepository()
    private val productRepository = fakeProductRepository()

    @Test
    @DisplayName("생성 시 첫 페이지 20개만 상태로 반영한다")
    fun loadsFirstPageOnCreate() {
        val viewModel = ProductListViewModel(cartRepository, repository = productRepository)

        assertThat(viewModel.uiState.value.products).hasSize(20)
    }

    @Test
    @DisplayName("뒤에 상품이 남아 있으면 더보기가 가능하다")
    fun canLoadMoreOnFirstPage() {
        val viewModel = ProductListViewModel(cartRepository, repository = productRepository)

        assertThat(viewModel.uiState.value.canLoadMore).isTrue()
    }

    @Test
    @DisplayName("더보기를 누르면 다음 페이지가 기존 목록 뒤에 이어 붙는다")
    fun loadMoreAppendsNextPage() {
        val viewModel = ProductListViewModel(cartRepository, repository = productRepository)

        viewModel.loadMore()

        assertThat(viewModel.uiState.value.products).hasSize(40)
    }

    @Test
    @DisplayName("더보기로 쌓인 목록의 앞부분은 첫 페이지 그대로 유지된다")
    fun loadMoreKeepsAlreadyLoadedProducts() {
        val viewModel = ProductListViewModel(cartRepository, repository = productRepository)
        val firstPage = viewModel.uiState.value.products

        viewModel.loadMore()

        assertThat(viewModel.uiState.value.products).startsWith(*firstPage.toTypedArray())
    }

    @Test
    @DisplayName("마지막 페이지까지 읽으면 더보기가 불가능해진다")
    fun cannotLoadMoreOnLastPage() {
        val viewModel = ProductListViewModel(cartRepository, repository = productRepository)

        viewModel.loadMore()
        viewModel.loadMore()

        assertThat(viewModel.uiState.value.products).hasSize(50)
        assertThat(viewModel.uiState.value.canLoadMore).isFalse()
    }

    @Test
    @DisplayName("마지막 페이지에서 더보기를 더 눌러도 목록이 늘어나지 않는다")
    fun loadMoreOnLastPageDoesNothing() {
        val viewModel = ProductListViewModel(cartRepository, repository = productRepository)

        repeat(5) { viewModel.loadMore() }

        assertThat(viewModel.uiState.value.products).hasSize(50)
    }

    @Test
    @DisplayName("더보기로 쌓인 목록에 같은 상품이 중복되지 않는다")
    fun loadMoreDoesNotDuplicateProducts() {
        val viewModel = ProductListViewModel(cartRepository, repository = productRepository)

        viewModel.loadMore()
        viewModel.loadMore()

        val keys = viewModel.uiState.value.products.map { item -> item.key() }
        assertThat(keys).doesNotHaveDuplicates()
    }

    @Test
    @DisplayName("생성 시에는 아무 상품도 장바구니에 담겨 있지 않다")
    fun startsWithNothingInCart() {
        val viewModel = ProductListViewModel(cartRepository, repository = productRepository)

        assertThat(viewModel.uiState.value.products).noneMatch { item -> item.isInCart() }
    }

    @Test
    @DisplayName("담기를 누르면 해당 상품이 장바구니에 반영된다")
    fun addToCartReflectsQuantity() {
        val viewModel = ProductListViewModel(cartRepository, repository = productRepository)
        val key = viewModel.uiState.value.products.first().key()

        viewModel.addToCart(key)

        val item = viewModel.uiState.value.products.first { candidate -> candidate.key() == key }
        assertThat(item.isInCart()).isTrue()
        assertThat(item.cartQuantityDisplayValue()).isEqualTo("1")
    }

    @Test
    @DisplayName("수량 증가를 누르면 표시 수량이 늘어난다")
    fun increaseReflectsQuantity() {
        val viewModel = ProductListViewModel(cartRepository, repository = productRepository)
        val key = viewModel.uiState.value.products.first().key()
        viewModel.addToCart(key)

        viewModel.increase(key)

        val item = viewModel.uiState.value.products.first { candidate -> candidate.key() == key }
        assertThat(item.cartQuantityDisplayValue()).isEqualTo("2")
    }

    @Test
    @DisplayName("수량 1에서 감소를 누르면 다시 장바구니에 없는 상태가 된다")
    fun decreaseAtMinimumRemovesFromCart() {
        val viewModel = ProductListViewModel(cartRepository, repository = productRepository)
        val key = viewModel.uiState.value.products.first().key()
        viewModel.addToCart(key)

        viewModel.decrease(key)

        val item = viewModel.uiState.value.products.first { candidate -> candidate.key() == key }
        assertThat(item.isInCart()).isFalse()
    }

    @Test
    @DisplayName("장바구니가 비어 있으면 배지는 0이다")
    fun cartBadgeIsZeroWhenEmpty() {
        val viewModel = ProductListViewModel(cartRepository, repository = productRepository)

        assertThat(viewModel.uiState.value.cartBadgeText).isEqualTo("0")
    }

    @Test
    @DisplayName("다른 화면에서 장바구니가 바뀌면 배지와 수량 표시가 갱신된다")
    fun refreshesWhenCartChangesElsewhere() {
        val viewModel = ProductListViewModel(cartRepository, repository = productRepository)
        val key = viewModel.uiState.value.products.first().key()
        // 상세 화면이 하는 것처럼 다른 ViewModel 을 통해 같은 저장소에 담는다
        ProductDetailViewModel(key, cartRepository, productRepository = productRepository).addToCart()

        cartRepository.increase(key)

        val item = viewModel.uiState.value.products.first { candidate -> candidate.key() == key }
        assertThat(item.cartQuantityDisplayValue()).isEqualTo("2")
        assertThat(viewModel.uiState.value.cartBadgeText).isEqualTo("2")
    }

    @Test
    @DisplayName("생성 시에는 최근 본 상품이 없다")
    fun startsWithNoRecentlyViewed() {
        val viewModel = ProductListViewModel(cartRepository, recentlyViewedRepository, productRepository)

        assertThat(viewModel.uiState.value.recentlyViewed).isEmpty()
    }

    @Test
    @DisplayName("상세 화면에서 상품을 보면 최근 본 상품 목록에 반영된다")
    fun refreshesWhenRecentlyViewedChangesElsewhere() {
        val viewModel = ProductListViewModel(cartRepository, recentlyViewedRepository, productRepository)
        val product = testProduct("p1")

        recentlyViewedRepository.recordViewed(product)

        assertThat(viewModel.uiState.value.recentlyViewed.map { viewed -> viewed.key() }).containsExactly("p1")
    }

    @Test
    @DisplayName("생성 시 로딩 상태로 시작해 서버 응답을 받으면 로딩이 끝난다")
    fun startsLoadingThenFinishesOnResponse() {
        val manualApi = ManualProductApi()
        val viewModel = ProductListViewModel(cartRepository, repository = ProductRepositoryImpl(manualApi))
        assertThat(viewModel.uiState.value.isLoading).isTrue()

        manualApi.completeWith(fakeProductResponses())

        assertThat(viewModel.uiState.value.isLoading).isFalse()
        assertThat(viewModel.uiState.value.products).hasSize(20)
    }

    @Test
    @DisplayName("서버에 연결하지 못하면 로딩이 끝나고 실패 상태가 된다")
    fun marksLoadFailedWhenRequestFails() {
        val manualApi = ManualProductApi()
        val viewModel = ProductListViewModel(cartRepository, repository = ProductRepositoryImpl(manualApi))

        manualApi.failWith()

        assertThat(viewModel.uiState.value.isLoading).isFalse()
        assertThat(viewModel.uiState.value.hasLoadFailed).isTrue()
    }

    @Test
    @DisplayName("HTTP 500 응답도 실패 상태로 표시한다")
    fun marksLoadFailedOnServerError() {
        val manualApi = ManualProductApi()
        val viewModel = ProductListViewModel(cartRepository, repository = ProductRepositoryImpl(manualApi))

        manualApi.completeWithServerError()

        assertThat(viewModel.uiState.value.hasLoadFailed).isTrue()
    }

    @Test
    @DisplayName("응답을 정상으로 받으면 실패 상태가 아니다")
    fun doesNotMarkLoadFailedOnSuccess() {
        val manualApi = ManualProductApi()
        val viewModel = ProductListViewModel(cartRepository, repository = ProductRepositoryImpl(manualApi))

        manualApi.completeWith(fakeProductResponses())

        assertThat(viewModel.uiState.value.hasLoadFailed).isFalse()
    }

    @Test
    @DisplayName("다시 시도를 누르면 실패 표시를 지우고 로딩 상태로 돌아간다")
    fun retryReturnsToLoadingState() {
        val manualApi = ManualProductApi()
        val viewModel = ProductListViewModel(cartRepository, repository = ProductRepositoryImpl(manualApi))
        manualApi.failWith()

        viewModel.retry()

        assertThat(viewModel.uiState.value.isLoading).isTrue()
        assertThat(viewModel.uiState.value.hasLoadFailed).isFalse()
    }

    @Test
    @DisplayName("다시 시도 후 응답이 오면 상품 목록이 채워진다")
    fun retryLoadsProductsOnSecondAttempt() {
        val manualApi = ManualProductApi()
        val viewModel = ProductListViewModel(cartRepository, repository = ProductRepositoryImpl(manualApi))
        manualApi.failWith()

        viewModel.retry()
        manualApi.completeWith(fakeProductResponses())

        assertThat(viewModel.uiState.value.isLoading).isFalse()
        assertThat(viewModel.uiState.value.hasLoadFailed).isFalse()
        assertThat(viewModel.uiState.value.products).hasSize(20)
    }

    @Test
    @DisplayName("다시 시도해도 또 실패하면 실패 상태로 되돌아온다")
    fun retryKeepsFailedStateWhenRequestFailsAgain() {
        val manualApi = ManualProductApi()
        val viewModel = ProductListViewModel(cartRepository, repository = ProductRepositoryImpl(manualApi))
        manualApi.failWith()

        viewModel.retry()
        manualApi.failWith()

        assertThat(viewModel.uiState.value.hasLoadFailed).isTrue()
    }
}
