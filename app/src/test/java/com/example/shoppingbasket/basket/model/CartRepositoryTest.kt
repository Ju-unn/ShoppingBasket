package com.example.shoppingbasket.basket.model

import com.example.shoppingbasket.common.fixture.clearCartRepository
import com.example.shoppingbasket.common.fixture.testProduct
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class CartRepositoryTest {

    @BeforeEach
    fun clearRepository() {
        clearCartRepository()
    }

    @Test
    @DisplayName("상품을 추가하면 조회 결과에 반영된다")
    fun add() {
        CartRepository.add(testProduct("p1", 10_000))

        assertThat(CartRepository.findAll().items()).hasSize(1)
    }

    @Test
    @DisplayName("key로 상품을 제거하면 조회 결과에서 사라진다")
    fun remove() {
        CartRepository.add(testProduct("p1", 10_000))

        CartRepository.remove("p1")

        assertThat(CartRepository.findAll().isEmpty()).isTrue()
    }
}
