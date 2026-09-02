package com.example.shoppingbasket.productlist.domain.model

import com.example.shoppingbasket.common.fixture.testProduct
import com.example.shoppingbasket.common.model.Page
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class ProductListTest {

    @Test
    @DisplayName("담긴 상품 목록을 그대로 반환한다")
    fun items() {
        val product = testProduct()
        val productList = ProductList(listOf(product))

        assertThat(productList.items()).containsExactly(product)
    }

    @Test
    @DisplayName("빈 목록 여부를 판단한다")
    fun isEmpty() {
        assertThat(ProductList(emptyList()).isEmpty()).isTrue()
        assertThat(ProductList(listOf(testProduct())).isEmpty()).isFalse()
    }

    @Test
    @DisplayName("페이지 단위로 잘라낸 목록을 반환한다")
    fun sliceBy() {
        val productList = productListOf(5)

        val firstPage = productList.sliceBy(Page.first(PAGE_SIZE))

        assertThat(firstPage.items()).hasSize(2)
    }

    @Test
    @DisplayName("마지막 페이지가 덜 찼으면 남은 상품만 반환한다")
    fun sliceByPartialLastPage() {
        val productList = productListOf(5)

        val lastPage = productList.sliceBy(Page.first(PAGE_SIZE).next().next())

        assertThat(lastPage.items()).hasSize(1)
    }

    @Test
    @DisplayName("뒤에 남은 상품이 있으면 다음 페이지가 있다고 판단한다")
    fun hasPageAfter() {
        val productList = productListOf(5)

        assertThat(productList.hasPageAfter(Page.first(PAGE_SIZE))).isTrue()
    }

    @Test
    @DisplayName("마지막 페이지 뒤에는 다음 페이지가 없다")
    fun hasNoPageAfterLastPage() {
        val productList = productListOf(5)

        assertThat(productList.hasPageAfter(Page.first(PAGE_SIZE).next().next())).isFalse()
    }

    @Test
    @DisplayName("key로 상품을 찾고, 없으면 null을 반환한다")
    fun findByKey() {
        val productList = productListOf(5)

        assertThat(productList.findByKey("p3")?.key()).isEqualTo("p3")
        assertThat(productList.findByKey("없는id")).isNull()
    }

    private fun productListOf(count: Int): ProductList {
        return ProductList((1..count).map { number -> testProduct(id = "p$number") })
    }

    private companion object {
        // 경계를 짧은 목록으로 검증하기 위해 실제 화면(20개)보다 작은 크기를 쓴다
        const val PAGE_SIZE = 2
    }
}
