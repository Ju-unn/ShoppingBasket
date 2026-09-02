package com.example.shoppingbasket.productlist.model

import com.example.shoppingbasket.common.model.Page
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// 상품 데이터의 출처가 로컬 목업에서 서버(GET /products)로 바뀌었다 (4단계 결정 4·5번).
// 코루틴을 아직 쓰지 않으므로 Call<T>.enqueue(callback) 방식으로 비동기 호출하고, 결과를 캐시해 둔다.
// 상세 화면은 이 캐시를 findById() 로 그대로 재사용하고 별도로 네트워크를 호출하지 않는다.
class ProductRepository(private val productApi: ProductApi = ProductApi.create()) {

    private var cachedProducts: ProductList = ProductList(emptyList())

    fun fetchProducts(onFinished: () -> Unit) {
        productApi.fetchProducts().enqueue(fetchCallback(onFinished))
    }

    fun findAll(): ProductList = cachedProducts

    fun firstPage(): Page = Page.first(PAGE_SIZE)

    fun findPage(page: Page): ProductList = findAll().sliceBy(page)

    fun hasPageAfter(page: Page): Boolean = findAll().hasPageAfter(page)

    fun findById(id: String): Product? = findAll().findByKey(id)

    private fun fetchCallback(onFinished: () -> Unit): Callback<List<ProductResponse>> {
        return object : Callback<List<ProductResponse>> {

            override fun onResponse(call: Call<List<ProductResponse>>, response: Response<List<ProductResponse>>) {
                response.body()?.let { products -> cachedProducts = ProductList(products.map { product -> product.toProduct() }) }
                onFinished()
            }

            // 실패 시 캐시를 비워두지 않고 그대로 유지한 채 로딩만 끝낸다 (에러 UI 없음, 4단계 5번 브랜치 확정 사항)
            override fun onFailure(call: Call<List<ProductResponse>>, throwable: Throwable) {
                onFinished()
            }
        }
    }

    private companion object {
        // 더보기 한 번에 불러올 상품 수 (2단계 기능 요구사항의 "20개 기준")
        const val PAGE_SIZE = 20
    }
}
