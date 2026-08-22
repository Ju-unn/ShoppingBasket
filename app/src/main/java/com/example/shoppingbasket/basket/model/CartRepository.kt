package com.example.shoppingbasket.basket.model

import com.example.shoppingbasket.productlist.model.Product

// 백엔드 연동 전까지 인메모리로 장바구니 상태를 보관한다. 여러 화면의 ViewModel이 이 싱글턴을 공유해서 참조한다.
object CartRepository {

    private var cartItems = CartItems(emptyList())

    fun add(product: Product) {
        cartItems = cartItems.added(product)
    }

    fun remove(key: String) {
        cartItems = cartItems.removed(key)
    }

    fun findAll(): CartItems = cartItems
}
