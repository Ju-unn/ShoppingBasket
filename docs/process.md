# 진행 상황

이 문서는 "1단계~8단계" 미션 진행 상황을 기록한다. Pencil 디자인 원본은
`C:\Users\apf_temp_admin\Desktop\쇼핑\shoppingUi.pen` 이고, 각 화면 프레임에
`1단계` ~ `8단계` 라벨이 붙어 있다.

## 전체 단계 개요 (pen 디자인 기준)

| 단계 | 화면 | 코드 구현 | pen 디자인 |
|---|---|---|---|
| 1단계 | 상품 목록 / 상품 상세 / 장바구니 (기본 흐름) | ✅ 완료 | ✅ 완료 |
| 2단계 | 상품 목록 더보기(페이지네이션), 장바구니 페이징(5개 이하/초과/마지막 페이지) | ✅ 완료 | ✅ 이미지·TopBar 정리 완료 |
| 3단계 | 수량 조절 (상품목록/상품상세/장바구니에서 +-) | ✅ 완료 (2026-09-01, PR #7~#14 병합) | ✅ 이미지·TopBar 정리 완료 |
| 4단계 | 최근 본 상품 + HTTP Client (목록 상단 섹션 + 상세의 마지막으로 본 상품 + 로컬 서버 연동) | 🚧 계획 중 (아래 "4단계 준비" 참고) | ✅ 이미지·TopBar 정리 완료 |
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

### 6. 3단계 기능 구현 완료 (2026-09-01)

계획대로 5개 브랜치를 도메인 → 공용 뷰 → 화면별 순서로 진행, 전부 `master` 에 병합했다.

| # | 브랜치 | PR | 내용 |
|---|---|---|---|
| 1 | `feature/basket-quantity-model` | #9 | `Quantity.decreased()/isMinimum()`, `CartItem.decreased()`, `CartItems.increase/decrease/quantityOf/totalQuantity`, `CartRepository` 증감 위임 + 변경 리스너 |
| 2 | `feature/common-stepper` | #10 | 공용 `Stepper` 컴포저블 (126×42, pen 기준) |
| 3 | `feature/productlist-quantity` | #11 | 목록 담기버튼↔Stepper 전환, 상단바 총 수량 배지, 리스너로 장바구니 변경 반영 |
| 4 | `feature/productdetail-quantity` | #13 | 상세 로컬 수량 Stepper + 수량×단가 총액 표시 |
| 5 | `feature/basket-quantity-ui` | #14 | 장바구니 카드 안 Stepper, `BasketViewModel` 증감 + 변경 리스너 |

- 최종 병합 커밋: `6f2a93d` (`master`)
- 단위 테스트(JUnit5+AssertJ) + 계측 테스트(JUnit4, 에뮬레이터 `Medium_Phone(AVD) - 16`) 51개 전부 통과 확인
- 진행 중 계측 테스트 1건 발견·수정: `BasketScreenTest.displaysPagingControlWhenMorePagesRemain` 가 `onNodeWithText("1")` 로 페이지 번호를 찾았는데, 카드 수량(Stepper)도 "1"을 표시해 텍스트가 겹쳐 실패 → 테스트용 수량을 2로 바꿔 해결 (프로덕션 로직 문제 아님)
- 결정 6-1~6-6번(위 "3단계 설계 결정" 표)이 실제 구현 기준으로 그대로 적용됨

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

### 지금 상태 (2026-09-03 기준) ⚠️ 이어받을 때 먼저 확인할 것

| 브랜치 | 상태 |
|---|---|
| `master` | PR #21(`test/productlist-mockwebserver`)까지 전부 머지됨 |
| `feature/persistence-room` | 작업 완료, 사용자 리뷰 대기 중(커밋/푸시 전) — 7번(마지막) 브랜치 |

4단계(최근 본 상품 + HTTP Client + Room 영속화, 브랜치 1~7)가 전부 완료됐다. 미션 원문 요구사항은 "미션 원문 요구사항 vs 이 프로젝트 상태" 표 기준으로 전부 ✅. 다음 작업은 사용자 지시에 따라 새로 정한다(5단계 계획 없음, 아래는 3단계 시점의 옛 기록).

### 현재 막힌 부분 (2026-09-03, 내일 이어서 작업)

- 로컬 서버(`server/` 디렉터리)를 실행하려는데 `..\gradlew run` 이 `ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.` 로 실패
- 원인: 터미널 세션에 `JAVA_HOME` 환경변수가 안 잡혀 있음. Android Studio 내장 JDK 경로(`C:\Program Files\Android\Android Studio\jbr`)는 확인해둠
- 임시 해결책(세션 한정, 아직 실행까지 검증 안 함): PowerShell 이면 `$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"`, cmd 면 `set JAVA_HOME=C:\Program Files\Android\Android Studio\jbr` 를 `..\gradlew run` 전에 실행
- 내일 할 일: 위 임시 해결책으로 서버가 실제로 뜨는지 확인 → 되면 `JAVA_HOME` 을 시스템 환경변수에 영구 등록할지 여부 결정(사용자 확인 필요, 시스템 설정 변경이라 임의로 안 함) → 서버 뜬 상태에서 앱 실행해 상품 목록/이미지가 정상 로드되는지 확인

### 3단계 브랜치 계획 — 완료 (기록용)

실제 진행 내역은 위 "지금까지 한 일" 6번 참고. 아래는 착수 전 세운 계획으로, 실행 순서·내용이
그대로 지켜졌다.

| # | 브랜치 | 내용 |
|---|---|---|
| 1 | `feature/basket-quantity-model` | 도메인. 수량 증감/조회 + 변경 리스너 |
| 2 | `feature/common-stepper` | 공용 `Stepper` 컴포저블 (**프로그래밍 요구사항 "반복되는 뷰 재활용"**) |
| 3 | `feature/productlist-quantity` | 목록 `+` ↔ Stepper 전환, 상단바 총 수량 배지 |
| 4 | `feature/productdetail-quantity` | 상세 수량 조절 + 총액 반영 |
| 5 | `feature/basket-quantity-ui` | 장바구니 카드 안 Stepper |

### 3단계 pen 스펙 (측정값, 구현에 그대로 반영됨)

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
   함수로 바꿀지 결정 필요 (미해결)
2. **계측 테스트가 여전히 raw `assert()` 를 쓴다.** AssertJ 가 `androidTestImplementation` 에 없다.
   의존성 추가는 사용자 승인이 필요해 손대지 않았다 (미해결, 아래 "남긴 것" 참고)
3. ~~결정 6-1번(재사용 컴포넌트 기준)의 부작용 확인~~ → **해결됨.** 3단계 구현 완료 후 pen 스크린샷과
   대조해 상품목록/상세/장바구니 3곳 모두 눈으로 확인함
4. **결정 6-2번(수량 1에서 제거)은 pen 에 없는 동작이다.** 미션의 "B마트 UX 참고" 를 근거로 정했다 (기록용, 조치 불필요)

### 아직 남긴 것 (2단계에서 이월)

- **`common/fixture/ProductFixture.kt` 는 `src/test` 에서만 쓸 수 있다.**
  androidTest 는 별도 소스셋이라 계측 테스트들이 각자 `Product(...)` 를 인라인으로 만들고 있다
- **androidTest 에 AssertJ 가 없어 raw `assert()` 를 쓰고 있다.** CLAUDE.md 8.1 은 계측 테스트에도
  AssertJ 를 권하지만 `androidTestImplementation` 에 빠져 있다 (의존성 추가는 사용자 확인 필요)
- ~~계측 테스트를 실행하지 못했다~~ → **해결됨.** 에뮬레이터(`Medium_Phone_API_36`)에서 33개 통과
- ~~`BasketViewModel` 이 `CartRepository` 싱글턴을 직접 참조한다~~ → **해결됨** (지금까지 한 일 5번)

## 4단계 준비 (2026-09-01)

미션 원문(1~4단계)을 사용자가 다시 공유했다. 미션 원문은 **RecyclerView + Activity + XML** 전제로
쓰여 있지만, 이 프로젝트는 1단계 때부터 **Jetpack Compose + Navigation Compose + 단일 Activity** 로
가기로 이미 정한 상태다(위 결정 사항들 참고). 4단계도 이 전제를 유지한다 — **XML 로 새로 만들지 않고
Compose 로 구현한다.** 아래는 미션 원문 요구사항을 이 프로젝트 아키텍처 기준으로 재정리한 것이다.

### 미션 원문 요구사항 vs 이 프로젝트 상태

| 요구사항 | 상태 |
|---|---|
| 최근 본 상품 목록 상단 섹션 (최대 10개) | ✅ 완료 (브랜치 1·2) |
| 상품 상세에 마지막으로 본 상품 1개 노출 (자기 자신 제외) | ✅ 완료 (브랜치 3) |
| 마지막으로 본 상품 화면에서 뒤로가기 → 상품 목록 | ✅ 완료 (브랜치 3) |
| 앱 재시작해도 최근 본 상품·장바구니 유지 (Room) | ✅ 완료 (브랜치 7) |
| 상품 목록 HTTP Client 구현 (OkHttp) | ✅ 완료 (브랜치 4·5) |
| MockWebServer 로 테스트 환경 구성 | ✅ 완료 (브랜치 6) |
| (힌트) Activity Result API | 해당 없음 — 단일 Activity + Compose Navigation 이라 애초에 안 씀 (3단계와 동일 이유) |
| (힌트) Intent Flag 로 뒤로가기 제어 | **Compose Navigation 등가물로 대체.** `navigate(popUpTo=...)` 로 백스택 정리 (아래 결정 사항 8번) |

### 이번에 새로 생긴 조건

- **서버는 로컬로 준비한다.** 원격 배포가 아니라 개발 PC에서 직접 띄우는 것을 전제로 함
- **로컬 서버 구성 방식은 그릴링으로 확정했다.** Docker 는 안 쓰고 Ktor 로 간다 (아래 결정 사항 2번)

### 7. 4단계 설계 결정 (2026-09-01, 그릴링으로 확정)

`/grill-me` 로 4단계의 애매한 지점을 정리했다. 아래로 확정.

| # | 쟁점 | 결정 |
|---|---|---|
| 1 | Room 도입에 따라 코루틴을 지금 도입할지 | **보류 유지.** Room/네트워크 모두 수동 스레드 처리(`Executor`)로 감싼다. 코루틴 도입은 계속 미룬다 |
| 2 | 로컬 서버 스택 | **Ktor**(Kotlin). 이 PC에 Docker 는 없고 Node(v24) 는 있지만, 새 언어/새 도구 없이 기존 JDK·Gradle 로 바로 시작 가능해서 선택 |
| 3 | 서버 코드 위치 | 이 저장소 **같은 곳에 별도 최상위 폴더**(예: `server/`)로 둔다. 별도 저장소로 안 쪼갠다 |
| 4 | 안드로이드 HTTP 클라이언트 | **Retrofit + kotlinx.serialization 컨버터.** 단, 코루틴을 안 쓰므로 `suspend fun` 대신 `Call<T>.enqueue(callback)` 방식으로 쓴다 |
| 5 | HTTP Client 적용 범위 | **상품 목록 조회(`GET /products`)만** 서버화. 상품 상세는 목록 조회 때 받은 `Product` 를 재사용하고 별도 네트워크 호출을 하지 않는다 |
| 6 | Room 적용 범위 | **최근 본 상품 + 장바구니 둘 다.** 미션 요구사항이 "장바구니 데이터도 유지"를 명시하므로 `CartRepository` 도 영속 대상에 포함 |
| 7 | 최근 본 상품 기록 정책 | 상세 화면 **진입 즉시** 기록. 이미 있던 상품을 재방문하면 **중복 추가하지 않고 순서만 맨 앞으로 갱신** |
| 8 | 상세 → 목록 뒤로가기 | "마지막으로 본 상품" 카드로 진입한 상세에서는 `navigate(...) { popUpTo(ROUTE_PRODUCT_LIST) }` 로 백스택을 정리해, 뒤로가기 한 번에 항상 상품 목록으로 가게 한다 |
| 9 | 개발 중 테스트 환경 | **에뮬레이터만.** `baseUrl` 은 `http://10.0.2.2:PORT/` 고정. 실기기 테스트는 필요해지면 나중에 추가 |
| 10 | Room 을 코루틴 없이 접근하는 방식 | **기존 인메모리 저장소(`CartRepository`, 신설 최근본상품 저장소)는 그대로 두고**, Room 은 저장/복원 전용 얇은 뒷단으로만 붙인다. 변경이 생기면 백그라운드 `Executor` 에 "DB 에 써줘"만 던지고 결과를 기다리지 않는다(fire-and-forget). 앱 시작 시 한 번만 같은 스레드에서 DB 를 읽어 인메모리 저장소를 채운다 |
| 11 | (2026-09-02) pen 에 "최근 본 상품"/"마지막으로 본 상품"의 정의가 2개(재사용 컴포넌트 `RecentItem`/`LastViewed` vs 실제 화면 `buA3e`/`zDK6R`/`kZ4dp` 인스턴스)라 모순 | **실제 화면 인스턴스 기준.** 컴포넌트 쪽(사진만 있고 이름 없음 / 회색 배경에 이름만 강조)은 미완성 초안으로 보이고, 실제 화면 쪽이 사진+이름 또는 라벨+상품명을 갖춘 완성된 형태라 이쪽을 따른다 (아래 "4단계 pen 스펙" 참고) |

**결정 1번의 의미**: 지금까지 미뤄온 코루틴 도입을 4단계에서도 계속 미룬다는 뜻이다. 대신 Room·Retrofit
접근은 전부 수동 스레드 전환(`Executor`, 콜백)으로 처리한다. 나중에 코루틴을 도입하면 이 부분들을
`suspend`/`Flow` 로 한꺼번에 정리하면 된다.

**결정 10번의 의미**: 1~3단계에서 이미 완성된 `CartRepository`/리스너 패턴을 건드리지 않는다. Room 은
"가끔 조용히 저장·복원해주는 뒷단"으로만 붙어서, 기존 코드 변경 범위를 최소화한다.

### 4단계에서 새로 필요한 것 (승인 필요 — CLAUDE.md "새 의존성은 먼저 물어본다")

- `androidx.room:room-runtime` / `room-ktx` / `room-compiler`(KSP) — 최근 본 상품·장바구니 영속화 (결정 6, 10번)
- `com.squareup.retrofit2:retrofit` + `converter-kotlinx-serialization` (+ `org.jetbrains.kotlinx:kotlinx-serialization-json`) — 상품 목록 HTTP Client (결정 4번)
- 테스트: `com.squareup.okhttp3:mockwebserver`
- 서버 쪽(`server/` 폴더, Ktor 프로젝트)은 이 안드로이드 앱의 `libs.versions.toml` 범위 밖 — 별도 Gradle 설정 필요 (결정 2, 3번)

### 4단계 pen 스펙 (측정값, 결정 11번 — 실제 화면 인스턴스 기준)

**상품 목록 - 최근 본 상품**(`buA3e`/`zDK6R`) — TopBar 바로 아래, 그리드 위에 위치

- 섹션 제목 "최근 본 상품": (20,76), 16sp **Bold** `black`
- 아이템: 98×98 사진(cornerRadius 없음) + 사진 아래 상품명, 아이템 간격 8dp(첫 아이템 x=20 기준 106 간격)
  - 상품명: 12sp **Bold** `#333333`(신규 색상 `gray_3` 필요), 사진과 4dp 간격, 1줄
- 4번째 아이템이 프레임 폭(360)을 넘어 잘려 보임 → **가로 스크롤** 목록(`LazyRow`)임을 뜻함
- 섹션 아래 구분선: 두께 7dp, 색상 `#EBEBEB`(신규 색상 `gray_divider` 필요), 전체 폭
- 최근 본 상품이 없으면 섹션 전체를 그리지 않는다 (별도 pen 화면 없음 — 자연스러운 추론)

**상품 상세 - 마지막으로 본 상품**(`kZ4dp`) — 가격/수량 행과 "장바구니 담기" 버튼 사이

- 카드: 324×80(상세 화면 좌우 패딩 18dp 안쪽 폭과 동일), cornerRadius 4, 흰 배경, `gray_border` 1px 테두리
- 라벨 "마지막으로 본 상품": 카드 안 (18,16), 12sp **Bold** `primary`
- 상품명: 카드 안 (18,40), 18sp Regular `gray_5`
- 가격/수량 행(y=495~537) 과 카드(y=557) 사이 간격 20dp
- 마지막으로 본 상품이 없으면(자기 자신 제외 후 없음) 카드를 그리지 않는다

### 4단계 브랜치 1 완료 (2026-09-02)

- `recentlyviewed/model/RecentlyViewedProducts.kt`: 일급 컬렉션. `viewed()`(최대 10개, 중복 시 맨 앞으로 재정렬), `latestExcluding()`
- `recentlyviewed/model/RecentlyViewedRepository.kt`: 인메모리 저장소, `CartRepository` 와 동일한 변경 리스너 패턴
- `ShoppingBasketApplication` 에 `recentlyViewedRepository` 보관 (`cartRepository` 와 동일하게 프레임워크 계층 예외)
- `ShoppingBasketApplicationTest` 에 `sharesSingleRecentlyViewedRepository` 계측 테스트 추가 (`sharesSingleCartRepository` 와 동일 패턴)
- 단위 테스트: `RecentlyViewedProductsTest`(5개), `RecentlyViewedRepositoryTest`(4개) — 전부 통과
- 확인 완료: `assembleDebug` 빌드 성공, `testDebugUnitTest` 전체 통과(신규 테스트 포함 무실패), `connectedAndroidTest` 전체 52개 통과(`sharesSingleRecentlyViewedRepository` 포함), 에뮬레이터에 설치 후 실행해 크래시 없음(logcat 확인) — UI 변경이 없는 브랜치라 화면 자체는 기존과 동일

### 4단계 브랜치 2 완료 (2026-09-02) — `feature/productlist-recentlyviewed-ui`

- `colors.xml`: `gray_3`(`#FF333333`), `gray_divider`(`#FFEBEBEB`) 추가 (pen 스펙 신규 색상)
- `strings.xml`: `recently_viewed_section_title`("최근 본 상품") 추가
- `ProductListUiState`: `recentlyViewed: List<Product>` 필드 추가 (기본값 빈 목록)
- `ProductListViewModel`: `RecentlyViewedRepository` 의존성 추가(기본값 있는 파라미터라 기존 테스트는 그대로 통과), 변경 리스너 구독으로 다른 화면에서 최근 본 상품이 바뀌면 목록 화면도 갱신(`refreshCartState` → `refreshState` 로 통합해 재사용)
- `ShoppingBasketApplication`/`MainActivity`: `CreationExtras.recentlyViewedRepository()` 확장 함수 추가, `ProductListViewModel` 생성 시 주입
- `ProductListScreen.kt`: `RecentlyViewedSection`(제목 + `LazyRow`) / `RecentlyViewedItem`(98×98 이미지 + 12sp Bold `gray_3` 이름) 컴포저블 추가, 목록이 비어 있으면 섹션 자체를 그리지 않음, 아래에 7dp `gray_divider` 구분선. 항목 클릭 시 기존 `onProductClick` 재사용해 상세로 이동
  - 섹션 제목-상단 간격(pen 미기재, 콘텐츠 패딩 20dp 값과 통일해 20dp로 결정) 등 pen에 없는 세부 여백은 기존 화면의 20dp/12dp 패딩 체계에 맞춰 정함
- 단위 테스트: `ProductListUiStateTest`(신규 1개), `ProductListViewModelTest`(신규 2개) — 전부 통과
- 계측 테스트: `ProductListScreenTest`(신규 3개: 빈 목록 시 섹션 미표시, 목록 있을 때 섹션 표시, 항목 클릭 콜백) — 전부 통과
- 확인 완료: `assembleDebug` 빌드 성공, `testDebugUnitTest` 전체 통과, `connectedAndroidTest` 전체 55개 통과(신규 3개 포함, 0 실패), 에뮬레이터 설치 후 실행해 크래시 없음(logcat, 프로세스 생존 확인) — 최근 본 상품 저장소가 아직 비어 있어(기록은 3번 브랜치에서 추가) 섹션은 정상적으로 숨겨진 상태로 렌더링됨, 데이터가 있을 때의 렌더링은 계측 테스트로 검증

### 4단계 브랜치 3 완료 (2026-09-02) — `feature/productdetail-recentlyviewed`

- `ProductDetailUiState`: `lastViewedProduct: Product?` 필드 추가 (기본값 없음, 자기 자신 제외 후 없으면 null)
- `ProductDetailViewModel`: `RecentlyViewedRepository` 의존성 추가(기본값 있는 파라미터). 진입 즉시 `recordViewed()` 로 기록하고, `latestExcluding()` 로 자기 자신을 제외한 마지막 상품을 상태에 담음
- `ProductDetailScreen.kt`: 가격/수량 행과 "장바구니 담기" 버튼 사이에 `LastViewedSection`/`LastViewedProductCard` 컴포저블 추가 (fillMaxWidth × 80dp, cornerRadius 4, `gray_border` 1px, 라벨 12sp Bold `primary` + 상품명 18sp `gray_5`), 상품 없으면 섹션 자체를 그리지 않음. 카드 클릭 시 `onLastViewedClick(key)` 호출
  - 라벨-상품명 간 8dp 스페이서로 pen의 (18,16)/(18,40) 상대 좌표 근사(텍스트 라인 높이 포함 시 목표 24dp 간격에 근접) — 기존 화면들의 스페이서 기반 근사 패턴과 동일
  - Compose `@Preview` 2개 추가(마지막 본 상품 있음/없음)로 에뮬레이터 없이도 레이아웃 확인 가능하게 함
- `MainActivity.kt`: `ProductDetailViewModel` 생성 시 `recentlyViewedRepository()` 주입, `onLastViewedClick` 에서 `navigate("productDetail/$id") { popUpTo(ROUTE_PRODUCT_LIST) }` 사용해 뒤로가기 시 항상 목록으로 이동(결정 8번)
- `RecentlyViewedRepository`: 계측 테스트 간 Application 수명 상태 격리를 위한 `clear()` 추가 (CartRepository 의 remove 패턴과 동일 목적)
- `MainActivityTest`: `@Before` 에서 `recentlyViewedRepository.clear()` 도 함께 호출하도록 수정 — 브랜치 3 도입 전에는 최근 본 상품이 없어 드러나지 않았던 테스트 간 상태 누수(같은 상품명이 "최근 본 상품" 섹션과 그리드에 동시에 노출되어 `onNodeWithText` 가 노드 2개를 찾는 문제)가 발견되어 함께 수정
- 단위 테스트: `ProductDetailUiStateTest`(신규 1개), `ProductDetailViewModelTest`(신규 3개: 기록 확인/최초 방문 시 null/자기 자신 제외), `RecentlyViewedRepositoryTest`(신규 1개: `clear()`) — 전부 통과
- 계측 테스트: `ProductDetailScreenTest`(신규 3개: 카드 없음/있음/클릭 콜백) — 전부 통과
- 확인 완료: `assembleDebug` 빌드 성공, `testDebugUnitTest` 전체 통과, `connectedAndroidTest` 전체 58개 통과(신규 4개 포함, 0 실패). 에뮬레이터에 실제 설치 후 상품 A→B 순서로 상세 진입 시 "마지막으로 본 상품" 카드가 자기 자신을 제외하고 정확히 노출됨을 스크린샷으로 확인, 카드 클릭 시 해당 상품 상세로 이동 후 뒤로가기 한 번에 목록 화면으로 바로 돌아가는 `popUpTo` 동작도 실기기에서 확인, logcat 에 FATAL EXCEPTION 없음

### 4단계 브랜치 4 완료 (2026-09-02) — `chore/server-ktor-setup`

- 저장소 최상위에 안드로이드 앱과 완전히 별개인 Gradle 프로젝트 `server/` 신설(결정 2, 3번). 루트 `settings.gradle.kts`에 `include` 하지 않고, 루트의 `gradle-wrapper.jar`/`gradlew`만 복사해 재사용 — `libs.versions.toml` 범위 밖이라는 원칙대로 의존성 버전은 `server/build.gradle.kts`에서 직접 관리
  - `kotlin("jvm") 2.0.21`(앱과 동일 버전) + `application` 플러그인 + Ktor 2.3.12(`ktor-server-core`/`ktor-server-netty`/`ktor-server-content-negotiation`/`ktor-serialization-kotlinx-json`) + `logback-classic`
  - JBR(JDK 21)로 빌드하므로 `jvmToolchain(11)` 지정은 넣지 않음(넣으면 로컬에 11 툴체인이 없어 다운로드 설정 없이는 빌드 실패) — 실행 중인 JVM을 그대로 사용
- `Application.kt`: `embeddedServer(Netty, port = 8080, host = "0.0.0.0")`로 기동, `GET /products` 라우트 하나만 제공(결정 5번 — 목록 조회만 서버화, 상세는 캐시된 데이터 재사용이라 서버에 별도 엔드포인트 불필요)
- `MockProducts.kt`: 안드로이드 `ProductRepository`의 목업 생성 규칙(모양 5종 × 용량 7종 × 가격 2종, `p1`~`p50`)을 그대로 복제한 `ProductResponse`(id/name/price/imageUrl) 50개 반환 — 두 코드가 물리적으로 분리돼 있어 규칙이 어긋나면 5단계(Retrofit 연동)에서 화면이 달라지므로, 값 일치 여부를 테스트로 고정
- 테스트: `ApplicationTest.kt` — Ktor `testApplication`으로 `/products` 호출, 200 응답·50개 개수·`p1`(`PET보틀-밀크티병(300ml)`, 12,000원) 값까지 검증
- 확인 완료
  1. **빌드 성공** — `server` 디렉터리에서 `./gradlew test` BUILD SUCCESSFUL(컴파일 오류 0, 신설 테스트 통과)
  2. **실제 실행** — `./gradlew run`으로 실제 기동 후 `curl http://127.0.0.1:8080/products`로 응답 확인: 200 OK, 50개, `p1` 값이 안드로이드 목업과 완전히 일치. 확인 후 프로세스 정상 종료(포트 8080 해제 확인)
  3. **문서 검증** — 이 섹션 자체가 검증 기록. 안드로이드 앱(`app/`)은 이번 브랜치에서 파일 변경이 없어 기존 `assembleDebug`/테스트 결과에 영향 없음(`git status`로 `app/` 무변경 확인)
- 에뮬레이터 연동(`baseUrl = http://10.0.2.2:8080/`)은 5단계(`feature/productlist-http-client`, Retrofit 도입)에서 실제로 연결한다. 이번 브랜치는 서버 단독 기동·응답 검증까지가 범위

### 4단계 브랜치 5 완료 (2026-09-02) — `feature/productlist-http-client`

- `libs.versions.toml`에 Retrofit 2.11.0 + `converter-kotlinx-serialization` + `kotlinx-serialization-json` 1.7.3 추가. Retrofit 은 OkHttp 위에서 동작하므로 `okhttp` 도 함께 명시(승인받은 "Retrofit"에 포함된 필수 전이 의존성, 별도 검토 대상 아님). `app/build.gradle.kts`에 `kotlin.plugin.serialization` 플러그인 추가
- `ProductApi`(신규): `@GET("products") fun fetchProducts(): Call<List<ProductResponse>>` — 코루틴을 아직 안 쓰므로 결정 4번대로 `Call.enqueue` 방식. `baseUrl = http://10.0.2.2:8080/`(결정 9번)
- `ProductResponse`(신규): 서버 DTO. UiState 와 같은 이유로 OC 7·9번 예외로 두고(JSON 구조를 그대로 표현하는 게 목적), `toProduct()` 확장 함수로 도메인 `Product` 변환을 즉시 끝낸다
- `ProductRepository`: 로컬 목업 생성 로직을 전부 제거하고 `productApi.fetchProducts().enqueue(...)`로 서버에서 받아 캐시하는 방식으로 전환. `findAll/findPage/hasPageAfter/findById`는 이 캐시를 읽기만 한다. 실패 시(서버 미기동 등) 캐시를 비운 채 로딩만 끝낸다 — 별도 에러 UI·재시도 버튼 없음(사용자 확인, 5단계 확정 사항)
- 상세 화면이 목록의 캐시를 재사용하려면(결정 5번) 같은 `ProductRepository` 인스턴스를 봐야 하므로, `ShoppingBasketApplication`에 `productRepository`를 추가하고 `MainActivity`에서 두 화면 모두에 공용으로 주입(장바구니/최근 본 상품과 동일한 패턴)
- `ProductListUiState`에 `isLoading` 추가, `ProductListViewModel`은 생성 시 로딩 상태로 시작해 `fetchProducts` 콜백에서 로딩을 끝낸다. `ProductListScreen`은 로딩 중엔 그리드 대신 `CircularProgressIndicator`만 보여준다(Figma에 없는 상태라 사용자에게 확인 후 최소 형태로 결정)
- **네트워크 보안 설정 추가**(예상 밖 이슈): 안드로이드는 targetSdk 28 이상에서 평문 HTTP 를 기본 차단한다. `app/src/debug/AndroidManifest.xml` + `app/src/debug/res/xml/network_security_config.xml`로 **디버그 빌드에서만** `10.0.2.2` 로의 cleartext 통신을 허용 — 릴리스 빌드는 영향받지 않는다
- 테스트
  - JVM: `ProductRepositoryTest`를 fetch/캐시 흐름 기준으로 재작성, `ProductResponseTest`(DTO→도메인 변환) 신규. `app/src/test/.../common/fixture/FakeProductApi.kt`에 `retrofit2.Call`을 직접 구현한 가짜(`FakeProductApi`는 `enqueue` 즉시 콜백 실행, `ManualProductApi`는 `completeWith()` 호출 전까지 대기)를 만들어 MockWebServer 없이 저장소·뷰모델을 동기적으로 검증(MockWebServer 도입은 6단계 범위)
  - `ProductListViewModelTest`/`ProductDetailViewModelTest`/`CartFlowTest`: 기본 `ProductRepository()`(실네트워크) 대신 미리 채워진 `fakeProductRepository()`를 명시적으로 주입하도록 전면 수정, `ProductListViewModelTest`에 로딩 상태 전환 테스트 1건 추가
  - androidTest: `MainActivityTest`의 첫 상품명을 더 이상 앱에서 계산할 수 없어(생성 규칙이 서버로 이동) 서버 값과 동일한 문자열로 하드코딩하고, 상품 목록이 뜰 때까지 `waitUntil`로 대기하는 `@Before` 추가 — **이 계측 테스트는 이제 로컬 서버가 떠 있어야 통과한다**(`server` 디렉터리에서 `./gradlew run`). `ShoppingBasketApplicationTest`는 장바구니 공유 테스트가 더 이상 `ProductRepository()`에 의존하지 않도록 상품을 직접 생성하게 바꾸고, `productRepository` 단일 인스턴스 테스트 추가. `ProductListScreenTest`에 로딩 인디케이터 노출/그리드 숨김 테스트 2건 추가
- 확인 완료
  1. **빌드 성공** — `assembleDebug`, `testDebugUnitTest`(신규/수정 테스트 포함 전체 통과) 모두 BUILD SUCCESSFUL
  2. **실제 실행** — `server`에서 `./gradlew run`으로 서버를 띄운 상태로 에뮬레이터에 설치해 실행: 상품 목록이 서버 응답(p1 `PET보틀-밀크티병(300ml)` 12,000원 등)으로 정상 렌더링됨을 스크린샷으로 확인, 상품 클릭 시 상세 화면에 같은 상품이 즉시 표시됨(별도 네트워크 호출 없이 캐시 재사용) 확인. `connectedDebugAndroidTest` 61개 전체 통과. 확인 후 서버 프로세스 종료
  3. **문서 검증** — 이 섹션이 검증 기록. 5단계 표에 적힌 범위(Retrofit 전환, `baseUrl` 연결, 상세 캐시 재사용)를 모두 충족
- 다음 6단계(`test/productlist-mockwebserver`)에서 MockWebServer 로 HTTP·직렬화 계층까지 검증한다. 이번 브랜치의 `FakeProductApi`류 수동 페이크는 저장소 로직(페이징/캐시/콜백)만 검증하고 실제 JSON 파싱 경로는 타지 않는다는 한계가 있다

**작업 방식(변하지 않는 원칙)**
- 한 번에 한 단계만 진행하고 사용자 검토를 받는다
- 단계가 끝나면 커밋 메시지 초안을 제안한다 (Claude는 직접 commit/push 하지 않음)
- 화면 작업 전 Pencil 디자인을 먼저 확인한다
- 한 브랜치에는 한 기능만 담는다. 계층(ViewModel/View)이 아니라 기능 단위로 자른다
- 빌드/테스트는 `.\gradlew testDebugUnitTest connectedDebugAndroidTest` 로 확인한다
  (`.\gradlew test --tests` 는 Android 에서 동작하지 않는다)

### 4단계 브랜치 계획 (2026-09-02)

3단계와 같은 순서 원칙(도메인 → 공용 뷰 → 화면별 → 인프라)으로 7개 브랜치를 잡는다.
앞 3개는 새 의존성이 필요 없어 바로 착수 가능하고, 뒤 4개는 결정 사항 269~274번에 적힌
새 의존성(Room / Retrofit / kotlinx.serialization / MockWebServer / Ktor)이 필요해 **착수 전 승인 필요**.

| # | 브랜치 | 내용 | 새 의존성 |
|---|---|---|---|
| 1 | `feature/recentlyviewed-model` | `RecentlyViewedProducts` 일급 컬렉션(최대 10개, 진입 즉시 추가·기존 항목 재방문 시 중복 없이 맨 앞으로 이동) + `RecentlyViewedRepository`(인메모리, `CartRepository` 와 같은 변경 리스너 패턴), `ShoppingBasketApplication` 에 보관 | 없음 |
| 2 | `feature/productlist-recentlyviewed-ui` | 상품 목록 상단 "최근 본 상품" 섹션. pen 4단계 프레임 확인 후 착수(레이아웃 미확인) | 없음 |
| 3 | `feature/productdetail-recentlyviewed` | 상품 상세에 "마지막으로 본 상품" 1개(자기 자신 제외) 노출, 진입 즉시 기록, 그 카드로 진입한 상세는 `navigate(...) { popUpTo(ROUTE_PRODUCT_LIST) }` 로 뒤로가기 시 항상 목록으로 이동(결정 8번) | 없음 |
| 4 | `chore/server-ktor-setup` | 저장소 최상위 `server/` 폴더에 별도 Ktor 프로젝트 생성, `GET /products` 가 현재 목업 50개와 동일한 데이터를 반환 | Ktor (서버 전용, 별도 Gradle 설정) |
| 5 | `feature/productlist-http-client` | `libs.versions.toml` 에 Retrofit + `converter-kotlinx-serialization` 추가, `ProductRepository` 를 `Call<T>.enqueue(callback)` 방식으로 `GET /products` 호출하게 전환(상세는 캐시된 `Product` 재사용, 결정 5번), `baseUrl = http://10.0.2.2:PORT/` | Retrofit, kotlinx-serialization-json |
| 6 | `test/productlist-mockwebserver` | `androidx-junit`/`test` 양쪽에 MockWebServer 로 `GET /products` 응답을 흉내 낸 테스트 추가 | okhttp3 mockwebserver |
| 7 | `feature/persistence-room` | Room 추가(runtime/ktx/compiler-KSP), 최근 본 상품·장바구니를 저장/복원하는 얇은 뒷단(`Executor` fire-and-forget 쓰기 + 앱 시작 시 1회 읽기, 결정 10번). 기존 `CartRepository`/`RecentlyViewedRepository` 는 그대로 둔다 | Room |

- 4·5·6·7번은 각 브랜치 착수 직전에 그때그때 새 의존성 추가를 사용자에게 확인받는다(2026-09-02 확정).
- 2·3번은 착수 직전 Pencil 4단계 프레임(레이아웃·간격·색)을 확인하고 세부 값을 이 문서에 채운 뒤 코드를 쓴다.

**1번(`feature/recentlyviewed-model`) 완료 (2026-09-02)**

- `recentlyviewed/model/RecentlyViewedProducts`(일급 컬렉션, 최대 10개·재방문 시 중복 없이 맨 앞 이동·`latestExcluding`),
  `RecentlyViewedRepository`(인메모리 + 변경 리스너, `CartRepository` 와 동일 구조)
- `ShoppingBasketApplication` 에 `recentlyViewedRepository` 추가
- 단위 테스트(JUnit5+AssertJ) 10개 신설 + 전체 unit 테스트 통과, 계측 테스트 52개(기존 51 + 신설 1) 전부 통과(`Medium_Phone(AVD) - 16`)
- 아직 어느 화면에서도 `recordViewed`/`latestExcluding` 을 호출하지 않는다(2·3번에서 연결 예정) — 지금은 죽은 코드처럼 보이지만 의도된 것

**6번(`test/productlist-mockwebserver`) 완료 (2026-09-03)**

- `okhttp3:mockwebserver` 추가(`libs.versions.toml`, `testImplementation`). `ProductApi.create()` 가 `baseUrl` 을 인자로 받도록 변경(기본값은 기존 운영 주소)해 테스트에서 MockWebServer 주소를 주입
- `ProductApiTest` 신설(`app/src/test`): 실제 Retrofit/OkHttp/kotlinx.serialization 경로로 JSON 파싱, `GET /products` 요청 경로, 서버 오류(500) 응답 처리를 검증
- 문서 계획엔 "androidx-junit/test 양쪽에" 라고 적혀 있었지만, `ProductApi`/`ProductRepositoryImpl` 은 Data 계층이라 CLAUDE.md 8.2 표 기준(Data 계층 → `app/src/test`)에 따라 **`test` 쪽에만** 추가했다. 기존 `FakeProductApi` 는 그대로 두고(저장소 로직 검증), 이번 테스트는 그게 못 타던 실제 HTTP·JSON 경로만 메꿈
- 확인 완료: `assembleDebug`/`testDebugUnitTest`(신규 3개 포함 전체 통과), `compileDebugAndroidTestKotlin` BUILD SUCCESSFUL(영향 없음)

**7번(`feature/persistence-room`) 완료 (2026-09-03)**

- Room 추가(`room-runtime` + `room-compiler`(KSP), `com.google.devtools.ksp` 플러그인 신설). `room-ktx` 는 코루틴 미사용(결정 1번)이라 제외
- 값 객체에 영속화 전용 원시값 접근자 최소 추가: `Money.toRawValue()`, `Quantity.toRawValue()`, `Product.unitPrice()`(단가 `Money` 그대로 반환), `CartItem.unitPrice()`(위임). 기존 `CartItem.currentQuantity()` 와 같은 성격의 "표현용 메서드" 예외로 취급(OC9)
- `basket/data/local/`: `CartItemEntity`, `CartItemDao`(`findAll`/`replaceAll`/`clear`/`insertAll`, 전부 동기), `CartPersistence`(저장은 `Executor` 로 fire-and-forget, 복원은 호출 스레드에서 동기)
- `recentlyviewed/data/local/`: 위와 동일 구조의 `RecentlyViewedProductEntity`/`RecentlyViewedProductDao`/`RecentlyViewedPersistence`
- `common/data/local/ShoppingBasketDatabase`: 두 Dao 를 묶는 Room DB. `create()` 에서 `allowMainThreadQueries()` 사용 — 앱 시작 시 1회 동기 읽기만 메인 스레드에서 허용(결정 10번). ponytail 주석으로 데이터가 커지면 백그라운드로 옮기라고 표시해둠
- `CartRepository`/`RecentlyViewedRepository` 에 `restore()` 추가(변경 리스너에 알리지 않음 — 초기값 채우기이지 "변경"이 아님)
- `ShoppingBasketApplication.onCreate()` 에서 DB 생성 → 두 저장소 복원 → 이후 변경 리스너로 fire-and-forget 저장을 연결. `CartRepository`/`RecentlyViewedRepository` 자체 코드는 건드리지 않음(결정 10번 그대로)
- 테스트: JVM(`app/src/test`) 은 `FakeCartItemDao`/`FakeRecentlyViewedProductDao`(인메모리 가짜, `common/fixture`)로 `CartPersistence`/`RecentlyViewedPersistence` 의 저장·복원·순서·덮어쓰기 로직 검증(동기 실행되도록 `Executor` 를 테스트에서 즉시 실행형으로 주입). androidTest 는 `Room.inMemoryDatabaseBuilder` 로 실제 SQLite 위에서 `CartItemDao`/`RecentlyViewedProductDao` 의 정렬·전체 교체 동작을 검증(FakeProductApi/MockWebServer 와 같은 분리 패턴, 6번 참고)
- 확인 완료
  1. **빌드 성공** — `assembleDebug`, `testDebugUnitTest`(신규 포함 전체 통과), `compileDebugAndroidTestKotlin` 모두 BUILD SUCCESSFUL
  2. **실제 실행** — `connectedDebugAndroidTest` 67개 전체 통과(신규 6개 포함). 추가로 실기기 흑백 상자 검증: 앱 실행 → 상품 상세 진입(최근 본 상품 기록) → 장바구니 담기 → `am force-stop` 으로 프로세스 강제 종료 → 기기의 `shopping_basket.db` 를 직접 pull 해 `cart_items`/`recently_viewed_products` 행이 정확한 값으로 저장된 것을 확인 → 앱 재실행 후 UI 덤프로 장바구니 배지 "1", "최근 본 상품" 섹션, 상품 타일의 수량 스테퍼가 전부 복원된 것을 확인. logcat 에 FATAL EXCEPTION 없음
  3. **문서 검증** — 이 섹션이 검증 기록. 7번 표에 적힌 범위(Room 추가, fire-and-forget 저장, 앱 시작 시 1회 복원, 기존 저장소 코드 불변)를 모두 충족
- 이것으로 4단계 미션 원문 요구사항("앱 재시작해도 최근 본 상품·장바구니 유지")이 모두 구현됨. 4단계 브랜치 계획 7개 전부 완료
