package com.example.shoppingbasket.productlist.viewmodel

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class ProductListUiStateTest {

    @Test
    @DisplayName("기본 상태는 빈 상품 목록이다")
    fun defaultState() {
        assertThat(ProductListUiState().products).isEmpty()
    }

    @Test
    @DisplayName("기본 상태에서는 더보기가 불가능하다")
    fun defaultCannotLoadMore() {
        assertThat(ProductListUiState().canLoadMore).isFalse()
    }

    @Test
    @DisplayName("기본 상태의 장바구니 배지는 0이다")
    fun defaultCartBadgeText() {
        assertThat(ProductListUiState().cartBadgeText).isEqualTo("0")
    }

    @Test
    @DisplayName("기본 상태는 최근 본 상품이 없다")
    fun defaultRecentlyViewed() {
        assertThat(ProductListUiState().recentlyViewed).isEmpty()
    }

    @Test
    @DisplayName("기본 상태는 로딩 중이 아니다")
    fun defaultIsNotLoading() {
        assertThat(ProductListUiState().isLoading).isFalse()
    }

    @Test
    @DisplayName("기본 상태는 불러오기 실패가 아니다")
    fun defaultHasNotLoadFailed() {
        assertThat(ProductListUiState().hasLoadFailed).isFalse()
    }
}
