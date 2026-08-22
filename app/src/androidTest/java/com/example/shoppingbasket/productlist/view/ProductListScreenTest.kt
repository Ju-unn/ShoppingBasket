package com.example.shoppingbasket.productlist.view

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.shoppingbasket.common.model.Money
import com.example.shoppingbasket.productlist.model.Product
import com.example.shoppingbasket.productlist.model.ProductId
import com.example.shoppingbasket.productlist.model.ProductImageUrl
import com.example.shoppingbasket.productlist.model.ProductName
import com.example.shoppingbasket.productlist.viewmodel.ProductListUiState
import org.junit.Rule
import org.junit.Test

class ProductListScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun displaysTopBarAndProducts() {
        setScreen(ProductListUiState(products = listOf(testProduct())))

        composeTestRule.onNodeWithText("Shopping").assertExists()
        composeTestRule.onNodeWithText("PET보틀-정사각(500ml)").assertExists()
        composeTestRule.onNodeWithText("10,000원").assertExists()
    }

    @Test
    fun clickingProductInvokesCallbackWithKey() {
        var clickedKey: String? = null
        setScreen(
            uiState = ProductListUiState(products = listOf(testProduct())),
            onProductClick = { key -> clickedKey = key }
        )

        composeTestRule.onNodeWithText("PET보틀-정사각(500ml)").performClick()

        assert(clickedKey == "p1")
    }

    @Test
    fun displaysLoadMoreButtonWhenMorePagesRemain() {
        setScreen(ProductListUiState(products = listOf(testProduct()), canLoadMore = true))

        composeTestRule.onNodeWithText("더보기").assertIsDisplayed()
    }

    @Test
    fun hidesLoadMoreButtonOnLastPage() {
        setScreen(ProductListUiState(products = listOf(testProduct()), canLoadMore = false))

        composeTestRule.onNodeWithText("더보기").assertDoesNotExist()
    }

    @Test
    fun clickingLoadMoreInvokesCallback() {
        var loadMoreCalled = false
        setScreen(
            uiState = ProductListUiState(products = listOf(testProduct()), canLoadMore = true),
            onLoadMore = { loadMoreCalled = true }
        )

        composeTestRule.onNodeWithText("더보기").performClick()

        assert(loadMoreCalled)
    }

    private fun setScreen(
        uiState: ProductListUiState,
        onProductClick: (String) -> Unit = {},
        onLoadMore: () -> Unit = {}
    ) {
        composeTestRule.setContent {
            ProductListScreen(
                uiState = uiState,
                onProductClick = onProductClick,
                onCartClick = {},
                onLoadMore = onLoadMore
            )
        }
    }

    private fun testProduct(): Product = Product(
        ProductId("p1"),
        ProductName("PET보틀-정사각(500ml)"),
        Money(10_000),
        ProductImageUrl("https://picsum.photos/seed/p1/600")
    )
}
