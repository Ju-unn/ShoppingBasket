package com.example.shoppingbasket.productlist.model

import com.example.shoppingbasket.common.model.Money

// 아직 백엔드가 없어 목업 데이터를 반환한다. 실제 데이터 소스가 준비되면 이 클래스만 교체한다.
class ProductRepository {

    fun findAll(): ProductList = ProductList(MOCK_PRODUCTS)

    private companion object {
        val MOCK_PRODUCTS = listOf(
            Product(ProductId("p1"), ProductName("PET보틀-정사각(500ml)"), Money(10_000)),
            Product(ProductId("p2"), ProductName("PET보틀-밀크티병(500ml)"), Money(12_000)),
            Product(ProductId("p3"), ProductName("PET보틀-정사각(300ml)"), Money(10_000)),
            Product(ProductId("p4"), ProductName("PET보틀-납작(2000ml)"), Money(12_000)),
            Product(ProductId("p5"), ProductName("PET보틀-단지(4000ml)"), Money(10_000)),
            Product(ProductId("p6"), ProductName("PET보틀-단지(2000ml)"), Money(12_000))
        )
    }
}
