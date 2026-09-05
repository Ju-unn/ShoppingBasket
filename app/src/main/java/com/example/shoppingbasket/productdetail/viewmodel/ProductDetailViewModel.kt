package com.example.shoppingbasket.productdetail.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.shoppingbasket.basket.model.CartRepository
import com.example.shoppingbasket.productlist.data.repository.ProductRepositoryImpl
import com.example.shoppingbasket.productlist.domain.model.Product
import com.example.shoppingbasket.productlist.domain.repository.ProductRepository
import com.example.shoppingbasket.recentlyviewed.model.RecentlyViewedRepository

class ProductDetailViewModel(
    productKey: String,
    private val cartRepository: CartRepository,
    private val recentlyViewedRepository: RecentlyViewedRepository = RecentlyViewedRepository(),
    productRepository: ProductRepository = ProductRepositoryImpl()
) : ViewModel() {

    private val _uiState = mutableStateOf(loadedState(productRepository.findById(productKey)))
    val uiState: State<ProductDetailUiState> = _uiState

    fun increase() {
        _uiState.value = _uiState.value.increasedQuantity()
    }

    fun decrease() {
        _uiState.value = _uiState.value.decreasedQuantity()
    }

    // 컴포지션 도중에는 다른 화면의 Compose 상태를 바꿀 수 없다. 그래서 최근 본 상품 기록은
    // 생성 시점이 아니라 View 가 컴포지션을 끝낸 뒤 알려 줄 때 한다 (5단계 I8)
    fun onScreenEntered() {
        val product = _uiState.value.product ?: return
        recentlyViewedRepository.recordViewed(product)
    }

    fun addToCart() {
        val state = _uiState.value
        val product = state.product ?: return
        cartRepository.add(product, state.quantity)
    }

    // 자기 자신을 제외한 마지막으로 본 상품을 함께 담는다 (4단계 결정 8번).
    // 기록 자체는 onScreenEntered() 가 맡으며, 기록 전후로 latestExcluding 의 답은 같다(자기 자신은 어차피 제외됨)
    private fun loadedState(product: Product?): ProductDetailUiState {
        if (product == null) {
            return ProductDetailUiState()
        }
        return ProductDetailUiState(product = product, lastViewedProduct = recentlyViewedRepository.latestExcluding(product))
    }
}
