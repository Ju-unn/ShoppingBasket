package com.example.shoppingbasket.basket.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.shoppingbasket.basket.model.CartRepository
import com.example.shoppingbasket.common.model.Page

class BasketViewModel(private val cartRepository: CartRepository) : ViewModel() {

    // 지금 보고 있는 페이지. 더보기와 달리 페이지를 누적하지 않고 통째로 갈아끼운다.
    private var currentPage: Page = cartRepository.firstPage()

    private val _uiState = mutableStateOf(pageState())
    val uiState: State<BasketUiState> = _uiState

    fun nextPage() {
        if (!cartRepository.hasPageAfter(currentPage)) {
            return
        }
        currentPage = currentPage.next()
        _uiState.value = pageState()
    }

    fun previousPage() {
        if (!currentPage.hasPrevious()) {
            return
        }
        currentPage = currentPage.previous()
        _uiState.value = pageState()
    }

    fun remove(key: String) {
        cartRepository.remove(key)
        // 지운 결과 현재 페이지가 비었으면 상품이 남아 있는 마지막 페이지로 당긴다
        currentPage = cartRepository.coercePage(currentPage)
        _uiState.value = pageState()
    }

    private fun pageState(): BasketUiState {
        return BasketUiState(
            cartItems = cartRepository.findPage(currentPage).items(),
            pageNumber = currentPage.number(),
            canGoPrevious = currentPage.hasPrevious(),
            canGoNext = cartRepository.hasPageAfter(currentPage)
        )
    }
}
