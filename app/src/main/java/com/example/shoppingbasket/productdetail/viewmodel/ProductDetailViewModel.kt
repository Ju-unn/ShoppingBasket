package com.example.shoppingbasket.productdetail.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.shoppingbasket.basket.model.CartRepository
import com.example.shoppingbasket.productlist.model.ProductRepository

class ProductDetailViewModel(
    productKey: String,
    private val cartRepository: CartRepository,
    productRepository: ProductRepository = ProductRepository()
) : ViewModel() {

    private val _uiState = mutableStateOf(ProductDetailUiState(product = productRepository.findById(productKey)))
    val uiState: State<ProductDetailUiState> = _uiState

    fun addToCart() {
        val product = _uiState.value.product ?: return
        cartRepository.add(product)
    }
}
