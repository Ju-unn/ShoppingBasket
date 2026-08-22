package com.example.shoppingbasket.basket.model

import com.example.shoppingbasket.common.model.Money
import com.example.shoppingbasket.common.model.Page
import com.example.shoppingbasket.common.model.Quantity
import com.example.shoppingbasket.productlist.model.Product

class CartItems(private val items: List<CartItem>) {

    fun items(): List<CartItem> = items

    fun isEmpty(): Boolean = items.isEmpty()

    fun sliceBy(page: Page): CartItems = CartItems(page.sliceOf(items))

    fun hasPageAfter(page: Page): Boolean = page.hasNextIn(items.size)

    // 상품을 지워 현재 페이지가 비어버린 경우, 내용이 남아 있는 마지막 페이지로 당긴다
    fun coercePage(page: Page): Page = page.coercedIn(items.size)

    fun totalPrice(): Money = items.fold(Money(0)) { total, item -> item.accumulate(total) }

    fun added(product: Product, quantity: Quantity = Quantity.ONE): CartItems {
        val matched = items.find { item -> item.isSameProduct(product) }
        if (matched != null) {
            return replaceMatching(matched.key()) { item -> item.increasedBy(quantity) }
        }
        return CartItems(items + CartItem(product, quantity))
    }

    fun removed(key: String): CartItems {
        return CartItems(items.filterNot { item -> item.key() == key })
    }

    fun increase(key: String): CartItems = replaceMatching(key) { item -> item.increasedBy(Quantity.ONE) }

    // 수량 1에서 감소를 누르면 장바구니에서 제거한다 (3단계 확정 사항)
    fun decrease(key: String): CartItems {
        val matched = items.find { item -> item.key() == key } ?: return this
        if (matched.isMinimumQuantity()) {
            return removed(key)
        }
        return replaceMatching(key) { item -> item.decreased() }
    }

    fun quantityOf(product: Product): Quantity? {
        return items.find { item -> item.isSameProduct(product) }?.currentQuantity()
    }

    fun totalQuantityDisplayValue(): String {
        if (isEmpty()) {
            return "0"
        }
        return items.map { item -> item.currentQuantity() }
            .reduce { total, quantity -> total.increasedBy(quantity) }
            .toDisplayValue()
    }

    private fun replaceMatching(key: String, transform: (CartItem) -> CartItem): CartItems {
        val updated = items.map { item -> replaceIfMatched(item, key, transform) }
        return CartItems(updated)
    }

    private fun replaceIfMatched(item: CartItem, key: String, transform: (CartItem) -> CartItem): CartItem {
        if (item.key() != key) {
            return item
        }
        return transform(item)
    }
}
