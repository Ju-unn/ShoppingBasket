# 진행 상황

이 문서는 "1단계~8단계" 미션 진행 상황을 기록한다. Pencil 디자인 원본은
`C:\Users\apf_temp_admin\Desktop\쇼핑\shoppingUi.pen` 이고, 각 화면 프레임에
`1단계` ~ `8단계` 라벨이 붙어 있다.

## 전체 단계 개요 (pen 디자인 기준)

| 단계 | 화면 | 코드 구현 | pen 디자인 |
|---|---|---|---|
| 1단계 | 상품 목록 / 상품 상세 / 장바구니 (기본 흐름) | ✅ 완료 | ✅ 완료 |
| 2단계 | 상품 목록 더보기(페이지네이션), 장바구니 페이징(5개 이하/초과/마지막 페이지) | ✅ 완료 | ✅ 이미지·TopBar 정리 완료 |
| 3단계 | 수량 조절 (상품목록/상품상세/장바구니에서 +-) | ❌ 미착수 | ✅ 이미지·TopBar 정리 완료 |
| 4단계 | 최근 본 상품 (목록 하단 섹션 + 상세의 마지막으로 본 상품) | ❌ 미착수 | ✅ 이미지·TopBar 정리 완료 |
| 5단계 | 로딩 스켈레톤 (상품목록/장바구니) | ❌ 미착수 | ✅ (스켈레톤이라 이미지 자체가 필요 없음) |
| 6단계 | 장바구니 선택창(체크박스 선택 후 주문), 추가 상품 추천 | ❌ 미착수 | ✅ 이미지·TopBar 정리 완료 |
| 7단계 | (pen 상에 대응 화면 없음 — 확인 필요) | ❌ 미착수 | — |
| 8단계 | 결제하기 - 쿠폰 적용 | ❌ 미착수 | ✅ (이미지 대상 없음, TopBar만 정리) |

## 지금까지 한 일

### 1. 1단계 기능 구현 (코드)
- `productlist` / `productdetail` / `basket` 3개 기능 패키지, MVVM(model/view/viewmodel) 구조로 구현
- 상품 목록 → 클릭 → 상품 상세 → 장바구니 담기 → 장바구니에서 삭제까지 전체 흐름 동작
- `Navigation Compose`로 3화면 연결 (`MainActivity.kt`)
- `CartRepository`를 인메모리 싱글톤으로 두고 화면 간 장바구니 상태 공유
- Glide로 상품 이미지 로딩
- 클래스별 JUnit5+AssertJ 단위 테스트, Composable별 androidTest 작성

### 2. Pencil 디자인(.pen) 정리 — 1단계~8단계 전체
- 1~8단계 화면의 빈 이미지 슬롯을 `images/` 폴더의 실제 상품 사진으로 채움 (상품목록과 동일한 6개 PET보틀 상품으로 통일, 스위트콘 등 안 맞는 상품 콘텐츠도 교체)
- 1~8단계 19개 화면에 제각각 만들어져 있던 상단바(raw frame)를 전부 지우고, 재사용 컴포넌트 `TopBar`의 3가지 변형(상품목록형/상세형/장바구니형)으로 통일

### 3. 코드 쪽 리팩터링
- pen의 TopBar 컴포넌트 구조에 맞춰 `ShoppingTopBar`를 확장(leadingIcon 슬롯 추가)하고, `ProductDetailScreen`/`BasketScreen`이 각자 재구현하던 topbar 코드를 제거해서 공용 컴포저블로 통일
- 테스트 중복 정리: `common/fixture/`(ProductFixture, CartRepositoryFixture)로 `testProduct()`/`clearCartRepository()` 공용화, 5개 테스트 파일의 중복 제거
- 전 화면 클릭 요소(`clickable`)에서 리플 애니메이션 제거 (`Modifier.noRippleClickable` 공용 확장 함수 추가)

### 4. 2단계 기능 구현 — 데이터 로딩 (코드)
- `common/model/Page` : 페이지 좌표 값 객체. `sliceOf` / `next` / `previous` / `hasNextIn` /
  `hasPrevious` / `coercedIn` / `number`. 상품 목록(20개)과 장바구니(5개)가 크기만 다르게 재사용한다
- 자르는 로직은 일급 컬렉션(`ProductList` / `CartItems`)의 `sliceBy` / `hasPageAfter` 에 두고,
  저장소는 `firstPage` / `findPage` / `hasPageAfter` 로 위임만 한다. **페이지 크기는 저장소 내부에 숨겼다**
  (밖에서 `Page` 를 조립하지 않고 `firstPage()` 를 받아 `next()` 로만 이동)
- 상품 목록은 **누적**(append) : `ProductListViewModel.loadMore()` 가 다음 페이지를 기존 목록 뒤에 붙인다.
  마지막 페이지면 아무 일도 하지 않는다
- 장바구니는 **교체**(replace) : `BasketViewModel.nextPage()` / `previousPage()` 가 현재 페이지만 갈아끼운다.
  상품을 지워 현재 페이지가 비면 `coercePage` 로 상품이 남아 있는 마지막 페이지로 당긴다
- `common/view/ShoppingButton` : pen 의 재사용 컴포넌트 `Button` 대응. 더보기 버튼과
  상품 상세의 "장바구니 담기" 가 공유한다 (중복 구현 제거)
- `basket/view/CartPagingControl` : pen 의 "장바구니 페이징"(129x42, 버튼 42dp, 색인 45dp).
  이동할 수 없는 방향은 `gray_border` 로 칠하고 클릭도 막는다. 페이지가 하나뿐이면 렌더링하지 않는다
- `Modifier.noRippleClickable` 에 `enabled` 파라미터 추가 (기본값 `true`)
- 브랜치 순서: `feature/common-page`(PR #3) → `feature/productlist-load-more`(PR #4) → `feature/basket-pagination`

### 5. CartRepository 를 주입 가능한 클래스로 전환 (3단계 준비, 2026-08-22)

- `object CartRepository` → `class CartRepository`. 인스턴스는 `ShoppingBasketApplication` 이 보관한다
  (Activity 에 두면 화면 회전 시 장바구니가 사라진다). `AndroidManifest.xml` 에 `android:name` 추가
- `MainActivity` 의 ViewModel 팩토리가 `CreationExtras` 의 `APPLICATION_KEY` 로 저장소를 꺼내 주입한다
- `BasketViewModel` / `ProductDetailViewModel` 이 생성자로 저장소를 받는다
- 테스트가 각자 새 저장소를 만들어 쓰게 되어 **전역 상태 청소(`CartRepositoryFixture`)를 삭제**했다
- `ShoppingBasketApplicationTest` 추가 (계측 31 → 33개)

## 결정 사항 (이 프로젝트 한정)

`CLAUDE.md` 는 여러 프로젝트가 공유하는 공통 규칙 문서다. **이 프로젝트에만 해당하는 예외·결정은
CLAUDE.md 를 고치지 말고 여기에 기록한다.**

### 1. 코루틴은 나중에 도입한다 (2026-08-22)

- `StateFlow` 는 코루틴 타입(`kotlinx.coroutines.flow.StateFlow`)이라 코루틴 없이는 쓸 수 없다.
  따라서 CLAUDE.md 3.4 의 "`StateFlow` 노출 + `collectAsStateWithLifecycle()` 구독" 은 **당분간 적용하지 않는다.**
- 그동안 ViewModel 은 지금처럼 `androidx.compose.runtime.State` / `mutableStateOf` 로 상태를 노출한다.
  (CLAUDE.md 3.1 의 Compose 타입 금지에 대한 한시적 예외. **상태 보관 타입에만** 해당하고
  `Context` / `@Composable` / `View` / `Modifier` 는 여전히 금지다.)
- 코루틴을 도입하는 시점에 3개 ViewModel(`ProductListViewModel`, `ProductDetailViewModel`,
  `BasketViewModel`)을 `StateFlow` 방식으로 일괄 전환한다. 그때 `libs.versions.toml` 에
  `kotlinx-coroutines-test`, `lifecycle-runtime-compose` 추가가 필요하다.

### 2. 브랜치 전략은 CLAUDE.md 9장을 그대로 따르지 않는다 (2026-08-22)

- 실제 저장소에는 `develop` 이 없고 기본 브랜치는 `main` 이 아니라 **`master`** 다.
- 이 프로젝트에서는 `master` 를 통합 브랜치로 쓰고, 작업은 `master` 에서 `feature/*` 를 분기해
  `master` 로 병합한다. `release/*` / `hotfix/*` 는 쓰지 않는다.
- 브랜치 이름 규칙과 커밋 메시지 규칙(CLAUDE.md 9.1 / 9.2)은 그대로 따른다.

### 3. ViewModel 은 CLAUDE.md 4.7(인스턴스 변수 2개 제한)의 예외로 둔다 (2026-08-22)

- 페이징 상태를 가지는 ViewModel 은 `repository` + 현재 페이지 + `_uiState` / `uiState` 로 최소 4개가 된다.
  기존 ViewModel 들이 2개를 지킬 수 있었던 건 들고 있을 상태가 없었기 때문이다.
- `repository` 와 페이지를 별도 객체로 묶는 방법도 검토했지만, 그 객체가 다시 `repository` 를
  들고 다녀야 해서 **클래스만 늘고 실익이 없다**고 판단해 묶지 않기로 했다.
- `model/`(Domain) 계층에는 4.7 을 그대로 엄격히 적용한다. 예외는 ViewModel 과 `UiState` 뿐이다.

### 4. 2단계에서 디자인(.pen)과 다르게 구현한 부분 (2026-08-22)

- **더보기 버튼은 pen 에 없다.** `상품 목록 - 더보기`(xkjYE) 프레임은 `상품 목록`(MiJPl) 과
  아이템 좌표가 완전히 같고(20/186 x 76/294/512) 버튼 노드가 하나도 없다.
  재사용 컴포넌트 `Button`(높이 48 / primary / 흰색 20sp Bold)을 그리드 마지막 행에
  한 줄 전체(span)로 넣는 것으로 정했다. 폭은 그리드 좌우 여백(20dp) 안쪽을 채운다.
- **마지막 페이지의 `>` 를 회색으로 바꿨다.** pen 의 `장바구니 - 마지막 페이지`(YFnu1) 는
  `>` 가 primary 초록 그대로인데, 1페이지(xsDiH)의 `<` 는 `#AAAAAA` 로 비활성 표시가 돼 있다.
  비활성 규칙이 한쪽에만 적용된 상태라 양쪽을 맞췄다.

### 5. 목업 상품을 6개에서 50개로 늘렸다 (2026-08-22)

- 페이지 크기가 20이라 기존 6개로는 더보기 버튼이 아예 뜨지 않는다.
  **20 / 20 / 10** 이 되도록 50개로 정해서, 마지막 페이지가 덜 찬 상태를 실제 화면에서도 확인할 수 있다.
- 50줄을 하드코딩하지 않고, pen 에 나오는 모양 5종 x 용량 7종 x 가격 2종을 번호로 순환시켜 생성한다.
  key 가 겹치지 않는다는 것은 `ProductRepositoryTest` 로 고정해 두었다.

### 6. 3단계 설계 결정 (2026-08-22, 사용자 확정)

3단계 미션 원문을 받고 6가지를 물어 아래로 확정했다.

| # | 쟁점 | 결정 |
|---|---|---|
| 1 | pen 안에 Stepper 정의가 2개(재사용 컴포넌트 vs 화면 프레임)라 모순 | **재사용 컴포넌트 `Stepper`(jxIE5) 기준** |
| 2 | 수량 1에서 `−` 를 누르면? | **장바구니에서 제거하고 `+` 버튼으로 되돌린다** (B마트 UX) |
| 3 | 상단바 장바구니 배지 숫자 | **총 수량** (상품 종류 수 아님) |
| 4 | 화면 간 수량 동기화 방식 | **저장소에 리스너 등록** (ViewModel 이 구독, `onCleared` 에서 해제) |
| 5 | 상품 상세의 `+` `−` 가 즉시 반영되나? | **아니다.** 상세는 로컬 수량이고 `장바구니 담기` 를 눌러야 반영 |
| 6 | `CartRepository` 를 `class` 로? | **전환함** (위 "지금까지 한 일" 5번에서 완료) |

**4번이 3단계의 핵심이다.** 기능 요구사항이 "목록↔장바구니 양방향 반영"을 요구하는데,
`CartRepository` 가 관찰 불가능한 상태라 지금 구조로는 안 된다. 코루틴을 미뤘으므로(결정 1번)
`StateFlow` 를 못 쓰고, 대신 도메인에 직접 만든 리스너를 둔다. 나중에 코루틴을 도입할 때
이 리스너를 `StateFlow` 로 갈아끼우면 된다.

**미션 힌트의 Activity Result API 는 쓰지 않는다.** 다중 Activity + XML 전제의 해법이고,
이 프로젝트는 단일 Activity + Navigation Compose 라 공유 저장소가 그 자리를 대신한다.

## 다음 단계

### 지금 상태 (2026-08-22 기준) ⚠️ 이어받을 때 먼저 확인할 것

| 브랜치 | 상태 |
|---|---|
| `master` | `01f34ee` (PR #6 머지됨). origin 과 동일 |
| `refactor/cart-repository-injection` | **작업 완료 · 커밋/푸시 전.** 위 "지금까지 한 일" 5번 내용이 여기 들어 있다 |
| `feature/basket-quantity-model` | 만들어만 두고 비어 있음 (`master` 와 동일). 위 리팩터링이 머지된 뒤 여기서 이어간다 |

**다른 기기에서 이어가려면 `refactor/cart-repository-injection` 을 먼저 커밋·푸시해야 한다.**
커밋 메시지 초안:

```
refactor(basket): CartRepository 를 주입 가능한 클래스로 전환

- object 싱글턴을 class 로 바꾸고 ShoppingBasketApplication 이 인스턴스를 보관
- BasketViewModel / ProductDetailViewModel 이 생성자로 저장소를 주입받도록 변경
- 테스트가 각자 새 저장소를 쓰게 되어 전역 상태 청소(CartRepositoryFixture) 제거
- ShoppingBasketApplicationTest 추가 (계측 31 -> 33개)
```

검증 결과: 단위 102개 + 계측 33개 전부 통과, `lintDebug` Error 0.

### 3단계 브랜치 계획 (5개)

2단계와 같은 순서(도메인 → 공용 뷰 → 화면별)이고 각각 PR 하나다.

| # | 브랜치 | 내용 |
|---|---|---|
| 1 | `feature/basket-quantity-model` | 도메인. 수량 증감/조회 + 변경 리스너 |
| 2 | `feature/common-stepper` | 공용 `Stepper` 컴포저블 (**프로그래밍 요구사항 "반복되는 뷰 재활용"**) |
| 3 | `feature/productlist-quantity` | 목록 `+` ↔ Stepper 전환, 상단바 총 수량 배지 |
| 4 | `feature/productdetail-quantity` | 상세 수량 조절 + 총액 반영 |
| 5 | `feature/basket-quantity-ui` | 장바구니 카드 안 Stepper |

**1번 브랜치에서 할 일:**

```kotlin
// common/model/Quantity.kt
fun decreased(): Quantity        // 1 밑으로는 못 내려감
fun isMinimum(): Boolean

// basket/model/CartItems.kt
fun increase(key: String): CartItems
fun decrease(key: String): CartItems
fun quantityOf(product: Product): Quantity?   // null = 장바구니에 없음
fun totalQuantity(): Int                      // 상단바 배지용

// basket/model/CartRepository.kt
fun increase(key: String) / decrease(key: String)
+ 변경 리스너 등록/해제 (결정 6-4번)
```

`Quantity` 는 `require(value >= 1)` 이라 0을 표현할 수 없다. 이 제약은 **유지**하고
"장바구니에 없음"은 `quantityOf()` 가 `null` 을 돌려주는 것으로 구분한다.
(`Quantity(0)` 을 허용하면 "0개짜리 장바구니 항목"이라는 말이 안 되는 상태가 생긴다.)

### 3단계 pen 스펙 (측정값)

**재사용 컴포넌트 `Stepper`(jxIE5)** — 결정 6-1번에 따라 이쪽을 따른다

- 126×42, 흰 배경, `gray-border` 1px 테두리, corner 4
- `−` Material `remove` 아이콘 20dp `gray5` at (10,11)
- 수량 텍스트 16sp **Bold** `black`, 중앙 (x57)
- `+` Material `add` 아이콘 20dp **`primary`** at (96,11)
- `ProductItem` / `CartItem` 컴포넌트는 100×28 로 축소해 참조한다 (아이콘 16dp, 수량 14sp)

**상품 목록**(I8FRa) — 아이템 154×206, 사진 154×154

| 상태 | 표현 |
|---|---|
| 장바구니에 없음 | `Plus` 원 **48dp** at (98,98). 흰 원 + `#555555` 선 2개(굵기 3, round cap). 사진 우하단 8dp |
| 담겨 있음 | Stepper **126×42** at (14,104). 사진 하단 중앙(좌우 14dp, 아래 8dp) |

**상단바 배지**(UFS8q): `primary` 원 **20dp** + 흰색 **14sp** Medium 숫자.
배지 자리를 만들려고 장바구니 아이콘이 왼쪽으로 24dp 밀려 있다.

**상품 상세**(C1TfD): Stepper at (220,495) — 가격 행 오른쪽.
1단계에 있던 `"가격"` 라벨이 사라지고 **금액만** 남는다. 금액은 `30,000원` = 10,000 × 3 →
**수량이 곱해진 총액**. 하단 `장바구니 담기` 버튼(360×48)은 그대로.

**장바구니**(YZEbS): 카드 324×152, Stepper at (186,60) — 카드 안 우측 하단.

### 검토가 필요한 지점

1. **`ShoppingBasketApplication.cartRepository` 가 public `val` 이다.**
   CLAUDE.md 4.9(프로퍼티 노출 금지)에 걸리지만 Domain 이 아니라 프레임워크/DI 계층이라 예외로 뒀다.
   함수로 바꿀지 결정 필요
2. **계측 테스트가 여전히 raw `assert()` 를 쓴다.** AssertJ 가 `androidTestImplementation` 에 없다.
   의존성 추가는 사용자 승인이 필요해 손대지 않았다 (아래 "남긴 것" 과 같은 항목)
3. **결정 6-1번(재사용 컴포넌트 기준)의 부작용**: 상세 화면에서 Stepper 가 흰 배경 위에 놓이는데,
   컴포넌트에 `gray-border` 테두리가 있어 다행히 보인다. 다만 화면 프레임 쪽 디자인(테두리 없음,
   22sp 회색)과 실제 화면이 달라 보이게 되므로, 구현 후 눈으로 한 번 확인이 필요하다
4. **결정 6-2번(수량 1에서 제거)은 pen 에 없는 동작이다.** 미션의 "B마트 UX 참고" 를 근거로 정했다

### 아직 남긴 것 (2단계에서 이월)

- **`common/fixture/ProductFixture.kt` 는 `src/test` 에서만 쓸 수 있다.**
  androidTest 는 별도 소스셋이라 계측 테스트들이 각자 `Product(...)` 를 인라인으로 만들고 있다
- **androidTest 에 AssertJ 가 없어 raw `assert()` 를 쓰고 있다.** CLAUDE.md 8.1 은 계측 테스트에도
  AssertJ 를 권하지만 `androidTestImplementation` 에 빠져 있다 (의존성 추가는 사용자 확인 필요)
- ~~계측 테스트를 실행하지 못했다~~ → **해결됨.** 에뮬레이터(`Medium_Phone_API_36`)에서 33개 통과
- ~~`BasketViewModel` 이 `CartRepository` 싱글턴을 직접 참조한다~~ → **해결됨** (지금까지 한 일 5번)

**작업 방식(변하지 않는 원칙)**
- 한 번에 한 단계만 진행하고 사용자 검토를 받는다
- 단계가 끝나면 커밋 메시지 초안을 제안한다 (Claude는 직접 commit/push 하지 않음)
- 화면 작업 전 Pencil 디자인을 먼저 확인한다
- 한 브랜치에는 한 기능만 담는다. 계층(ViewModel/View)이 아니라 기능 단위로 자른다
- 빌드/테스트는 `.\gradlew testDebugUnitTest connectedDebugAndroidTest` 로 확인한다
  (`.\gradlew test --tests` 는 Android 에서 동작하지 않는다)
