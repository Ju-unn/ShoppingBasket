package com.example.shoppingbasket

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import com.example.shoppingbasket.productlist.model.ProductRepository
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MainActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    // 목업 상품 이름은 생성 규칙에 따라 달라지므로 하드코딩하지 않고 저장소에서 가져온다
    private val firstProductName = ProductRepository().findAll().items().first().displayName()

    @Before
    fun clearCart() {
        // 장바구니 저장소는 Application 수명이라 같은 프로세스의 앞선 테스트 결과가 남는다
        val cartRepository = ApplicationProvider
            .getApplicationContext<ShoppingBasketApplication>()
            .cartRepository
        cartRepository.findAll().items().forEach { item -> cartRepository.remove(item.key()) }
    }

    @Test
    fun navigatesFromListToDetailAndBack() {
        composeTestRule.onNodeWithText(firstProductName).performClick()

        composeTestRule.onNodeWithText("장바구니 담기").assertExists()

        composeTestRule.onNodeWithContentDescription("닫기").performClick()

        composeTestRule.onNodeWithText("Shopping").assertExists()
    }

    @Test
    fun navigatesToBasketFromCartIcon() {
        composeTestRule.onNodeWithContentDescription("장바구니").performClick()

        composeTestRule.onNodeWithText("Cart").assertExists()
    }

    @Test
    fun addsProductToCartAndSeesItInBasket() {
        composeTestRule.onNodeWithText(firstProductName).performClick()
        composeTestRule.onNodeWithText("장바구니 담기").performClick()
        composeTestRule.onNodeWithContentDescription("닫기").performClick()

        composeTestRule.onNodeWithContentDescription("장바구니").performClick()

        composeTestRule.onNodeWithText("Cart").assertExists()
        composeTestRule.onNodeWithText(firstProductName).assertExists()
    }
}
