package com.example.shoppingbasket.basket.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.shoppingbasket.common.data.local.ShoppingBasketDatabase
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

// 실제 Room/SQLite 위에서 CartItemDao 가 올바르게 동작하는지 검증한다 (JVM 단위 테스트는 FakeCartItemDao 로 대체)
@RunWith(AndroidJUnit4::class)
class CartItemDaoTest {

    private lateinit var database: ShoppingBasketDatabase
    private lateinit var dao: CartItemDao

    @Before
    fun createDatabase() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        database = Room.inMemoryDatabaseBuilder(context, ShoppingBasketDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = database.cartItemDao()
    }

    @After
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun findAllReturnsEmptyListInitially() {
        assert(dao.findAll().isEmpty())
    }

    @Test
    fun replaceAllInsertsRowsOrderedBySortOrder() {
        dao.replaceAll(
            listOf(
                CartItemEntity("p2", "상품-p2", 20_000, "https://example.com/p2", 1, 1),
                CartItemEntity("p1", "상품-p1", 10_000, "https://example.com/p1", 2, 0)
            )
        )

        val rows = dao.findAll()

        assert(rows.map { entity -> entity.productId } == listOf("p1", "p2"))
    }

    @Test
    fun replaceAllRemovesRowsNotInNewList() {
        dao.replaceAll(listOf(CartItemEntity("p1", "상품-p1", 10_000, "https://example.com/p1", 1, 0)))

        dao.replaceAll(listOf(CartItemEntity("p2", "상품-p2", 20_000, "https://example.com/p2", 1, 0)))

        assert(dao.findAll().map { entity -> entity.productId } == listOf("p2"))
    }
}
