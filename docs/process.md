# 진행 상황

이 문서는 "1단계~8단계" 미션 진행 상황을 기록한다. Pencil 디자인 원본은
`C:\Users\apf_temp_admin\Desktop\쇼핑\shoppingUi.pen` 이고, 각 화면 프레임에
`1단계` ~ `8단계` 라벨이 붙어 있다.

## 전체 단계 개요 (pen 디자인 기준)

| 단계 | 화면 | 코드 구현 | pen 디자인 |
|---|---|---|---|
| 1단계 | 상품 목록 / 상품 상세 / 장바구니 (기본 흐름) | ✅ 완료 | ✅ 완료 |
| 2단계 | 상품 목록 더보기(페이지네이션), 장바구니 페이징(5개 이하/초과/마지막 페이지) | ❌ 미착수 | ✅ 이미지·TopBar 정리 완료 |
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

## 다음 단계

**다음 할 일: 2단계 기능 구현.**

2단계는 디자인(pen)은 이미 정리돼 있지만, 실제 **기능/프로그래밍 요구사항 스펙**이 아직 없다.
1단계처럼 "🚀 2단계 - ... / 기능 요구 사항 / 프로그래밍 요구 사항 / 피그마 / 힌트" 형식의
미션 원문을 받아야 추측 없이 구현을 시작할 수 있다 (CLAUDE.md 3번, 11번 규칙).

2단계 화면 구성(pen 기준)으로 미뤄보면 다음 기능일 가능성이 높음 — 스펙 받으면 확인 필요:
- 상품 목록 더보기(스크롤/페이지네이션으로 상품 추가 로딩)
- 장바구니 페이징 (상품이 5개 이하일 때 / 5개 초과일 때 / 마지막 페이지)

**작업 방식(변하지 않는 원칙)**
- 한 번에 한 단계만 진행하고 사용자 검토를 받는다
- 단계가 끝나면 커밋 메시지 초안을 제안한다 (Claude는 직접 commit/push 하지 않음)
- 화면 작업 전 Pencil 디자인을 먼저 확인한다
