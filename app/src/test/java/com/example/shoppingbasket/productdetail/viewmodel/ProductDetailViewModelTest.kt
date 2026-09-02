package com.example.shoppingbasket.productdetail.viewmodel

import com.example.shoppingbasket.basket.model.CartRepository
import com.example.shoppingbasket.common.fixture.fakeProductRepository
import com.example.shoppingbasket.recentlyviewed.model.RecentlyViewedRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class ProductDetailViewModelTest {

    // 테스트마다 새 저장소를 만들어 쓰므로 앞선 테스트의 장바구니가 남지 않는다
    private val cartRepository = CartRepository()
    private val recentlyViewedRepository = RecentlyViewedRepository()
    // 상세 화면은 상품 목록이 받아온 캐시를 재사용하므로(4단계 결정 5번) 미리 채워진 가짜 저장소를 쓴다
    private val productRepository = fakeProductRepository()

    @Test
    @DisplayName("존재하는 상품 key로 생성하면 해당 상품을 상태로 반영한다")
    fun loadsExistingProduct() {
        val viewModel = ProductDetailViewModel("p1", cartRepository, productRepository = productRepository)

        assertThat(viewModel.uiState.value.product?.key()).isEqualTo("p1")
    }

    @Test
    @DisplayName("존재하지 않는 상품 key면 상태의 상품이 없다")
    fun loadsMissingProduct() {
        val viewModel = ProductDetailViewModel("없는id", cartRepository, productRepository = productRepository)

        assertThat(viewModel.uiState.value.product).isNull()
    }

    @Test
    @DisplayName("장바구니 담기를 호출하면 저장소에 상품이 추가된다")
    fun addToCart() {
        val viewModel = ProductDetailViewModel("p1", cartRepository, productRepository = productRepository)

        viewModel.addToCart()

        assertThat(cartRepository.findAll().items()).hasSize(1)
    }

    @Test
    @DisplayName("수량 증가를 누르면 로컬 수량 표시가 늘어난다")
    fun increaseChangesLocalQuantity() {
        val viewModel = ProductDetailViewModel("p1", cartRepository, productRepository = productRepository)

        viewModel.increase()

        assertThat(viewModel.uiState.value.quantityDisplayValue()).isEqualTo("2")
    }

    @Test
    @DisplayName("최소 수량에서 수량 감소를 눌러도 1 밑으로 내려가지 않는다")
    fun decreaseStaysAtMinimum() {
        val viewModel = ProductDetailViewModel("p1", cartRepository, productRepository = productRepository)

        viewModel.decrease()

        assertThat(viewModel.uiState.value.quantityDisplayValue()).isEqualTo("1")
    }

    @Test
    @DisplayName("수량을 늘린 뒤 담으면 늘어난 수량만큼 저장소에 반영된다")
    fun addToCartUsesIncreasedQuantity() {
        val viewModel = ProductDetailViewModel("p1", cartRepository, productRepository = productRepository)
        viewModel.increase()
        viewModel.increase()

        viewModel.addToCart()

        assertThat(cartRepository.findAll().items().first().displayQuantity()).isEqualTo("3")
    }

    @Test
    @DisplayName("장바구니에 없는 상품이면 수량을 늘려도 로컬 상태만 바뀐다")
    fun increaseDoesNotAffectCartUntilAdded() {
        val viewModel = ProductDetailViewModel("p1", cartRepository, productRepository = productRepository)

        viewModel.increase()

        assertThat(cartRepository.findAll().items()).isEmpty()
    }

    @Test
    @DisplayName("진입 즉시 최근 본 상품으로 기록된다")
    fun recordsProductAsRecentlyViewedOnCreate() {
        ProductDetailViewModel("p1", cartRepository, recentlyViewedRepository, productRepository)

        assertThat(recentlyViewedRepository.findAll().items().map { product -> product.key() }).containsExactly("p1")
    }

    @Test
    @DisplayName("마지막으로 본 상품이 없으면(첫 방문) 상태에도 없다")
    fun startsWithoutLastViewedProduct() {
        val viewModel = ProductDetailViewModel("p1", cartRepository, recentlyViewedRepository, productRepository)

        assertThat(viewModel.uiState.value.lastViewedProduct).isNull()
    }

    @Test
    @DisplayName("이전에 다른 상품을 봤다면 자기 자신을 제외한 마지막 상품이 상태에 담긴다")
    fun exposesLastViewedProductExcludingSelf() {
        ProductDetailViewModel("p2", cartRepository, recentlyViewedRepository, productRepository)

        val viewModel = ProductDetailViewModel("p1", cartRepository, recentlyViewedRepository, productRepository)

        assertThat(viewModel.uiState.value.lastViewedProduct?.key()).isEqualTo("p2")
    }
}
