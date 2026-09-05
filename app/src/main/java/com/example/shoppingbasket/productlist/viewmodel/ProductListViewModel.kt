package com.example.shoppingbasket.productlist.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.shoppingbasket.basket.model.CartRepository
import com.example.shoppingbasket.common.model.Page
import com.example.shoppingbasket.productlist.data.repository.ProductRepositoryImpl
import com.example.shoppingbasket.productlist.domain.model.Product
import com.example.shoppingbasket.productlist.domain.repository.ProductRepository
import com.example.shoppingbasket.recentlyviewed.model.RecentlyViewedRepository

class ProductListViewModel(
    private val cartRepository: CartRepository,
    private val recentlyViewedRepository: RecentlyViewedRepository = RecentlyViewedRepository(),
    private val repository: ProductRepository = ProductRepositoryImpl()
) : ViewModel() {

    // 지금까지 읽어들인 마지막 페이지. 더보기를 누를 때마다 다음 페이지로 넘어간다.
    private var loadedPage: Page = repository.firstPage()
    private var loadedProducts: List<Product> = productsOf(loadedPage)

    // 장바구니/최근 본 상품이 다른 화면(상세)에서 바뀌어도 갱신하기 위한 구독 (3단계 확정 사항)
    private val cartChangeListener = { refreshState() }
    private val recentlyViewedChangeListener = { refreshState() }

    private val _uiState = mutableStateOf(stateFor(loadedProducts, isLoading = true))
    val uiState: State<ProductListUiState> = _uiState

    init {
        cartRepository.addChangeListener(cartChangeListener)
        recentlyViewedRepository.addChangeListener(recentlyViewedChangeListener)
        fetchProducts()
    }

    override fun onCleared() {
        cartRepository.removeChangeListener(cartChangeListener)
        recentlyViewedRepository.removeChangeListener(recentlyViewedChangeListener)
    }

    // 실패 안내의 "다시 시도" 를 누르면 다시 로딩 상태로 돌아가 같은 요청을 한 번 더 보낸다
    fun retry() {
        _uiState.value = stateFor(loadedProducts, isLoading = true)
        fetchProducts()
    }

    // 다음 페이지를 읽어 기존 목록 뒤에 이어 붙인다 (교체가 아니라 누적)
    fun loadMore() {
        if (!repository.hasPageAfter(loadedPage)) {
            return
        }
        loadedPage = loadedPage.next()
        loadedProducts = loadedProducts + productsOf(loadedPage)
        _uiState.value = stateFor(loadedProducts)
    }

    fun addToCart(key: String) {
        val product = loadedProducts.find { candidate -> candidate.key() == key } ?: return
        cartRepository.add(product)
    }

    fun increase(key: String) {
        cartRepository.increase(key)
    }

    fun decrease(key: String) {
        cartRepository.decrease(key)
    }

    private fun fetchProducts() {
        repository.fetchProducts(onLoaded = { onProductsFetched() }, onFailed = { onProductsFetchFailed() })
    }

    // 서버 응답이 도착하면 목록을 다시 첫 페이지부터 채우고 로딩을 끝낸다
    private fun onProductsFetched() {
        loadedPage = repository.firstPage()
        loadedProducts = productsOf(loadedPage)
        _uiState.value = stateFor(loadedProducts)
    }

    // 실패하면 로딩을 끝내되 빈 목록을 정상인 것처럼 보여주지 않고 실패 상태로 표시한다
    private fun onProductsFetchFailed() {
        _uiState.value = stateFor(loadedProducts, hasLoadFailed = true)
    }

    private fun refreshState() {
        _uiState.value = stateFor(loadedProducts)
    }

    private fun stateFor(
        products: List<Product>,
        isLoading: Boolean = false,
        hasLoadFailed: Boolean = false
    ): ProductListUiState {
        return ProductListUiState(
            products = products.map { product -> ProductListItem(product, cartRepository.quantityOf(product)) },
            canLoadMore = repository.hasPageAfter(loadedPage),
            cartBadgeText = cartRepository.findAll().totalQuantityDisplayValue(),
            recentlyViewed = recentlyViewedRepository.findAll().items(),
            isLoading = isLoading,
            hasLoadFailed = hasLoadFailed
        )
    }

    private fun productsOf(page: Page): List<Product> = repository.findPage(page).items()
}
