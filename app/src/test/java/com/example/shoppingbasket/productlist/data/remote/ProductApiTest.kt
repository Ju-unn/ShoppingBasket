package com.example.shoppingbasket.productlist.data.remote

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

// FakeProductApi 는 실제 네트워크를 타지 않아 HTTP·JSON 역직렬화 경로를 검증하지 못한다 (4단계 브랜치 5 한계).
// 이 테스트는 MockWebServer 로 실제 Retrofit/OkHttp/kotlinx.serialization 경로를 확인한다.
class ProductApiTest {

    private val server = MockWebServer()

    @BeforeEach
    fun startServer() = server.start()

    @AfterEach
    fun stopServer() = server.shutdown()

    @Test
    @DisplayName("서버 JSON 응답을 ProductResponse 목록으로 파싱한다")
    fun parsesProductListResponse() {
        server.enqueue(MockResponse().setBody(PRODUCTS_JSON))
        val api = ProductApi.create(server.url("/").toString())

        val response = api.fetchProducts().execute()

        assertThat(response.body()).containsExactly(
            ProductResponse("p1", "PET보틀-정사각(500ml)", 10_000, "https://picsum.photos/seed/p1/600")
        )
    }

    @Test
    @DisplayName("GET /products 경로로 요청한다")
    fun requestsProductsPath() {
        server.enqueue(MockResponse().setBody(PRODUCTS_JSON))
        val api = ProductApi.create(server.url("/").toString())

        api.fetchProducts().execute()

        val request = server.takeRequest()
        assertThat(request.method).isEqualTo("GET")
        assertThat(request.path).isEqualTo("/products")
    }

    @Test
    @DisplayName("서버 오류 응답은 실패로 처리되고 body가 비어 있다")
    fun serverErrorYieldsUnsuccessfulResponse() {
        server.enqueue(MockResponse().setResponseCode(500))
        val api = ProductApi.create(server.url("/").toString())

        val response = api.fetchProducts().execute()

        assertThat(response.isSuccessful).isFalse()
        assertThat(response.body()).isNull()
    }

    private companion object {
        const val PRODUCTS_JSON =
            """[{"id":"p1","name":"PET보틀-정사각(500ml)","price":10000,"imageUrl":"https://picsum.photos/seed/p1/600"}]"""
    }
}
