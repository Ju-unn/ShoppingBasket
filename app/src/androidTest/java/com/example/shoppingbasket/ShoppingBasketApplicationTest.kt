package com.example.shoppingbasket

import androidx.test.core.app.ApplicationProvider
import com.example.shoppingbasket.productlist.model.ProductRepository
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
        val product = ProductRepository().findAll().items().first()
        application.cartRepository.add(product)

        assert(application.cartRepository.findAll().items().isNotEmpty())

        application.cartRepository.remove(product.key())
    }
}
