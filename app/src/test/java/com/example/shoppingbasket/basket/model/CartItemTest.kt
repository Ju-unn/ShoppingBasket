package com.example.shoppingbasket.basket.model

import com.example.shoppingbasket.common.model.Money
import com.example.shoppingbasket.common.model.Quantity
import com.example.shoppingbasket.productlist.model.Product
import com.example.shoppingbasket.productlist.model.ProductId
import com.example.shoppingbasket.productlist.model.ProductImageUrl
import com.example.shoppingbasket.productlist.model.ProductName
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class CartItemTest {

    @Test
    @DisplayName("상품 정보와 수량을 표시용 메서드로 노출한다")
    fun exposesDisplayValues() {
        val cartItem = CartItem(product("p1", 10_000), Quantity(2))

        assertThat(cartItem.key()).isEqualTo("p1")
        assertThat(cartItem.displayName()).isEqualTo("상품-p1")
        assertThat(cartItem.displayQuantity()).isEqualTo("2")
        assertThat(cartItem.displayTotalPrice()).isEqualTo("20,000원")
    }

    @Test
    @DisplayName("같은 상품인지 판단한다")
    fun isSameProduct() {
        val cartItem = CartItem(product("p1", 10_000), Quantity(1))

        assertThat(cartItem.isSameProduct(product("p1", 10_000))).isTrue()
        assertThat(cartItem.isSameProduct(product("p2", 10_000))).isFalse()
    }

    @Test
    @DisplayName("수량을 늘린 새 항목을 반환한다")
    fun increasedBy() {
        val cartItem = CartItem(product("p1", 10_000), Quantity(1))

        val increased = cartItem.increasedBy(Quantity(2))

        assertThat(increased.displayQuantity()).isEqualTo("3")
    }

    @Test
    @DisplayName("누적 금액에 자신의 총 가격을 더한다")
    fun accumulate() {
        val cartItem = CartItem(product("p1", 10_000), Quantity(2))

        assertThat(cartItem.accumulate(Money(1_000))).isEqualTo(Money(21_000))
    }

    private fun product(id: String, priceWon: Int): Product {
        return Product(
            ProductId(id),
            ProductName("상품-$id"),
            Money(priceWon),
            ProductImageUrl("https://picsum.photos/seed/$id/600")
        )
    }
}
