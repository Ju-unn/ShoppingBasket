package com.example.shoppingbasket.productlist.domain.repository

import com.example.shoppingbasket.common.model.Page
import com.example.shoppingbasket.productlist.domain.model.Product
import com.example.shoppingbasket.productlist.domain.model.ProductList

// 상품 데이터 접근을 추상화하는 도메인 인터페이스. 구현체는 data.repository.ProductRepositoryImpl.
interface ProductRepository {

    fun fetchProducts(onFinished: () -> Unit)

    fun findAll(): ProductList

    fun firstPage(): Page

    fun findPage(page: Page): ProductList

    fun hasPageAfter(page: Page): Boolean

    fun findById(id: String): Product?
}
