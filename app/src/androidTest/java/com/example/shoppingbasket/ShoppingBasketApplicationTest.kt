package com.example.shoppingbasket

import androidx.test.core.app.ApplicationProvider
import com.example.shoppingbasket.common.model.Money
import com.example.shoppingbasket.productlist.domain.model.Product
import com.example.shoppingbasket.productlist.domain.model.ProductId
import com.example.shoppingbasket.productlist.domain.model.ProductImageUrl
import com.example.shoppingbasket.productlist.domain.model.ProductName
import org.junit.Test

class ShoppingBasketApplicationTest {

    private val application = ApplicationProvider.getApplicationContext<ShoppingBasketApplication>()

    @Test
    fun sharesSingleCartRepository() {
        // 화면마다 저장소를 새로 만들면 장바구니가 화면 간에 공유되지 않는다
        assert(application.cartRepository === application.cartRepository)
    }

    @Test
    fun keepsAddedProductInSharedRepository() {
        // 이 테스트는 장바구니 저장소 공유 여부만 확인하므로 서버 응답을 기다릴 필요 없이 직접 상품을 만든다
        val product = Product(ProductId("test"), ProductName("테스트 상품"), Money(1_000), ProductImageUrl("https://example.com/image.png"))
        application.cartRepository.add(product)

        assert(application.cartRepository.findAll().items().isNotEmpty())

        application.cartRepository.remove(product.key())
    }

    @Test
    fun sharesSingleRecentlyViewedRepository() {
        // 화면마다 저장소를 새로 만들면 최근 본 상품이 화면 간에 공유되지 않는다
        assert(application.recentlyViewedRepository === application.recentlyViewedRepository)
    }

    @Test
    fun sharesSingleProductRepository() {
        // 상품 목록이 받아온 캐시를 상세 화면이 재사용하려면(4단계 결정 5번) 같은 인스턴스를 써야 한다
        assert(application.productRepository === application.productRepository)
    }
}
