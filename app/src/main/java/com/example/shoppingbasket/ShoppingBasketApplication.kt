package com.example.shoppingbasket

import android.app.Application
import com.example.shoppingbasket.basket.model.CartRepository
import com.example.shoppingbasket.recentlyviewed.model.RecentlyViewedRepository

// 앱 전체가 공유하는 의존성을 보관한다 (수동 DI 컨테이너).
// 장바구니/최근 본 상품은 화면을 옮겨 다녀도 유지되어야 하므로 Activity 가 아니라 Application 수명에 맞춘다.
// 프레임워크 계층이라 프로퍼티 노출 금지(CLAUDE.md 4.9)의 대상이 아니다.
class ShoppingBasketApplication : Application() {

    val cartRepository: CartRepository = CartRepository()
    val recentlyViewedRepository: RecentlyViewedRepository = RecentlyViewedRepository()
}
