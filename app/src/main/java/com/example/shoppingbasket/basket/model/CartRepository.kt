package com.example.shoppingbasket.basket.model

import com.example.shoppingbasket.common.model.Page
import com.example.shoppingbasket.productlist.model.Product

// 백엔드 연동 전까지 인메모리로 장바구니 상태를 보관한다.
// 여러 화면의 ViewModel 이 같은 인스턴스를 주입받아 장바구니를 공유한다.
// 페이지 크기는 밖으로 노출하지 않고 firstPage() 로 첫 페이지를 만들어 준다.
class CartRepository {

    private var cartItems = CartItems(emptyList())

    fun add(product: Product) {
        cartItems = cartItems.added(product)
    }

    fun remove(key: String) {
        cartItems = cartItems.removed(key)
    }

    fun findAll(): CartItems = cartItems

    fun firstPage(): Page = Page.first(PAGE_SIZE)

    fun findPage(page: Page): CartItems = findAll().sliceBy(page)

    fun hasPageAfter(page: Page): Boolean = findAll().hasPageAfter(page)

    fun coercePage(page: Page): Page = findAll().coercePage(page)

    private companion object {
        // 장바구니 한 페이지에 보여줄 상품 수 (2단계 기능 요구사항의 "5개 기준")
        const val PAGE_SIZE = 5
    }
}
