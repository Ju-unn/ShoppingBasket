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

    @Test
    @DisplayName("첫 페이지는 상품 5개를 반환한다")
    fun findFirstPage() {
        addProducts(8)

        val firstPage = CartRepository.findPage(CartRepository.firstPage())

        assertThat(firstPage.items()).hasSize(5)
    }

    @Test
    @DisplayName("마지막 페이지는 남은 상품만 반환한다")
    fun findLastPage() {
        addProducts(8)

        val lastPage = CartRepository.findPage(CartRepository.firstPage().next())

        assertThat(lastPage.items()).hasSize(3)
    }

    @Test
    @DisplayName("상품이 5개 이하면 다음 페이지가 없다")
    fun hasNoPageAfterWhenSinglePage() {
        addProducts(5)

        assertThat(CartRepository.hasPageAfter(CartRepository.firstPage())).isFalse()
    }

    @Test
    @DisplayName("상품이 5개를 넘으면 다음 페이지가 있다")
    fun hasPageAfterWhenOverflowing() {
        addProducts(6)

        assertThat(CartRepository.hasPageAfter(CartRepository.firstPage())).isTrue()
    }

    @Test
    @DisplayName("마지막 페이지의 상품을 모두 지우면 이전 페이지로 당긴다")
    fun coercePageAfterRemovingLastPage() {
        addProducts(6)
        val secondPage = CartRepository.firstPage().next()

        CartRepository.remove("p6")

        assertThat(CartRepository.coercePage(secondPage).number()).isEqualTo(1)
    }

    private fun addProducts(count: Int) {
        (1..count).forEach { number -> CartRepository.add(testProduct("p$number")) }
    }
}
