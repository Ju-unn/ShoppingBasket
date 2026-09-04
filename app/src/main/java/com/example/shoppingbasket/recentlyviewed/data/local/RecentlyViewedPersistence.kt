package com.example.shoppingbasket.recentlyviewed.data.local

import com.example.shoppingbasket.common.model.Money
import com.example.shoppingbasket.productlist.domain.model.Product
import com.example.shoppingbasket.productlist.domain.model.ProductId
import com.example.shoppingbasket.productlist.domain.model.ProductImageUrl
import com.example.shoppingbasket.productlist.domain.model.ProductName
import com.example.shoppingbasket.recentlyviewed.model.RecentlyViewedProducts
import java.util.concurrent.Executor
import java.util.concurrent.Executors

// RecentlyViewedRepository 를 Room 에 저장/복원하는 얇은 뒷단 (4단계 결정 10번).
// RecentlyViewedRepository 자체는 건드리지 않고, 변경이 생기면 백그라운드로 fire-and-forget 저장만 한다.
class RecentlyViewedPersistence(
    private val recentlyViewedProductDao: RecentlyViewedProductDao,
    private val writeExecutor: Executor = Executors.newSingleThreadExecutor()
) {

    // 앱 시작 시 1회, 호출한 스레드에서 동기적으로 읽는다 (결정 10번)
    fun restore(): RecentlyViewedProducts {
        return RecentlyViewedProducts(recentlyViewedProductDao.findAll().map { entity -> entity.toProduct() })
    }

    fun save(products: RecentlyViewedProducts) {
        val entities = products.items().mapIndexed { index, product -> product.toEntity(index) }
        writeExecutor.execute { recentlyViewedProductDao.replaceAll(entities) }
    }
}

private fun Product.toEntity(sortOrder: Int): RecentlyViewedProductEntity {
    return RecentlyViewedProductEntity(
        productId = key(),
        productName = displayName(),
        productPrice = unitPrice().toRawValue(),
        productImageUrl = displayImageUrl(),
        sortOrder = sortOrder
    )
}

private fun RecentlyViewedProductEntity.toProduct(): Product {
    return Product(ProductId(productId), ProductName(productName), Money(productPrice), ProductImageUrl(productImageUrl))
}
