package com.example.shoppingbasket.productlist.data.remote

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.GET

interface ProductApi {

    @GET("products")
    fun fetchProducts(): Call<List<ProductResponse>>

    companion object {
        // 에뮬레이터에서 호스트 PC의 로컬 Ktor 서버로 접근하는 주소 (4단계 결정 9번)
        private const val BASE_URL = "http://10.0.2.2:8080/"
        private val JSON = Json { ignoreUnknownKeys = true }

        fun create(): ProductApi {
            val converterFactory = JSON.asConverterFactory("application/json".toMediaType())
            val retrofit = Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(converterFactory).build()
            return retrofit.create(ProductApi::class.java)
        }
    }
}
