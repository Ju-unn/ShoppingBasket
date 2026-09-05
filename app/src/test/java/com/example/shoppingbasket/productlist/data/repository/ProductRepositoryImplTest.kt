package com.example.shoppingbasket.productlist.data.repository

import com.example.shoppingbasket.common.fixture.FailingProductApi
import com.example.shoppingbasket.common.fixture.FakeProductApi
import com.example.shoppingbasket.common.fixture.ManualProductApi
import com.example.shoppingbasket.common.fixture.ServerErrorProductApi
import com.example.shoppingbasket.common.fixture.fakeProductResponses
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class ProductRepositoryImplTest {

    private val repository = ProductRepositoryImpl(FakeProductApi(fakeProductResponses()))

    @Test
    @DisplayName("fetch 하기 전에는 상품이 없다")
    fun startsEmptyBeforeFetch() {
        assertThat(repository.findAll().isEmpty()).isTrue()
    }

    @Test
    @DisplayName("fetch 하면 서버 응답 50개를 반환한다")
    fun findAllAfterFetch() {
        repository.fetchProducts(onLoaded = {}, onFailed = {})

        assertThat(repository.findAll().items()).hasSize(50)
    }

    @Test
    @DisplayName("fetch 성공 콜백이 정확히 한 번 호출된다")
    fun fetchProductsInvokesLoadedCallbackOnce() {
        var loadedCount = 0

        repository.fetchProducts(onLoaded = { loadedCount += 1 }, onFailed = {})

        assertThat(loadedCount).isEqualTo(1)
    }

    @Test
    @DisplayName("서버에 연결하지 못하면 실패 콜백이 호출된다")
    fun fetchProductsInvokesFailedCallbackOnNetworkFailure() {
        var failedCount = 0
        val failingRepository = ProductRepositoryImpl(FailingProductApi())

        failingRepository.fetchProducts(onLoaded = {}, onFailed = { failedCount += 1 })

        assertThat(failedCount).isEqualTo(1)
    }

    @Test
    @DisplayName("서버에 연결하지 못해도 성공 콜백은 호출되지 않는다")
    fun fetchProductsDoesNotInvokeLoadedCallbackOnNetworkFailure() {
        var loadedCount = 0
        val failingRepository = ProductRepositoryImpl(FailingProductApi())

        failingRepository.fetchProducts(onLoaded = { loadedCount += 1 }, onFailed = {})

        assertThat(loadedCount).isZero()
    }

    @Test
    @DisplayName("HTTP 500 응답도 성공이 아니라 실패로 다룬다")
    fun fetchProductsTreatsServerErrorAsFailure() {
        var failedCount = 0
        var loadedCount = 0
        val errorRepository = ProductRepositoryImpl(ServerErrorProductApi())

        errorRepository.fetchProducts(onLoaded = { loadedCount += 1 }, onFailed = { failedCount += 1 })

        assertThat(failedCount).isEqualTo(1)
        assertThat(loadedCount).isZero()
    }

    @Test
    @DisplayName("실패해도 이미 받아둔 캐시는 지우지 않는다")
    fun keepsCachedProductsOnFailure() {
        val manualApi = ManualProductApi()
        val cachingRepository = ProductRepositoryImpl(manualApi)
        cachingRepository.fetchProducts(onLoaded = {}, onFailed = {})
        manualApi.completeWith(fakeProductResponses())

        cachingRepository.fetchProducts(onLoaded = {}, onFailed = {})
        manualApi.failWith()

        assertThat(cachingRepository.findAll().items()).hasSize(50)
    }

    @Test
    @DisplayName("id로 상품을 찾는다")
    fun findById() {
        repository.fetchProducts(onLoaded = {}, onFailed = {})

        assertThat(repository.findById("p1")?.key()).isEqualTo("p1")
        assertThat(repository.findById("없는id")).isNull()
    }

    @Test
    @DisplayName("첫 페이지는 상품 20개를 반환한다")
    fun findFirstPage() {
        repository.fetchProducts(onLoaded = {}, onFailed = {})

        assertThat(repository.findPage(repository.firstPage()).items()).hasSize(20)
    }

    @Test
    @DisplayName("다음 페이지는 이어지는 상품 20개를 반환한다")
    fun findNextPage() {
        repository.fetchProducts(onLoaded = {}, onFailed = {})

        val secondPage = repository.findPage(repository.firstPage().next())

        assertThat(secondPage.items()).hasSize(20)
        assertThat(secondPage.findByKey("p21")).isNotNull()
    }

    @Test
    @DisplayName("마지막 페이지는 남은 상품 10개만 반환한다")
    fun findLastPage() {
        repository.fetchProducts(onLoaded = {}, onFailed = {})

        val lastPage = repository.findPage(repository.firstPage().next().next())

        assertThat(lastPage.items()).hasSize(10)
        assertThat(lastPage.findByKey("p50")).isNotNull()
    }

    @Test
    @DisplayName("마지막 페이지 전까지는 다음 페이지가 있다")
    fun hasPageAfter() {
        repository.fetchProducts(onLoaded = {}, onFailed = {})

        assertThat(repository.hasPageAfter(repository.firstPage())).isTrue()
        assertThat(repository.hasPageAfter(repository.firstPage().next())).isTrue()
    }

    @Test
    @DisplayName("마지막 페이지에는 다음 페이지가 없다")
    fun hasNoPageAfterLastPage() {
        repository.fetchProducts(onLoaded = {}, onFailed = {})

        val lastPage = repository.firstPage().next().next()

        assertThat(repository.hasPageAfter(lastPage)).isFalse()
    }

    @Test
    @DisplayName("응답에 담긴 모든 상품의 key는 서로 겹치지 않는다")
    fun productKeysAreUnique() {
        repository.fetchProducts(onLoaded = {}, onFailed = {})

        val keys = repository.findAll().items().map { product -> product.key() }

        assertThat(keys).doesNotHaveDuplicates()
    }
}
