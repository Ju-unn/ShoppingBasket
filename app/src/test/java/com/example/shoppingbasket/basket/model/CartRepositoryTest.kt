package com.example.shoppingbasket.basket.model

import com.example.shoppingbasket.common.model.Money
import com.example.shoppingbasket.productlist.model.Product
import com.example.shoppingbasket.productlist.model.ProductId
import com.example.shoppingbasket.productlist.model.ProductImageUrl
import com.example.shoppingbasket.productlist.model.ProductName
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class CartRepositoryTest {

    @BeforeEach
    fun clearRepository() {
        CartRepository.findAll().items().forEach { item -> CartRepository.remove(item.key()) }
    }

    @Test
    @DisplayName("상품을 추가하면 조회 결과에 반영된다")
    fun add() {
        CartRepository.add(product("p1", 10_000))

        assertThat(CartRepository.findAll().items()).hasSize(1)
    }

    @Test
    @DisplayName("key로 상품을 제거하면 조회 결과에서 사라진다")
    fun remove() {
        CartRepository.add(product("p1", 10_000))

        CartRepository.remove("p1")

        assertThat(CartRepository.findAll().isEmpty()).isTrue()
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
