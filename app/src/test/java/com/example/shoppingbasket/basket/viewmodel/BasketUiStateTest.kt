package com.example.shoppingbasket.basket.viewmodel

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class BasketUiStateTest {

    @Test
    @DisplayName("기본 상태는 빈 장바구니 목록이다")
    fun defaultState() {
        assertThat(BasketUiState().cartItems).isEmpty()
    }

    @Test
    @DisplayName("기본 상태의 페이지 번호는 1이다")
    fun defaultPageNumber() {
        assertThat(BasketUiState().pageNumber).isEqualTo(1)
    }

    @Test
    @DisplayName("기본 상태에서는 앞뒤로 이동할 수 없다")
    fun defaultCannotMove() {
        assertThat(BasketUiState().canGoPrevious).isFalse()
        assertThat(BasketUiState().canGoNext).isFalse()
    }
}
