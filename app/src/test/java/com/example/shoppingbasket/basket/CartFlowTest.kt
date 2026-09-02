package com.example.shoppingbasket.basket

import com.example.shoppingbasket.basket.viewmodel.BasketViewModel
import com.example.shoppingbasket.basket.model.CartRepository
import com.example.shoppingbasket.common.fixture.fakeProductRepository
import com.example.shoppingbasket.productdetail.viewmodel.ProductDetailViewModel
import com.example.shoppingbasket.productlist.viewmodel.ProductListViewModel
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

/**
 * 상품 목록 -> 상품 상세 -> 장바구니 로 이어지는 화면 간 흐름을 실제 저장소로 검증한다.
 * 각 ViewModel 단위 테스트가 통과해도 화면을 잇는 과정에서 끊길 수 있어 따로 둔다.
 */
class CartFlowTest {

    // 실제 앱에서 Application 이 들고 있는 것과 같은, 화면들이 공유하는 장바구니 저장소
    private val cartRepository = CartRepository()
    // 실제 앱에서는 서버 fetch 로 채워지지만, 이 테스트는 화면 간 장바구니 흐름만 확인하므로 미리 채워진 가짜 저장소를 쓴다
    private val productRepository = fakeProductRepository()

    @Test
    @DisplayName("목록에서 고른 상품을 상세에서 담으면 장바구니 화면에 보인다")
    fun addFromDetailAppearsInBasket() {
        val listViewModel = ProductListViewModel(cartRepository, repository = productRepository)
        val productKey = listViewModel.uiState.value.products.first().key()

        ProductDetailViewModel(productKey, cartRepository, productRepository = productRepository).addToCart()

        val basketViewModel = BasketViewModel(cartRepository)
        assertThat(basketViewModel.uiState.value.cartItems).hasSize(1)
        assertThat(basketViewModel.uiState.value.cartItems.first().key()).isEqualTo(productKey)
    }

    @Test
    @DisplayName("더보기로 불러온 뒤쪽 페이지 상품도 담을 수 있다")
    fun addProductFromLoadedPage() {
        val listViewModel = ProductListViewModel(cartRepository, repository = productRepository)
        listViewModel.loadMore()
        val productKey = listViewModel.uiState.value.products.last().key()

        ProductDetailViewModel(productKey, cartRepository, productRepository = productRepository).addToCart()

        assertThat(BasketViewModel(cartRepository).uiState.value.cartItems.first().key()).isEqualTo(productKey)
    }

    @Test
    @DisplayName("여러 상품을 담으면 장바구니 첫 페이지에 순서대로 쌓인다")
    fun addMultipleProducts() {
        val listViewModel = ProductListViewModel(cartRepository, repository = productRepository)
        val productKeys = listViewModel.uiState.value.products.take(3).map { item -> item.key() }

        productKeys.forEach { productKey ->
            ProductDetailViewModel(productKey, cartRepository, productRepository = productRepository).addToCart()
        }

        val cartKeys = BasketViewModel(cartRepository).uiState.value.cartItems.map { cartItem -> cartItem.key() }
        assertThat(cartKeys).containsExactlyElementsOf(productKeys)
    }
}
