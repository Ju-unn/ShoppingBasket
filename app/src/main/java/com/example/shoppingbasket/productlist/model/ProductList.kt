package com.example.shoppingbasket.productlist.model

class ProductList(private val products: List<Product>) {

    fun items(): List<Product> = products

    fun isEmpty(): Boolean = products.isEmpty()
}
