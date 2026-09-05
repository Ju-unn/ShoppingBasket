package com.example.shoppingbasket.common.fixture

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStore

// onCleared() 는 protected 라 테스트에서 직접 부를 수 없다.
// 화면이 실제로 사라질 때와 같은 경로인 ViewModelStore.clear() 를 태워 정리 과정을 재현한다
fun clearViewModel(viewModel: ViewModel) {
    val store = ViewModelStore()
    store.put(STORE_KEY, viewModel)
    store.clear()
}

private const val STORE_KEY = "test"
