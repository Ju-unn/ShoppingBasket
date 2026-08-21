package com.example.shoppingbasket.productlist.model

import com.example.shoppingbasket.common.model.Money
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class ProductListTest {

    @Test
    @DisplayName("담긴 상품 목록을 그대로 반환한다")
    fun items() {
        val product = Product(ProductId("p1"), ProductName("PET보틀-정사각(500ml)"), Money(10_000))
        val productList = ProductList(listOf(product))

        assertThat(productList.items()).containsExactly(product)
    }

    @Test
    @DisplayName("빈 목록 여부를 판단한다")
    fun isEmpty() {
        assertThat(ProductList(emptyList()).isEmpty()).isTrue()
        assertThat(ProductList(listOf(Product(ProductId("p1"), ProductName("이름"), Money(0)))).isEmpty()).isFalse()
    }
}
