package com.example.shoppingbasket.basket

import com.example.shoppingbasket.basket.viewmodel.BasketViewModel
import com.example.shoppingbasket.common.fixture.clearCartRepository
import com.example.shoppingbasket.productdetail.viewmodel.ProductDetailViewModel
import com.example.shoppingbasket.productlist.viewmodel.ProductListViewModel
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

/**
 * 상품 목록 -> 상품 상세 -> 장바구니 로 이어지는 화면 간 흐름을 실제 저장소로 검증한다.
 * 각 ViewModel 단위 테스트가 통과해도 화면을 잇는 과정에서 끊길 수 있어 따로 둔다.
 */
class CartFlowTest {

    @BeforeEach
    fun clearCart() {
        clearCartRepository()
    }

    @Test
    @DisplayName("목록에서 고른 상품을 상세에서 담으면 장바구니 화면에 보인다")
    fun addFromDetailAppearsInBasket() {
        val listViewModel = ProductListViewModel()
        val productKey = listViewModel.uiState.value.products.first().key()

        ProductDetailViewModel(productKey).addToCart()

        val basketViewModel = BasketViewModel()
        assertThat(basketViewModel.uiState.value.cartItems).hasSize(1)
        assertThat(basketViewModel.uiState.value.cartItems.first().key()).isEqualTo(productKey)
    }

    @Test
    @DisplayName("더보기로 불러온 뒤쪽 페이지 상품도 담을 수 있다")
    fun addProductFromLoadedPage() {
        val listViewModel = ProductListViewModel()
        listViewModel.loadMore()
        val productKey = listViewModel.uiState.value.products.last().key()

        ProductDetailViewModel(productKey).addToCart()

        assertThat(BasketViewModel().uiState.value.cartItems.first().key()).isEqualTo(productKey)
    }

    @Test
    @DisplayName("여러 상품을 담으면 장바구니 첫 페이지에 순서대로 쌓인다")
    fun addMultipleProducts() {
        val listViewModel = ProductListViewModel()
        val productKeys = listViewModel.uiState.value.products.take(3).map { product -> product.key() }

        productKeys.forEach { productKey -> ProductDetailViewModel(productKey).addToCart() }

        val cartKeys = BasketViewModel().uiState.value.cartItems.map { cartItem -> cartItem.key() }
        assertThat(cartKeys).containsExactlyElementsOf(productKeys)
    }
}
