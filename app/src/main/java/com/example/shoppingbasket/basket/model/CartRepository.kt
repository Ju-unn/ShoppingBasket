package com.example.shoppingbasket.basket.model

import com.example.shoppingbasket.common.model.Page
import com.example.shoppingbasket.common.model.Quantity
import com.example.shoppingbasket.productlist.model.Product

// 백엔드 연동 전까지 인메모리로 장바구니 상태를 보관한다.
// 여러 화면의 ViewModel 이 같은 인스턴스를 주입받아 장바구니를 공유한다.
// 페이지 크기는 밖으로 노출하지 않고 firstPage() 로 첫 페이지를 만들어 준다.
class CartRepository {

    private var cartItems = CartItems(emptyList())
    private val changeListeners = mutableListOf<() -> Unit>()

    fun add(product: Product, quantity: Quantity = Quantity.ONE) {
        cartItems = cartItems.added(product, quantity)
        notifyChanged()
    }

    fun remove(key: String) {
        cartItems = cartItems.removed(key)
        notifyChanged()
    }

    // 목록/상세 화면에서 온 수량 증가 요청. 코루틴 도입 전이라 리스너로 변경을 알린다 (3단계 확정 사항)
    fun increase(key: String) {
        cartItems = cartItems.increase(key)
        notifyChanged()
    }

    fun decrease(key: String) {
        cartItems = cartItems.decrease(key)
        notifyChanged()
    }

    fun quantityOf(product: Product): Quantity? = cartItems.quantityOf(product)

    fun findAll(): CartItems = cartItems

    fun firstPage(): Page = Page.first(PAGE_SIZE)

    fun findPage(page: Page): CartItems = findAll().sliceBy(page)

    fun hasPageAfter(page: Page): Boolean = findAll().hasPageAfter(page)

    fun coercePage(page: Page): Page = findAll().coercePage(page)

    fun addChangeListener(listener: () -> Unit) {
        changeListeners.add(listener)
    }

    fun removeChangeListener(listener: () -> Unit) {
        changeListeners.remove(listener)
    }

    private fun notifyChanged() {
        changeListeners.forEach { listener -> listener() }
    }

    private companion object {
        // 장바구니 한 페이지에 보여줄 상품 수 (2단계 기능 요구사항의 "5개 기준")
        const val PAGE_SIZE = 5
    }
}
