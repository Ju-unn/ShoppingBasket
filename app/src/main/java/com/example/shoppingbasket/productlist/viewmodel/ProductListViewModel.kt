package com.example.shoppingbasket.productlist.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.shoppingbasket.productlist.model.ProductRepository

class ProductListViewModel(
    repository: ProductRepository = ProductRepository()
) : ViewModel() {

    private val _uiState = mutableStateOf(ProductListUiState(products = repository.findAll().items()))
    val uiState: State<ProductListUiState> = _uiState
}
