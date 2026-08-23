package com.example.shoppingbasket.basket.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.shoppingbasket.basket.model.CartRepository
import com.example.shoppingbasket.common.model.Page

class BasketViewModel(private val cartRepository: CartRepository) : ViewModel() {

    // 지금 보고 있는 페이지. 더보기와 달리 페이지를 누적하지 않고 통째로 갈아끼운다.
    private var currentPage: Page = cartRepository.firstPage()

    // 다른 화면(목록/상세)에서 장바구니가 바뀌어도 현재 페이지 표시를 갱신하기 위한 구독 (3단계 확정 사항)
    private val cartChangeListener = { refreshCartState() }

    private val _uiState = mutableStateOf(pageState())
    val uiState: State<BasketUiState> = _uiState

    init {
        cartRepository.addChangeListener(cartChangeListener)
    }

    override fun onCleared() {
        cartRepository.removeChangeListener(cartChangeListener)
    }

    fun nextPage() {
        if (!cartRepository.hasPageAfter(currentPage)) {
            return
        }
        currentPage = currentPage.next()
        refreshCartState()
    }

    fun previousPage() {
        if (!currentPage.hasPrevious()) {
            return
        }
        currentPage = currentPage.previous()
        refreshCartState()
    }

    fun remove(key: String) {
        cartRepository.remove(key)
        // 지운 결과 현재 페이지가 비었으면 상품이 남아 있는 마지막 페이지로 당긴다
        currentPage = cartRepository.coercePage(currentPage)
        refreshCartState()
    }

    fun increase(key: String) {
        cartRepository.increase(key)
    }

    // 수량 1에서 감소하면 항목이 지워지므로 remove 와 같이 페이지를 다시 당긴다
    fun decrease(key: String) {
        cartRepository.decrease(key)
        currentPage = cartRepository.coercePage(currentPage)
        refreshCartState()
    }

    private fun refreshCartState() {
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
