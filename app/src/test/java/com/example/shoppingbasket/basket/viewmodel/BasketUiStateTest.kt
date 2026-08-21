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
}
