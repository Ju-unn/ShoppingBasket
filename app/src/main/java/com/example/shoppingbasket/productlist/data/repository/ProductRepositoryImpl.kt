package com.example.shoppingbasket.productlist.data.repository

import com.example.shoppingbasket.common.model.Page
import com.example.shoppingbasket.productlist.data.remote.ProductApi
import com.example.shoppingbasket.productlist.data.remote.ProductResponse
import com.example.shoppingbasket.productlist.data.remote.toProduct
import com.example.shoppingbasket.productlist.domain.model.Product
import com.example.shoppingbasket.productlist.domain.model.ProductList
import com.example.shoppingbasket.productlist.domain.repository.ProductRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// 상품 데이터의 출처가 로컬 목업에서 서버(GET /products)로 바뀌었다 (4단계 결정 4·5번).
// 코루틴을 아직 쓰지 않으므로 Call<T>.enqueue(callback) 방식으로 비동기 호출하고, 결과를 캐시해 둔다.
// 상세 화면은 이 캐시를 findById() 로 그대로 재사용하고 별도로 네트워크를 호출하지 않는다.
class ProductRepositoryImpl(private val productApi: ProductApi = ProductApi.create()) : ProductRepository {

    private var cachedProducts: ProductList = ProductList(emptyList())

    override fun fetchProducts(onLoaded: () -> Unit, onFailed: () -> Unit) {
        productApi.fetchProducts().enqueue(fetchCallback(onLoaded, onFailed))
    }

    override fun findAll(): ProductList = cachedProducts

    override fun firstPage(): Page = Page.first(PAGE_SIZE)

    override fun findPage(page: Page): ProductList = findAll().sliceBy(page)

    override fun hasPageAfter(page: Page): Boolean = findAll().hasPageAfter(page)

    override fun findById(id: String): Product? = findAll().findByKey(id)

    private fun fetchCallback(onLoaded: () -> Unit, onFailed: () -> Unit): Callback<List<ProductResponse>> {
        return object : Callback<List<ProductResponse>> {

            // HTTP 500 처럼 성공이 아닌 응답에는 본문이 없다. 연결 자체가 끊긴 것과 똑같이 실패로 다룬다 (5단계 확정 사항)
            override fun onResponse(call: Call<List<ProductResponse>>, response: Response<List<ProductResponse>>) {
                val products = loadedProducts(response) ?: return onFailed()
                cachedProducts = ProductList(products.map { product -> product.toProduct() })
                onLoaded()
            }

            // 캐시는 비우지 않고 그대로 둔 채 실패만 알린다. 화면이 안내와 재시도 버튼을 보여준다
            override fun onFailure(call: Call<List<ProductResponse>>, throwable: Throwable) {
                onFailed()
            }
        }
    }

    private fun loadedProducts(response: Response<List<ProductResponse>>): List<ProductResponse>? {
        if (!response.isSuccessful) {
            return null
        }
        return response.body()
    }

    private companion object {
        // 더보기 한 번에 불러올 상품 수 (2단계 기능 요구사항의 "20개 기준")
        const val PAGE_SIZE = 20
    }
}
