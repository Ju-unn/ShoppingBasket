package com.example.shoppingbasket

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import org.junit.Before
import org.junit.Rule
import org.junit.Test

// 서버가 http://127.0.0.1:8080 에서 떠 있어야 한다(에뮬레이터는 10.0.2.2 로 접근). server/ 디렉터리에서 ./gradlew run
class MainActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    // 서버 MockProducts 의 p1 값과 동일해야 한다 (4단계 결정 5번 — 생성 규칙이 서버 쪽으로 옮겨가 더는 앱에서 계산할 수 없다)
    private val firstProductName = "PET보틀-밀크티병(300ml)"

    @Before
    fun clearSharedRepositories() {
        // 장바구니/최근 본 상품 저장소는 Application 수명이라 같은 프로세스의 앞선 테스트 결과가 남는다
        val application = ApplicationProvider.getApplicationContext<ShoppingBasketApplication>()
        val cartRepository = application.cartRepository
        cartRepository.findAll().items().forEach { item -> cartRepository.remove(item.key()) }
        application.recentlyViewedRepository.clear()
    }

    @Before
    fun waitForProductsToLoad() {
        // 상품 목록이 이제 서버 응답을 기다려야 채워진다 (5단계, Call.enqueue 비동기 호출)
        composeTestRule.waitUntil(timeoutMillis = 10_000) {
            composeTestRule.onAllNodesWithText(firstProductName).fetchSemanticsNodes().isNotEmpty()
        }
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
