package com.example.shoppingbasket.productlist.viewmodel

import com.example.shoppingbasket.common.fixture.testProduct
import com.example.shoppingbasket.common.model.Quantity
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class ProductListItemTest {

    @Test
    @DisplayName("상품 정보를 표시용 메서드로 노출한다")
    fun exposesDisplayValues() {
        val item = ProductListItem(testProduct("p1", 10_000), cartQuantity = null)

        assertThat(item.key()).isEqualTo("p1")
        assertThat(item.displayName()).isEqualTo("상품-p1")
        assertThat(item.displayPrice()).isEqualTo("10,000원")
    }

    @Test
    @DisplayName("장바구니에 없으면 담기지 않은 상태다")
    fun notInCartWhenQuantityIsNull() {
        val item = ProductListItem(testProduct("p1", 10_000), cartQuantity = null)

        assertThat(item.isInCart()).isFalse()
        assertThat(item.cartQuantityDisplayValue()).isEmpty()
    }

    @Test
    @DisplayName("장바구니에 있으면 담긴 수량을 표시한다")
    fun inCartWhenQuantityPresent() {
        val item = ProductListItem(testProduct("p1", 10_000), cartQuantity = Quantity(3))

        assertThat(item.isInCart()).isTrue()
        assertThat(item.cartQuantityDisplayValue()).isEqualTo("3")
    }
}
