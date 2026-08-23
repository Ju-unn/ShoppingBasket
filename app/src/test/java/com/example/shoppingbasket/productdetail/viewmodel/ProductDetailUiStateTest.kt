package com.example.shoppingbasket.productdetail.viewmodel

import com.example.shoppingbasket.common.fixture.testProduct
import com.example.shoppingbasket.common.model.Quantity
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class ProductDetailUiStateTest {

    @Test
    @DisplayName("기본 수량은 1이다")
    fun defaultQuantity() {
        assertThat(ProductDetailUiState().quantity).isEqualTo(Quantity.ONE)
    }

    @Test
    @DisplayName("상품이 없으면 총액 표시가 비어 있다")
    fun totalPriceEmptyWithoutProduct() {
        assertThat(ProductDetailUiState().totalPriceDisplayValue()).isEmpty()
    }

    @Test
    @DisplayName("총액은 단가에 수량을 곱한 값이다")
    fun totalPriceMultipliesQuantity() {
        val uiState = ProductDetailUiState(product = testProduct("p1", 10_000), quantity = Quantity(3))

        assertThat(uiState.totalPriceDisplayValue()).isEqualTo("30,000원")
    }

    @Test
    @DisplayName("수량 증가 시 1 늘어난다")
    fun increasedQuantity() {
        val uiState = ProductDetailUiState(quantity = Quantity(1)).increasedQuantity()

        assertThat(uiState.quantityDisplayValue()).isEqualTo("2")
    }

    @Test
    @DisplayName("수량 감소 시 1 줄어든다")
    fun decreasedQuantity() {
        val uiState = ProductDetailUiState(quantity = Quantity(2)).decreasedQuantity()

        assertThat(uiState.quantityDisplayValue()).isEqualTo("1")
    }

    @Test
    @DisplayName("최소 수량(1)에서는 더 줄어들지 않는다")
    fun decreasedQuantityStaysAtMinimum() {
        val uiState = ProductDetailUiState(quantity = Quantity.ONE).decreasedQuantity()

        assertThat(uiState.quantityDisplayValue()).isEqualTo("1")
    }
}
