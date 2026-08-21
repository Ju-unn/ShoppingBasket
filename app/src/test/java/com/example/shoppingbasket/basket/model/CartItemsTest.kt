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

class CartItemsTest {

    @Test
    @DisplayName("빈 목록 여부를 판단한다")
    fun isEmpty() {
        assertThat(CartItems(emptyList()).isEmpty()).isTrue()
    }

    @Test
    @DisplayName("새 상품을 담으면 항목이 추가된다")
    fun addedNewProduct() {
        val cartItems = CartItems(emptyList()).added(product("p1", 10_000))

        assertThat(cartItems.items()).hasSize(1)
        assertThat(cartItems.items().first().displayQuantity()).isEqualTo("1")
    }

    @Test
    @DisplayName("이미 담긴 상품을 다시 담으면 수량만 늘어난다")
    fun addedExistingProductIncreasesQuantity() {
        val cartItems = CartItems(emptyList())
            .added(product("p1", 10_000))
            .added(product("p1", 10_000))

        assertThat(cartItems.items()).hasSize(1)
        assertThat(cartItems.items().first().displayQuantity()).isEqualTo("2")
    }

    @Test
    @DisplayName("key로 상품을 제거한다")
    fun removed() {
        val cartItems = CartItems(emptyList())
            .added(product("p1", 10_000))
            .added(product("p2", 12_000))
            .removed("p1")

        assertThat(cartItems.items()).hasSize(1)
        assertThat(cartItems.items().first().key()).isEqualTo("p2")
    }

    @Test
    @DisplayName("담긴 상품들의 총액을 합산한다")
    fun totalPrice() {
        val cartItems = CartItems(emptyList())
            .added(product("p1", 10_000), Quantity(2))
            .added(product("p2", 5_000))

        assertThat(cartItems.totalPrice()).isEqualTo(Money(25_000))
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
