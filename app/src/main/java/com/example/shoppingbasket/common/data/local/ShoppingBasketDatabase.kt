package com.example.shoppingbasket.common.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.shoppingbasket.basket.data.local.CartItemDao
import com.example.shoppingbasket.basket.data.local.CartItemEntity
import com.example.shoppingbasket.recentlyviewed.data.local.RecentlyViewedProductDao
import com.example.shoppingbasket.recentlyviewed.data.local.RecentlyViewedProductEntity

// 장바구니/최근 본 상품을 저장/복원하는 얇은 뒷단 전용 DB (4단계 결정 6, 10번)
@Database(
    entities = [CartItemEntity::class, RecentlyViewedProductEntity::class],
    version = 1,
    exportSchema = false
)
abstract class ShoppingBasketDatabase : RoomDatabase() {

    abstract fun cartItemDao(): CartItemDao

    abstract fun recentlyViewedProductDao(): RecentlyViewedProductDao

    companion object {
        fun create(context: Context): ShoppingBasketDatabase {
            return Room.databaseBuilder(context, ShoppingBasketDatabase::class.java, "shopping_basket.db")
                // ponytail: 앱 시작 시 1회 동기 읽기만 메인 스레드에서 허용한다 (코루틴 없이 가는 4단계 결정 1, 10번).
                // 지연이 체감될 만큼 데이터가 커지면 백그라운드 읽기 + 로딩 상태로 옮긴다.
                .allowMainThreadQueries()
                .build()
        }
    }
}
