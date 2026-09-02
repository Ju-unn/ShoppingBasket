package com.example.shoppingbasket.productlist.domain.model

@JvmInline
value class ProductId(private val value: String) {

    init {
        require(value.isNotBlank()) { "상품 ID는 비어있을 수 없다" }
    }

    fun toKeyValue(): String = value
}
