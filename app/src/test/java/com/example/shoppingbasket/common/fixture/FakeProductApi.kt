package com.example.shoppingbasket.common.fixture

import com.example.shoppingbasket.productlist.model.ProductApi
import com.example.shoppingbasket.productlist.model.ProductRepository
import com.example.shoppingbasket.productlist.model.ProductResponse
import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * JVM 단위 테스트(src/test)에서 실제 네트워크 없이 ProductRepository 를 검증하기 위한 가짜 상품 API.
 *
 * androidTest 는 별도 소스셋이라 이 파일을 참조할 수 없다.
 */
// enqueue 호출 즉시 콜백을 실행해 fetchProducts 가 사실상 동기처럼 동작한다
class FakeProductApi(private val responses: List<ProductResponse> = fakeProductResponses()) : ProductApi {

    override fun fetchProducts(): Call<List<ProductResponse>> = ImmediateCall(responses)
}

// 응답이 도착해도 fetchProducts 호출 시점에는 아무 일도 하지 않고, completeWith() 를 불러야 콜백이 실행되는 가짜 API.
// 로딩 상태가 응답 전/후로 바뀌는지 검증할 때 쓴다.
class ManualProductApi : ProductApi {

    private var pendingCallback: Callback<List<ProductResponse>>? = null

    override fun fetchProducts(): Call<List<ProductResponse>> {
        return ManualCall { callback -> pendingCallback = callback }
    }

    fun completeWith(responses: List<ProductResponse>) {
        pendingCallback?.onResponse(ImmediateCall(responses), Response.success(responses))
    }
}

fun fakeProductResponses(count: Int = 50): List<ProductResponse> {
    return (1..count).map { number -> ProductResponse("p$number", "상품-$number", 10_000, "https://picsum.photos/seed/p$number/600") }
}

fun fakeProductRepository(count: Int = 50): ProductRepository {
    val repository = ProductRepository(FakeProductApi(fakeProductResponses(count)))
    repository.fetchProducts {}
    return repository
}

private class ManualCall(
    private val onEnqueue: (Callback<List<ProductResponse>>) -> Unit
) : Call<List<ProductResponse>> by ImmediateCall(emptyList()) {

    override fun enqueue(callback: Callback<List<ProductResponse>>) {
        onEnqueue(callback)
    }
}

private class ImmediateCall(private val responses: List<ProductResponse>) : Call<List<ProductResponse>> {

    override fun enqueue(callback: Callback<List<ProductResponse>>) {
        callback.onResponse(this, Response.success(responses))
    }

    override fun execute(): Response<List<ProductResponse>> = Response.success(responses)

    override fun isExecuted(): Boolean = true

    override fun cancel() = Unit

    override fun isCanceled(): Boolean = false

    override fun clone(): Call<List<ProductResponse>> = ImmediateCall(responses)

    override fun request(): Request = Request.Builder().url("http://test/products").build()

    override fun timeout(): Timeout = Timeout.NONE
}
