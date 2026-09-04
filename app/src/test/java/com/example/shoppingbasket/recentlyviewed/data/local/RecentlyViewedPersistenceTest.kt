package com.example.shoppingbasket.recentlyviewed.data.local

import com.example.shoppingbasket.common.fixture.FakeRecentlyViewedProductDao
import com.example.shoppingbasket.common.fixture.testProduct
import com.example.shoppingbasket.recentlyviewed.model.RecentlyViewedProducts
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.util.concurrent.Executor

class RecentlyViewedPersistenceTest {

    private val dao = FakeRecentlyViewedProductDao()

    // 테스트에서는 백그라운드로 던지지 않고 호출한 스레드에서 즉시 실행해 결정적으로 검증한다
    private val persistence = RecentlyViewedPersistence(dao, writeExecutor = Executor { command -> command.run() })

    @Test
    @DisplayName("저장한 뒤 복원하면 최근 본 상품이 그대로 돌아온다")
    fun saveThenRestore() {
        val products = RecentlyViewedProducts(listOf(testProduct("p1")))

        persistence.save(products)
        val restored = persistence.restore()

        assertThat(restored.items().map { product -> product.key() }).containsExactly("p1")
    }

    @Test
    @DisplayName("저장 순서(최신순)를 그대로 복원한다")
    fun preservesOrder() {
        val products = RecentlyViewedProducts(listOf(testProduct("p2"), testProduct("p1")))

        persistence.save(products)
        val restored = persistence.restore()

        assertThat(restored.items().map { product -> product.key() }).containsExactly("p2", "p1")
    }

    @Test
    @DisplayName("다시 저장하면 이전 내용을 완전히 대체한다")
    fun replacesPreviousContent() {
        persistence.save(RecentlyViewedProducts(listOf(testProduct("p1"))))

        persistence.save(RecentlyViewedProducts(listOf(testProduct("p2"))))

        assertThat(persistence.restore().items().map { product -> product.key() }).containsExactly("p2")
    }

    @Test
    @DisplayName("아무것도 저장하지 않았으면 빈 상태로 복원한다")
    fun restoreWhenEmpty() {
        assertThat(persistence.restore().isEmpty()).isTrue()
    }
}
