package com.example.shoppingbasket.recentlyviewed.model

import com.example.shoppingbasket.productlist.model.Product

// 백엔드/Room 연동 전까지 인메모리로 최근 본 상품을 보관한다 (CartRepository 와 같은 구조).
// 화면 간에는 변경 리스너로 동기화한다 (코루틴 도입 전까지의 3단계 확정 방식).
class RecentlyViewedRepository {

    private var recentlyViewed = RecentlyViewedProducts(emptyList())
    private val changeListeners = mutableListOf<() -> Unit>()

    fun recordViewed(product: Product) {
        recentlyViewed = recentlyViewed.viewed(product)
        notifyChanged()
    }

    fun findAll(): RecentlyViewedProducts = recentlyViewed

    fun latestExcluding(product: Product): Product? = recentlyViewed.latestExcluding(product)

    fun addChangeListener(listener: () -> Unit) {
        changeListeners.add(listener)
    }

    fun removeChangeListener(listener: () -> Unit) {
        changeListeners.remove(listener)
    }

    private fun notifyChanged() {
        changeListeners.forEach { listener -> listener() }
    }
}
