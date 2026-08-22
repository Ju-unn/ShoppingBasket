package com.example.shoppingbasket.productlist.model

import com.example.shoppingbasket.common.model.Page

class ProductList(private val products: List<Product>) {

    fun items(): List<Product> = products

    fun isEmpty(): Boolean = products.isEmpty()

    fun sliceBy(page: Page): ProductList = ProductList(page.sliceOf(products))

    fun hasPageAfter(page: Page): Boolean = page.hasNextIn(products.size)

    fun findByKey(key: String): Product? = products.find { product -> product.key() == key }
}
