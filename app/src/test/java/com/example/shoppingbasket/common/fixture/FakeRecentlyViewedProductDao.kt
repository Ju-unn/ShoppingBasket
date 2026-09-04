package com.example.shoppingbasket.common.fixture

import com.example.shoppingbasket.recentlyviewed.data.local.RecentlyViewedProductDao
import com.example.shoppingbasket.recentlyviewed.data.local.RecentlyViewedProductEntity

// Room 없이 RecentlyViewedPersistence 를 검증하기 위한 인메모리 가짜 DAO
class FakeRecentlyViewedProductDao : RecentlyViewedProductDao {

    private var rows = emptyList<RecentlyViewedProductEntity>()

    override fun findAll(): List<RecentlyViewedProductEntity> = rows

    override fun replaceAll(products: List<RecentlyViewedProductEntity>) {
        clear()
        insertAll(products)
    }

    override fun clear() {
        rows = emptyList()
    }

    override fun insertAll(products: List<RecentlyViewedProductEntity>) {
        rows = rows + products
    }
}
