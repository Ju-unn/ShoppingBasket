package com.example.shoppingbasket.productlist.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class ProductRepositoryTest {

    @Test
    @DisplayName("목업 상품 6개를 반환한다")
    fun findAll() {
        val products = ProductRepository().findAll()

        assertThat(products.isEmpty()).isFalse()
        assertThat(products.items()).hasSize(6)
    }
}
