package com.example.shoppingbasket.basket.data.local

import com.example.shoppingbasket.basket.model.CartItem
import com.example.shoppingbasket.basket.model.CartItems
import com.example.shoppingbasket.common.model.Money
import com.example.shoppingbasket.common.model.Quantity
import com.example.shoppingbasket.productlist.domain.model.Product
import com.example.shoppingbasket.productlist.domain.model.ProductId
import com.example.shoppingbasket.productlist.domain.model.ProductImageUrl
import com.example.shoppingbasket.productlist.domain.model.ProductName
import java.util.concurrent.Executor
import java.util.concurrent.Executors

// CartRepository 를 Room 에 저장/복원하는 얇은 뒷단 (4단계 결정 10번).
// CartRepository 자체는 건드리지 않고, 변경이 생기면 백그라운드로 fire-and-forget 저장만 한다.
class CartPersistence(
    private val cartItemDao: CartItemDao,
    private val writeExecutor: Executor = Executors.newSingleThreadExecutor()
) {

    // 앱 시작 시 1회, 호출한 스레드에서 동기적으로 읽는다 (결정 10번)
    fun restore(): CartItems {
        return CartItems(cartItemDao.findAll().map { entity -> entity.toCartItem() })
    }

    fun save(cartItems: CartItems) {
        val entities = cartItems.items().mapIndexed { index, item -> item.toEntity(index) }
        writeExecutor.execute { cartItemDao.replaceAll(entities) }
    }
}

private fun CartItem.toEntity(sortOrder: Int): CartItemEntity {
    return CartItemEntity(
        productId = key(),
        productName = displayName(),
        productPrice = unitPrice().toRawValue(),
        productImageUrl = displayImageUrl(),
        quantity = currentQuantity().toRawValue(),
        sortOrder = sortOrder
    )
}

private fun CartItemEntity.toCartItem(): CartItem {
    val product = Product(ProductId(productId), ProductName(productName), Money(productPrice), ProductImageUrl(productImageUrl))
    return CartItem(product, Quantity(quantity))
}
