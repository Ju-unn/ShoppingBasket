package com.example.shoppingbasket.basket.data.local

import com.example.shoppingbasket.basket.model.CartItem
import com.example.shoppingbasket.basket.model.CartItems
import com.example.shoppingbasket.common.fixture.FakeCartItemDao
import com.example.shoppingbasket.common.fixture.testProduct
import com.example.shoppingbasket.common.model.Quantity
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.util.concurrent.Executor

class CartPersistenceTest {

    private val dao = FakeCartItemDao()

    // 테스트에서는 백그라운드로 던지지 않고 호출한 스레드에서 즉시 실행해 결정적으로 검증한다
    private val persistence = CartPersistence(dao, writeExecutor = Executor { command -> command.run() })

    @Test
    @DisplayName("저장한 뒤 복원하면 담긴 상품과 수량이 그대로 돌아온다")
    fun saveThenRestore() {
        val cartItems = CartItems(listOf(CartItem(testProduct("p1", 10_000), Quantity(3))))

        persistence.save(cartItems)
        val restored = persistence.restore()

        assertThat(restored.items()).hasSize(1)
        assertThat(restored.items().first().key()).isEqualTo("p1")
        assertThat(restored.items().first().displayQuantity()).isEqualTo("3")
    }

    @Test
    @DisplayName("저장 순서를 그대로 복원한다")
    fun preservesOrder() {
        val cartItems = CartItems(
            listOf(
                CartItem(testProduct("p1", 10_000), Quantity(1)),
                CartItem(testProduct("p2", 20_000), Quantity(1))
            )
        )

        persistence.save(cartItems)
        val restored = persistence.restore()

        assertThat(restored.items().map { item -> item.key() }).containsExactly("p1", "p2")
    }

    @Test
    @DisplayName("다시 저장하면 이전 내용을 완전히 대체한다")
    fun replacesPreviousContent() {
        persistence.save(CartItems(listOf(CartItem(testProduct("p1", 10_000), Quantity(1)))))

        persistence.save(CartItems(listOf(CartItem(testProduct("p2", 20_000), Quantity(1)))))

        assertThat(persistence.restore().items().map { item -> item.key() }).containsExactly("p2")
    }

    @Test
    @DisplayName("아무것도 저장하지 않았으면 빈 상태로 복원한다")
    fun restoreWhenEmpty() {
        assertThat(persistence.restore().isEmpty()).isTrue()
    }
}
