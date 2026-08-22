package com.example.shoppingbasket.basket.model

import com.example.shoppingbasket.common.model.Money
import com.example.shoppingbasket.common.model.Quantity
import com.example.shoppingbasket.productlist.model.Product

class CartItems(private val items: List<CartItem>) {

    fun items(): List<CartItem> = items

    fun isEmpty(): Boolean = items.isEmpty()

    fun totalPrice(): Money = items.fold(Money(0)) { total, item -> item.accumulate(total) }

    fun added(product: Product, quantity: Quantity = Quantity.ONE): CartItems {
        val matched = items.find { item -> item.isSameProduct(product) }
        if (matched != null) {
            return increasedItem(matched, quantity)
        }
        return CartItems(items + CartItem(product, quantity))
    }

    fun removed(key: String): CartItems {
        return CartItems(items.filterNot { item -> item.key() == key })
    }

    private fun increasedItem(matched: CartItem, quantity: Quantity): CartItems {
        val updated = items.map { item -> replaceIfMatched(item, matched, quantity) }
        return CartItems(updated)
    }

    private fun replaceIfMatched(item: CartItem, matched: CartItem, quantity: Quantity): CartItem {
        if (item.key() != matched.key()) {
            return item
        }
        return item.increasedBy(quantity)
    }
}
