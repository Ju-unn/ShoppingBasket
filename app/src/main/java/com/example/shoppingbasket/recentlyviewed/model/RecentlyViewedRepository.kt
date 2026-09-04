package com.example.shoppingbasket.recentlyviewed.model

import com.example.shoppingbasket.productlist.domain.model.Product

// 백엔드/Room 연동 전까지 인메모리로 최근 본 상품을 보관한다 (CartRepository 와 같은 구조).
// 화면 간에는 변경 리스너로 동기화한다 (코루틴 도입 전까지의 3단계 확정 방식).
class RecentlyViewedRepository {

    private var recentlyViewed = RecentlyViewedProducts(emptyList())
    private val changeListeners = mutableListOf<() -> Unit>()

    // Room 에서 복원한 상태를 초기 값으로 채운다 (4단계 결정 10번). 이미 채워진 상태를 다시 저장할 필요는 없어 알리지 않는다
    fun restore(products: RecentlyViewedProducts) {
        recentlyViewed = products
    }

    fun recordViewed(product: Product) {
        recentlyViewed = recentlyViewed.viewed(product)
        notifyChanged()
    }

    fun findAll(): RecentlyViewedProducts = recentlyViewed

    fun latestExcluding(product: Product): Product? = recentlyViewed.latestExcluding(product)

    // 계측 테스트마다 Application 수명 저장소를 초기화하기 위한 용도 (CartRepository 의 remove 와 같은 목적)
    fun clear() {
        recentlyViewed = RecentlyViewedProducts(emptyList())
        notifyChanged()
    }

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
