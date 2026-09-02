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

    fun addToCart() {
        val state = _uiState.value
        val product = state.product ?: return
        cartRepository.add(product, state.quantity)
    }

    // 진입 즉시 최근 본 상품으로 기록하고, 자기 자신을 제외한 마지막 상품을 함께 담는다 (4단계 결정 8번)
    private fun loadedState(product: Product?): ProductDetailUiState {
        if (product == null) {
            return ProductDetailUiState()
        }
        recentlyViewedRepository.recordViewed(product)
        return ProductDetailUiState(product = product, lastViewedProduct = recentlyViewedRepository.latestExcluding(product))
    }
}
