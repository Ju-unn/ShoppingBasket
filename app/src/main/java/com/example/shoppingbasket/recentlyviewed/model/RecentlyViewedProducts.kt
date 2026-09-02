package com.example.shoppingbasket.recentlyviewed.model

import com.example.shoppingbasket.productlist.domain.model.Product

// 최근 본 상품 일급 컬렉션. 최신순으로 최대 MAX_SIZE개까지만 보관한다.
class RecentlyViewedProducts(private val products: List<Product>) {

    fun items(): List<Product> = products

    fun isEmpty(): Boolean = products.isEmpty()

    // 이미 본 상품이면 중복 추가하지 않고 맨 앞으로만 옮긴다 (4단계 결정 7번)
    fun viewed(product: Product): RecentlyViewedProducts {
        val reordered = listOf(product) + products.filterNot { existing -> existing.key() == product.key() }
        return RecentlyViewedProducts(reordered.take(MAX_SIZE))
    }

    fun latestExcluding(product: Product): Product? {
        return products.firstOrNull { existing -> existing.key() != product.key() }
    }

    private companion object {
        const val MAX_SIZE = 10
    }
}
