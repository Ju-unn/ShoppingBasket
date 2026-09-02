package com.example.shoppingbasket.common.fixture

import com.example.shoppingbasket.common.model.Money
import com.example.shoppingbasket.productlist.domain.model.Product
import com.example.shoppingbasket.productlist.domain.model.ProductId
import com.example.shoppingbasket.productlist.domain.model.ProductImageUrl
import com.example.shoppingbasket.productlist.domain.model.ProductName

/**
 * JVM 단위 테스트(src/test)에서 사용하는 테스트용 상품 픽스처.
 *
 * androidTest 는 별도 소스셋이라 이 파일을 참조할 수 없다.
 * 계측 테스트는 각자 Product(...) 를 직접 만들어 쓴다.
 */
// 테스트용 상품 생성
fun testProduct(id: String = "p1", priceWon: Int = 10_000): Product = Product(
    ProductId(id),
    ProductName("상품-$id"),
    Money(priceWon),
    ProductImageUrl("https://picsum.photos/seed/$id/600")
)
