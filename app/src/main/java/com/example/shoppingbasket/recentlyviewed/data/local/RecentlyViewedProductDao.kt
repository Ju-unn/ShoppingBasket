package com.example.shoppingbasket.recentlyviewed.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

// 코루틴을 쓰지 않으므로 전부 동기 메서드다 (4단계 결정 1, 10번)
@Dao
interface RecentlyViewedProductDao {

    @Query("SELECT * FROM recently_viewed_products ORDER BY sortOrder ASC")
    fun findAll(): List<RecentlyViewedProductEntity>

    // 매번 전체를 지우고 다시 넣어 최신 상태와 완전히 동기화한다 (fire-and-forget 저장용)
    @Transaction
    fun replaceAll(products: List<RecentlyViewedProductEntity>) {
        clear()
        insertAll(products)
    }

    @Query("DELETE FROM recently_viewed_products")
    fun clear()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(products: List<RecentlyViewedProductEntity>)
}
