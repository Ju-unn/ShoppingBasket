package com.example.shoppingbasket.productlist.model

import com.example.shoppingbasket.common.fixture.FakeProductApi
import com.example.shoppingbasket.common.fixture.fakeProductResponses
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class ProductRepositoryTest {

    private val repository = ProductRepository(FakeProductApi(fakeProductResponses()))

    @Test
    @DisplayName("fetch 하기 전에는 상품이 없다")
    fun startsEmptyBeforeFetch() {
        assertThat(repository.findAll().isEmpty()).isTrue()
    }

    @Test
    @DisplayName("fetch 하면 서버 응답 50개를 반환한다")
    fun findAllAfterFetch() {
        repository.fetchProducts {}

        assertThat(repository.findAll().items()).hasSize(50)
    }

    @Test
    @DisplayName("fetch 완료 콜백이 정확히 한 번 호출된다")
    fun fetchProductsInvokesCallbackOnce() {
        var callCount = 0

        repository.fetchProducts { callCount += 1 }

        assertThat(callCount).isEqualTo(1)
    }

    @Test
    @DisplayName("id로 상품을 찾는다")
    fun findById() {
        repository.fetchProducts {}

        assertThat(repository.findById("p1")?.key()).isEqualTo("p1")
        assertThat(repository.findById("없는id")).isNull()
    }

    @Test
    @DisplayName("첫 페이지는 상품 20개를 반환한다")
    fun findFirstPage() {
        repository.fetchProducts {}

        assertThat(repository.findPage(repository.firstPage()).items()).hasSize(20)
    }

    @Test
    @DisplayName("다음 페이지는 이어지는 상품 20개를 반환한다")
    fun findNextPage() {
        repository.fetchProducts {}

        val secondPage = repository.findPage(repository.firstPage().next())

        assertThat(secondPage.items()).hasSize(20)
        assertThat(secondPage.findByKey("p21")).isNotNull()
    }

    @Test
    @DisplayName("마지막 페이지는 남은 상품 10개만 반환한다")
    fun findLastPage() {
        repository.fetchProducts {}

        val lastPage = repository.findPage(repository.firstPage().next().next())

        assertThat(lastPage.items()).hasSize(10)
        assertThat(lastPage.findByKey("p50")).isNotNull()
    }

    @Test
    @DisplayName("마지막 페이지 전까지는 다음 페이지가 있다")
    fun hasPageAfter() {
        repository.fetchProducts {}

        assertThat(repository.hasPageAfter(repository.firstPage())).isTrue()
        assertThat(repository.hasPageAfter(repository.firstPage().next())).isTrue()
    }

    @Test
    @DisplayName("마지막 페이지에는 다음 페이지가 없다")
    fun hasNoPageAfterLastPage() {
        repository.fetchProducts {}

        val lastPage = repository.firstPage().next().next()

        assertThat(repository.hasPageAfter(lastPage)).isFalse()
    }

    @Test
    @DisplayName("응답에 담긴 모든 상품의 key는 서로 겹치지 않는다")
    fun productKeysAreUnique() {
        repository.fetchProducts {}

        val keys = repository.findAll().items().map { product -> product.key() }

        assertThat(keys).doesNotHaveDuplicates()
    }
}
