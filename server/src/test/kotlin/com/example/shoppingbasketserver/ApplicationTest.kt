package com.example.shoppingbasketserver

import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation as ClientContentNegotiation
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {

    @Test
    fun productsEndpointReturnsFiftyMockProductsMatchingAndroidApp() = testApplication {
        application { module() }
        val client = createClient { install(ClientContentNegotiation) { json() } }

        val response = client.get("/products")

        assertEquals(HttpStatusCode.OK, response.status)
        val products: List<ProductResponse> = response.body()
        assertEquals(50, products.size)
        // 안드로이드 ProductRepository 의 p1 목업(PET보틀-밀크티병(300ml), 12,000원)과 동일해야 한다
        assertEquals(ProductResponse("p1", "PET보틀-밀크티병(300ml)", 12_000, "https://picsum.photos/seed/p1/600"), products.first())
    }
}
