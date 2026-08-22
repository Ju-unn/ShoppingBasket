package com.example.shoppingbasket.common.fixture

import com.example.shoppingbasket.common.model.Money
import com.example.shoppingbasket.productlist.model.Product
import com.example.shoppingbasket.productlist.model.ProductId
import com.example.shoppingbasket.productlist.model.ProductImageUrl
import com.example.shoppingbasket.productlist.model.ProductName

/**
 * test / androidTest에서 공통으로 사용하는 테스트용 상품 픽스처
 */
// 테스트용 상품 생성
fun testProduct(id: String = "p1", priceWon: Int = 10_000): Product = Product(
    ProductId(id),
    ProductName("상품-$id"),
    Money(priceWon),
    ProductImageUrl("https://picsum.photos/seed/$id/600")
)
