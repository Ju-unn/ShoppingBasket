package com.example.shoppingbasket.productlist.view

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.shoppingbasket.common.model.Money
import com.example.shoppingbasket.common.model.Quantity
import com.example.shoppingbasket.productlist.model.Product
import com.example.shoppingbasket.productlist.model.ProductId
import com.example.shoppingbasket.productlist.model.ProductImageUrl
import com.example.shoppingbasket.productlist.model.ProductName
import com.example.shoppingbasket.productlist.viewmodel.ProductListItem
import com.example.shoppingbasket.productlist.viewmodel.ProductListUiState
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

        assert(clickedKey == "p1")
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

        assert(loadMoreCalled)
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

        assert(addedKey == "p1")
    }

    private fun setScreen(
        uiState: ProductListUiState,
        onProductClick: (String) -> Unit = {},
        onLoadMore: () -> Unit = {},
        onAddToCart: (String) -> Unit = {},
        onIncrease: (String) -> Unit = {},
        onDecrease: (String) -> Unit = {}
    ) {
        composeTestRule.setContent {
            ProductListScreen(
                uiState = uiState,
                onProductClick = onProductClick,
                onCartClick = {},
                onLoadMore = onLoadMore,
                onAddToCart = onAddToCart,
                onIncrease = onIncrease,
                onDecrease = onDecrease
            )
        }
    }

    private fun testItem(cartQuantity: Quantity? = null): ProductListItem {
        val product = Product(
            ProductId("p1"),
            ProductName("PET보틀-정사각(500ml)"),
            Money(10_000),
            ProductImageUrl("https://picsum.photos/seed/p1/600")
        )
        return ProductListItem(product, cartQuantity)
    }
}
