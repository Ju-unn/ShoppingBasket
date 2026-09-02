package com.example.shoppingbasket.recentlyviewed.model

import com.example.shoppingbasket.common.fixture.testProduct
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class RecentlyViewedProductsTest {

    @Test
    @DisplayName("상품을 보면 맨 앞에 추가된다")
    fun viewedAddsToFront() {
        val recentlyViewed = RecentlyViewedProducts(emptyList())
            .viewed(testProduct("p1"))
            .viewed(testProduct("p2"))

        assertThat(recentlyViewed.items().map { product -> product.key() }).containsExactly("p2", "p1")
    }

    @Test
    @DisplayName("이미 본 상품을 다시 보면 중복 추가하지 않고 맨 앞으로만 옮긴다")
    fun viewedAgainMovesToFrontWithoutDuplicate() {
        val recentlyViewed = RecentlyViewedProducts(emptyList())
            .viewed(testProduct("p1"))
            .viewed(testProduct("p2"))
            .viewed(testProduct("p1"))

        assertThat(recentlyViewed.items().map { product -> product.key() }).containsExactly("p1", "p2")
    }

    @Test
    @DisplayName("최대 10개까지만 보관하고 오래된 항목은 밀려난다")
    fun viewedKeepsAtMostTenItems() {
        var recentlyViewed = RecentlyViewedProducts(emptyList())
        (1..11).forEach { number -> recentlyViewed = recentlyViewed.viewed(testProduct("p$number")) }

        assertThat(recentlyViewed.items()).hasSize(10)
        assertThat(recentlyViewed.items().map { product -> product.key() }).doesNotContain("p1")
    }

    @Test
    @DisplayName("특정 상품을 제외한 가장 최근 상품을 반환한다")
    fun latestExcludingReturnsMostRecentOtherProduct() {
        val recentlyViewed = RecentlyViewedProducts(emptyList())
            .viewed(testProduct("p1"))
            .viewed(testProduct("p2"))

        assertThat(recentlyViewed.latestExcluding(testProduct("p2"))?.key()).isEqualTo("p1")
    }

    @Test
    @DisplayName("제외할 상품밖에 없으면 null을 반환한다")
    fun latestExcludingReturnsNullWhenOnlyExcludedProductExists() {
        val recentlyViewed = RecentlyViewedProducts(emptyList()).viewed(testProduct("p1"))

        assertThat(recentlyViewed.latestExcluding(testProduct("p1"))).isNull()
    }
}
