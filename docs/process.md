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

## 다음 단계

**다음 할 일: 3단계 기능 구현 (수량 조절).**

pen 기준 3단계는 상품 목록 / 상품 상세 / 장바구니에서 수량을 +- 로 조절하는 화면이다
(`상품 목록 - 수량 조절`, `상품 상세 - 수량 조절`, `장바구니 - 수량 조절`, 재사용 컴포넌트 `Stepper`).
2단계와 마찬가지로 **기능/프로그래밍 요구사항 미션 원문을 받아야** 추측 없이 시작할 수 있다
(CLAUDE.md 3번, 11번 규칙).

이미 있는 것: `common/model/Quantity`(1 이상 검증), `CartItems.added()` 의 수량 누적,
`CartItem.increasedBy()`. 3단계는 여기에 감소·직접 지정과 화면을 붙이는 작업이 될 가능성이 높다.

### 2단계에서 처리하지 않고 남긴 것

- **계측 테스트를 실행하지 못했다.** 에뮬레이터/기기가 없어 `assembleDebugAndroidTest`(컴파일)까지만
  확인했다. `.\gradlew connectedAndroidTest` 로 한 번 돌려봐야 한다
- **`BasketViewModel` 이 `CartRepository` 싱글턴을 직접 참조한다.** `ProductListViewModel` 은
  생성자 주입인데 일관성이 없고, 테스트마다 `clearCartRepository()` 로 전역 상태를 청소해야 한다
- **`common/fixture/ProductFixture.kt` 는 주석과 달리 `src/test` 에서만 쓸 수 있다.**
  androidTest 는 별도 소스셋이라 계측 테스트들이 각자 `Product(...)` 를 인라인으로 만들고 있다
- **androidTest 에 AssertJ 가 없어 raw `assert()` 를 쓰고 있다.** CLAUDE.md 8.1 은 계측 테스트에도
  AssertJ 를 권하지만 `androidTestImplementation` 에 빠져 있다 (의존성 추가는 사용자 확인 필요)

**작업 방식(변하지 않는 원칙)**
- 한 번에 한 단계만 진행하고 사용자 검토를 받는다
- 단계가 끝나면 커밋 메시지 초안을 제안한다 (Claude는 직접 commit/push 하지 않음)
- 화면 작업 전 Pencil 디자인을 먼저 확인한다
- 한 브랜치에는 한 기능만 담는다. 계층(ViewModel/View)이 아니라 기능 단위로 자른다
