package com.example.shoppingbasket.productlist.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class ProductRepositoryTest {

    private val repository = ProductRepository()

    @Test
    @DisplayName("목업 상품 50개를 반환한다")
    fun findAll() {
        val products = repository.findAll()

        assertThat(products.isEmpty()).isFalse()
        assertThat(products.items()).hasSize(50)
    }

    @Test
    @DisplayName("id로 상품을 찾는다")
    fun findById() {
        assertThat(repository.findById("p1")?.key()).isEqualTo("p1")
        assertThat(repository.findById("없는id")).isNull()
    }

    @Test
    @DisplayName("첫 페이지는 상품 20개를 반환한다")
    fun findFirstPage() {
        val firstPage = repository.findPage(repository.firstPage())

        assertThat(firstPage.items()).hasSize(20)
    }

    @Test
    @DisplayName("다음 페이지는 이어지는 상품 20개를 반환한다")
    fun findNextPage() {
        val secondPage = repository.findPage(repository.firstPage().next())

        assertThat(secondPage.items()).hasSize(20)
        assertThat(secondPage.findByKey("p21")).isNotNull()
    }

    @Test
    @DisplayName("마지막 페이지는 남은 상품 10개만 반환한다")
    fun findLastPage() {
        val lastPage = repository.findPage(repository.firstPage().next().next())

        assertThat(lastPage.items()).hasSize(10)
        assertThat(lastPage.findByKey("p50")).isNotNull()
    }

    @Test
    @DisplayName("마지막 페이지 전까지는 다음 페이지가 있다")
    fun hasPageAfter() {
        assertThat(repository.hasPageAfter(repository.firstPage())).isTrue()
        assertThat(repository.hasPageAfter(repository.firstPage().next())).isTrue()
    }

    @Test
    @DisplayName("마지막 페이지에는 다음 페이지가 없다")
    fun hasNoPageAfterLastPage() {
        val lastPage = repository.firstPage().next().next()

        assertThat(repository.hasPageAfter(lastPage)).isFalse()
    }

    @Test
    @DisplayName("모든 목업 상품의 key는 서로 겹치지 않는다")
    fun mockProductKeysAreUnique() {
        val keys = repository.findAll().items().map { product -> product.key() }

        assertThat(keys).doesNotHaveDuplicates()
    }
}
