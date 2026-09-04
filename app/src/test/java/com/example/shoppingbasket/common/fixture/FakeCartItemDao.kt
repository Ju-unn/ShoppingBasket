package com.example.shoppingbasket.common.fixture

import com.example.shoppingbasket.basket.data.local.CartItemDao
import com.example.shoppingbasket.basket.data.local.CartItemEntity

// Room 없이 CartPersistence 를 검증하기 위한 인메모리 가짜 DAO
class FakeCartItemDao : CartItemDao {

    private var rows = emptyList<CartItemEntity>()

    override fun findAll(): List<CartItemEntity> = rows

    override fun replaceAll(items: List<CartItemEntity>) {
        clear()
        insertAll(items)
    }

    override fun clear() {
        rows = emptyList()
    }

    override fun insertAll(items: List<CartItemEntity>) {
        rows = rows + items
    }
}
