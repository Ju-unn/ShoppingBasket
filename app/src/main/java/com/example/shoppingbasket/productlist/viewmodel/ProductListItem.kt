package com.example.shoppingbasket.productlist.viewmodel

import com.example.shoppingbasket.common.model.Quantity
import com.example.shoppingbasket.productlist.domain.model.Product

// 목록 화면 렌더링용 묶음. cartQuantity 가 null 이면 아직 장바구니에 없다는 뜻이다.
data class ProductListItem(private val product: Product, private val cartQuantity: Quantity?) {

    fun key(): String = product.key()

    fun displayName(): String = product.displayName()

    fun displayPrice(): String = product.displayPrice()

    fun displayImageUrl(): String = product.displayImageUrl()

    fun isInCart(): Boolean = cartQuantity != null

    fun cartQuantityDisplayValue(): String = cartQuantity?.toDisplayValue().orEmpty()
}
