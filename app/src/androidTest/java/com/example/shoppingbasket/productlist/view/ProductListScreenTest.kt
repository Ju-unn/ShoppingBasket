package com.example.shoppingbasket.productlist.view

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.shoppingbasket.common.model.Money
import com.example.shoppingbasket.common.model.Quantity
import com.example.shoppingbasket.productlist.domain.model.Product
import com.example.shoppingbasket.productlist.domain.model.ProductId
import com.example.shoppingbasket.productlist.domain.model.ProductImageUrl
import com.example.shoppingbasket.productlist.domain.model.ProductName
import com.example.shoppingbasket.productlist.viewmodel.ProductListItem
import com.example.shoppingbasket.productlist.viewmodel.ProductListUiState
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test

class ProductListScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun displaysTopBarAndProducts() {
        setScreen(ProductListUiState(products = listOf(testItem())))

        composeTestRule.onNodeWithText("Shopping").assertExists()
        composeTestRule.onNodeWithText("PET보틀-정사각(500ml)").assertExists()
        composeTestRule.onNodeWithText("10,000원").assertExists()
    }

    @Test
    fun clickingProductInvokesCallbackWithKey() {
        var clickedKey: String? = null
        setScreen(
            uiState = ProductListUiState(products = listOf(testItem())),
            onProductClick = { key -> clickedKey = key }
        )

        composeTestRule.onNodeWithText("PET보틀-정사각(500ml)").performClick()

        assertThat(clickedKey).isEqualTo("p1")
    }

    @Test
    fun displaysLoadMoreButtonWhenMorePagesRemain() {
        setScreen(ProductListUiState(products = listOf(testItem()), canLoadMore = true))

        composeTestRule.onNodeWithText("더보기").assertIsDisplayed()
    }

    @Test
    fun hidesLoadMoreButtonOnLastPage() {
        setScreen(ProductListUiState(products = listOf(testItem()), canLoadMore = false))

        composeTestRule.onNodeWithText("더보기").assertDoesNotExist()
    }

    @Test
    fun clickingLoadMoreInvokesCallback() {
        var loadMoreCalled = false
        setScreen(
            uiState = ProductListUiState(products = listOf(testItem()), canLoadMore = true),
            onLoadMore = { loadMoreCalled = true }
        )

        composeTestRule.onNodeWithText("더보기").performClick()

        assertThat(loadMoreCalled).isTrue()
    }

    @Test
    fun hidesCartBadgeWhenEmpty() {
        setScreen(ProductListUiState(cartBadgeText = "0"))

        composeTestRule.onNodeWithText("0").assertDoesNotExist()
    }

    @Test
    fun displaysCartBadgeWhenNotEmpty() {
        setScreen(ProductListUiState(cartBadgeText = "3"))

        composeTestRule.onNodeWithText("3").assertExists()
    }

    @Test
    fun clickingAddButtonInvokesCallbackWithKey() {
        var addedKey: String? = null
        setScreen(
            uiState = ProductListUiState(products = listOf(testItem(cartQuantity = null))),
            onAddToCart = { key -> addedKey = key }
        )

        composeTestRule.onNodeWithContentDescription("장바구니에 담기").performClick()

        assertThat(addedKey).isEqualTo("p1")
    }

    @Test
    fun hidesRecentlyViewedSectionWhenEmpty() {
        setScreen(ProductListUiState(recentlyViewed = emptyList()))

        composeTestRule.onNodeWithText("최근 본 상품").assertDoesNotExist()
    }

    @Test
    fun displaysRecentlyViewedSectionWhenNotEmpty() {
        setScreen(ProductListUiState(recentlyViewed = listOf(testRecentlyViewedProduct())))

        composeTestRule.onNodeWithText("최근 본 상품").assertIsDisplayed()
        composeTestRule.onNodeWithText("PET보틀-정사각(500ml)").assertIsDisplayed()
    }

    @Test
    fun clickingRecentlyViewedItemInvokesCallbackWithKey() {
        var clickedKey: String? = null
        setScreen(
            uiState = ProductListUiState(recentlyViewed = listOf(testRecentlyViewedProduct())),
            onProductClick = { key -> clickedKey = key }
        )

        composeTestRule.onNodeWithText("PET보틀-정사각(500ml)").performClick()

        assertThat(clickedKey).isEqualTo("p1")
    }

    @Test
    fun showsLoadingIndicatorWhileLoading() {
        setScreen(ProductListUiState(isLoading = true))

        composeTestRule.onNodeWithContentDescription("상품 불러오는 중").assertExists()
    }

    @Test
    fun hidesProductGridWhileLoading() {
        setScreen(ProductListUiState(products = listOf(testItem()), isLoading = true))

        composeTestRule.onNodeWithText("PET보틀-정사각(500ml)").assertDoesNotExist()
    }

    @Test
    fun showsFailureMessageAndRetryButtonWhenLoadFailed() {
        setScreen(ProductListUiState(hasLoadFailed = true))

        composeTestRule.onNodeWithText("상품을 불러오지 못했습니다").assertIsDisplayed()
        composeTestRule.onNodeWithText("다시 시도").assertIsDisplayed()
    }

    @Test
    fun keepsTopBarWhenLoadFailed() {
        setScreen(ProductListUiState(hasLoadFailed = true))

        composeTestRule.onNodeWithText("Shopping").assertExists()
        composeTestRule.onNodeWithContentDescription("장바구니").assertExists()
    }

    @Test
    fun hidesProductGridWhenLoadFailed() {
        setScreen(ProductListUiState(products = listOf(testItem()), hasLoadFailed = true))

        composeTestRule.onNodeWithText("PET보틀-정사각(500ml)").assertDoesNotExist()
    }

    @Test
    fun clickingRetryInvokesCallback() {
        var retryCalled = false
        setScreen(uiState = ProductListUiState(hasLoadFailed = true), onRetry = { retryCalled = true })

        composeTestRule.onNodeWithText("다시 시도").performClick()

        assertThat(retryCalled).isTrue()
    }

    @Test
    fun hidesFailureMessageWhileLoading() {
        setScreen(ProductListUiState(isLoading = true, hasLoadFailed = true))

        composeTestRule.onNodeWithText("다시 시도").assertDoesNotExist()
    }

    private fun setScreen(
        uiState: ProductListUiState,
        onProductClick: (String) -> Unit = {},
        onLoadMore: () -> Unit = {},
        onAddToCart: (String) -> Unit = {},
        onIncrease: (String) -> Unit = {},
        onDecrease: (String) -> Unit = {},
        onRetry: () -> Unit = {}
    ) {
        composeTestRule.setContent {
            ProductListScreen(
                uiState = uiState,
                onProductClick = onProductClick,
                onCartClick = {},
                onLoadMore = onLoadMore,
                onAddToCart = onAddToCart,
                onIncrease = onIncrease,
                onDecrease = onDecrease,
                onRetry = onRetry
            )
        }
    }

    private fun testItem(cartQuantity: Quantity? = null): ProductListItem {
        return ProductListItem(testRecentlyViewedProduct(), cartQuantity)
    }

    private fun testRecentlyViewedProduct(): Product {
        return Product(
            ProductId("p1"),
            ProductName("PET보틀-정사각(500ml)"),
            Money(10_000),
            ProductImageUrl("https://picsum.photos/seed/p1/600")
        )
    }
}
