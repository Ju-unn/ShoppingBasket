# 공부할 목록

## Navigation Compose

`MainActivity.kt`에서 상품 목록 → 상품 상세 → 장바구니 화면 전환에 `androidx.navigation:navigation-compose`를 적용했다. 지금은 동작하는 코드를 그대로 사용했지만, 아래 개념은 따로 공부가 필요하다.

- `NavHost` / `NavController` / `composable` route 정의 방식
- route에 인자 전달하기 (`navArgument`, `backStackEntry.arguments`) — 지금은 `productDetail/{productId}` 같은 문자열 경로에 직접 값을 끼워 넣었는데, 타입 안전하게 만드는 방법(type-safe navigation, Kotlin Serialization 기반 route)도 있음
- `popBackStack()`과 백스택 동작 원리
- 화면 간 이동 시 ViewModel 생명주기 (destination을 벗어나면 ViewModel이 언제 정리되는지)
- 딥링크, 중첩 네비게이션 그래프 (지금 프로젝트 범위 밖이지만 참고)
