package com.example.shoppingbasket.common.fixture

import com.example.shoppingbasket.basket.model.CartRepository

/**
 * CartRepository는 싱글톤이라 테스트 간 상태가 공유된다.
 * 각 테스트의 @BeforeEach에서 호출해 비우고 시작한다.
 */
fun clearCartRepository() {
    CartRepository.findAll().items().forEach { item -> CartRepository.remove(item.key()) }
}
