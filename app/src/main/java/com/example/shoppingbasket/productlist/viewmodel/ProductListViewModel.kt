package com.example.shoppingbasket.productlist.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.shoppingbasket.common.model.Page
import com.example.shoppingbasket.productlist.model.Product
import com.example.shoppingbasket.productlist.model.ProductRepository

class ProductListViewModel(
    private val repository: ProductRepository = ProductRepository()
) : ViewModel() {

    // 지금까지 읽어들인 마지막 페이지. 더보기를 누를 때마다 다음 페이지로 넘어간다.
    private var loadedPage: Page = repository.firstPage()

    private val _uiState = mutableStateOf(firstPageState())
    val uiState: State<ProductListUiState> = _uiState

    // 다음 페이지를 읽어 기존 목록 뒤에 이어 붙인다 (교체가 아니라 누적)
    fun loadMore() {
        if (!repository.hasPageAfter(loadedPage)) {
            return
        }
        loadedPage = loadedPage.next()
        _uiState.value = appendedState()
    }

    private fun firstPageState(): ProductListUiState {
        return ProductListUiState(productsOf(loadedPage), repository.hasPageAfter(loadedPage))
    }

    private fun appendedState(): ProductListUiState {
        val products = _uiState.value.products + productsOf(loadedPage)
        return ProductListUiState(products, repository.hasPageAfter(loadedPage))
    }

    private fun productsOf(page: Page): List<Product> = repository.findPage(page).items()
}
