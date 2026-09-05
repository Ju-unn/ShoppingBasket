package com.example.shoppingbasket.common.fixture

import com.example.shoppingbasket.productlist.data.remote.ProductApi
import com.example.shoppingbasket.productlist.data.remote.ProductResponse
import com.example.shoppingbasket.productlist.data.repository.ProductRepositoryImpl
import com.example.shoppingbasket.productlist.domain.repository.ProductRepository
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

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
// 로딩 상태가 응답 전/후로 바뀌는지, 실패했을 때 화면이 어떻게 되는지 검증할 때 쓴다.
class ManualProductApi : ProductApi {

    private var pendingCallback: Callback<List<ProductResponse>>? = null

    override fun fetchProducts(): Call<List<ProductResponse>> {
        return ManualCall { callback -> pendingCallback = callback }
    }

    fun completeWith(responses: List<ProductResponse>) {
        pendingCallback?.onResponse(ImmediateCall(responses), Response.success(responses))
    }

    // 서버에 아예 닿지 못한 상황(연결 거부·타임아웃)을 흉내 낸다
    fun failWith(throwable: Throwable = IOException("서버에 연결할 수 없습니다")) {
        pendingCallback?.onFailure(ImmediateCall(emptyList()), throwable)
    }

    // 연결은 됐지만 서버가 오류를 돌려준 상황(HTTP 500 등)을 흉내 낸다
    fun completeWithServerError(code: Int = SERVER_ERROR_CODE) {
        pendingCallback?.onResponse(ImmediateCall(emptyList()), errorResponse(code))
    }
}

// 항상 실패하는 가짜 API. 생성 즉시 실패 경로를 타야 하는 테스트에서 쓴다.
class FailingProductApi(private val throwable: Throwable = IOException("서버에 연결할 수 없습니다")) : ProductApi {

    override fun fetchProducts(): Call<List<ProductResponse>> = FailingCall(throwable)
}

// 항상 HTTP 오류를 돌려주는 가짜 API.
class ServerErrorProductApi(private val code: Int = SERVER_ERROR_CODE) : ProductApi {

    override fun fetchProducts(): Call<List<ProductResponse>> = ServerErrorCall(code)
}

// Retrofit 오류 응답에는 본문이 필요하다. 내용은 검증 대상이 아니라 빈 JSON 으로 채운다
private fun errorResponse(code: Int): Response<List<ProductResponse>> {
    return Response.error(code, EMPTY_JSON_BODY.toResponseBody(JSON_MEDIA_TYPE))
}

private const val SERVER_ERROR_CODE = 500
private const val EMPTY_JSON_BODY = "{}"
private val JSON_MEDIA_TYPE = "application/json".toMediaType()

fun fakeProductResponses(count: Int = 50): List<ProductResponse> {
    return (1..count).map { number -> ProductResponse("p$number", "상품-$number", 10_000, "https://picsum.photos/seed/p$number/600") }
}

fun fakeProductRepository(count: Int = 50): ProductRepository {
    val repository = ProductRepositoryImpl(FakeProductApi(fakeProductResponses(count)))
    repository.fetchProducts(onLoaded = {}, onFailed = {})
    return repository
}

private class ManualCall(
    private val onEnqueue: (Callback<List<ProductResponse>>) -> Unit
) : Call<List<ProductResponse>> by ImmediateCall(emptyList()) {

    override fun enqueue(callback: Callback<List<ProductResponse>>) {
        onEnqueue(callback)
    }
}

private class FailingCall(private val throwable: Throwable) : Call<List<ProductResponse>> by ImmediateCall(emptyList()) {

    override fun enqueue(callback: Callback<List<ProductResponse>>) {
        callback.onFailure(this, throwable)
    }
}

private class ServerErrorCall(private val code: Int) : Call<List<ProductResponse>> by ImmediateCall(emptyList()) {

    override fun enqueue(callback: Callback<List<ProductResponse>>) {
        callback.onResponse(this, errorResponse(code))
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
