package com.example.shoppingbasket.productlist.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.shoppingbasket.basket.model.CartRepository
import com.example.shoppingbasket.common.model.Page
import com.example.shoppingbasket.productlist.model.Product
import com.example.shoppingbasket.productlist.model.ProductRepository
import com.example.shoppingbasket.recentlyviewed.model.RecentlyViewedRepository

class ProductListViewModel(
    private val cartRepository: CartRepository,
    private val recentlyViewedRepository: RecentlyViewedRepository = RecentlyViewedRepository(),
    private val repository: ProductRepository = ProductRepository()
) : ViewModel() {

    // 지금까지 읽어들인 마지막 페이지. 더보기를 누를 때마다 다음 페이지로 넘어간다.
    private var loadedPage: Page = repository.firstPage()
    private var loadedProducts: List<Product> = productsOf(loadedPage)

    // 장바구니/최근 본 상품이 다른 화면(상세)에서 바뀌어도 갱신하기 위한 구독 (3단계 확정 사항)
    private val cartChangeListener = { refreshState() }
    private val recentlyViewedChangeListener = { refreshState() }

    private val _uiState = mutableStateOf(stateFor(loadedProducts))
    val uiState: State<ProductListUiState> = _uiState

    init {
        cartRepository.addChangeListener(cartChangeListener)
        recentlyViewedRepository.addChangeListener(recentlyViewedChangeListener)
    }

    override fun onCleared() {
        cartRepository.removeChangeListener(cartChangeListener)
        recentlyViewedRepository.removeChangeListener(recentlyViewedChangeListener)
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

    private fun refreshState() {
        _uiState.value = stateFor(loadedProducts)
    }

    private fun stateFor(products: List<Product>): ProductListUiState {
        return ProductListUiState(
            products = products.map { product -> ProductListItem(product, cartRepository.quantityOf(product)) },
            canLoadMore = repository.hasPageAfter(loadedPage),
            cartBadgeText = cartRepository.findAll().totalQuantityDisplayValue(),
            recentlyViewed = recentlyViewedRepository.findAll().items()
        )
    }

    private fun productsOf(page: Page): List<Product> = repository.findPage(page).items()
}
