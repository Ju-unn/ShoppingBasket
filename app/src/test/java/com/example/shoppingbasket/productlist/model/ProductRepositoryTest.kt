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

    @Test
    @DisplayName("id로 상품을 찾는다")
    fun findById() {
        val repository = ProductRepository()

        assertThat(repository.findById("p1")?.key()).isEqualTo("p1")
        assertThat(repository.findById("없는id")).isNull()
    }
}
