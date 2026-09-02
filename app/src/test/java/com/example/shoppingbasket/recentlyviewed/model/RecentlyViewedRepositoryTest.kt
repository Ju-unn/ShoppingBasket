package com.example.shoppingbasket.recentlyviewed.model

import com.example.shoppingbasket.common.fixture.testProduct
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class RecentlyViewedRepositoryTest {

    private val repository = RecentlyViewedRepository()

    @Test
    @DisplayName("상품을 기록하면 조회 결과에 반영된다")
    fun recordViewed() {
        repository.recordViewed(testProduct("p1"))

        assertThat(repository.findAll().items().map { product -> product.key() }).containsExactly("p1")
    }

    @Test
    @DisplayName("특정 상품을 제외한 가장 최근 상품을 조회한다")
    fun latestExcluding() {
        repository.recordViewed(testProduct("p1"))
        repository.recordViewed(testProduct("p2"))

        assertThat(repository.latestExcluding(testProduct("p2"))?.key()).isEqualTo("p1")
    }

    @Test
    @DisplayName("변경 리스너를 등록하면 기록 시 알림을 받는다")
    fun notifiesChangeListenerOnRecord() {
        var notifiedCount = 0
        repository.addChangeListener { notifiedCount += 1 }

        repository.recordViewed(testProduct("p1"))

        assertThat(notifiedCount).isEqualTo(1)
    }

    @Test
    @DisplayName("등록 해제한 리스너는 더 이상 알림을 받지 않는다")
    fun removeChangeListener() {
        var notifiedCount = 0
        val listener = { notifiedCount += 1 }
        repository.addChangeListener(listener)
        repository.removeChangeListener(listener)

        repository.recordViewed(testProduct("p1"))

        assertThat(notifiedCount).isEqualTo(0)
    }
}
