# CLAUDE.md

이 문서는 이 프로젝트에서 코드를 작성하거나 수정할 때 반드시 따라야 하는 규칙을 정의한다.

- **언어**: Kotlin
- **프로젝트 형태**: Android 앱 (Jetpack Compose, MVVM)
- **코드/네이밍**: 영어 / **주석·설명·문서**: 한국어

---

## 1. 필수 사항 (최우선 규칙)

0. **Figma 디자인을 자세히 분석한 후에 UI 작업을 진행할 것.** 추측으로 화면을 만들지 않는다.
1. **Figma의 dp 값을 참조할 것.** 단, 고정 픽셀로 박지 말고 휴대폰 화면 비율에 맞게 대응시킨다.
2. **색상은 Figma에서 쓰는 값과 동일하게 사용할 것.** `app/src/main/res/values/colors.xml` 에 정의하고 거기서만 참조한다.
3. **애매한 부분은 임의로 결정하지 말고 사용자에게 먼저 질문할 것.**
4. **MVVM 패턴에 맞게 패키지를 분리할 것.**
5. **테마(기능)별로 패키지를 나누고, 그 아래에 model / view / viewmodel 을 둘 것.** 필요하면 리팩터링을 진행한다.
6. **하나의 작업이 끝나면 거기서 멈추고 사용자의 검토를 받을 것.** 검토 후 사용자가 직접 git push 한다.
7. **모든 클래스는 테스트 코드를 가질 것** (`test` / `androidTest` 둘 다 대상). 단, private 클래스는 제외.

> 규칙 4·5는 원래 MVP로 시작했으나 사용자 결정에 따라 **MVVM** 으로 변경됨. `presenter` 패키지는 만들지 않는다.

---

## 2. 프로젝트 개요

- Android 앱 (Kotlin + **Jetpack Compose**), 단일 모듈 `:app`
- `namespace` / `applicationId`: `com.example.shoppingbasket`
- `compileSdk` / `targetSdk` 35, `minSdk` 24, JVM target 11
- 빌드: Gradle Kotlin DSL + 버전 카탈로그 (`gradle/libs.versions.toml`) — 의존성은 반드시 카탈로그에 먼저 추가하고 `libs.*` 로 참조한다
- 현재 상태: **Android Studio 기본 템플릿 그대로**. `MainActivity` + `Greeting` 컴포저블만 있고 실제 기능 코드는 아직 없음
- **아직 git 저장소가 아님** (`.git` 없음). 사용자가 push 예정이므로 `git init` 이 필요하면 먼저 물어본다

---

## 3. 아키텍처

### 3.1 기본 원칙

- **Google 권장 아키텍처(MVVM)** 를 기본 골격으로 유지한다.
- **Clean Architecture** 의 계층 분리 원칙을 적절히 결합한다. 단, 과도한 추상화는 피한다.
- 계층은 다음과 같이 나눈다.

| 계층 | 위치 | 역할 |
|---|---|---|
| **View (UI)** | `<기능>/view/` | Composable 화면·컴포넌트, Activity. 입출력(표시·이벤트 전달)만 담당 |
| **ViewModel** | `<기능>/viewmodel/` | 흐름 제어. UiState 를 만들고 View 이벤트를 받아 Domain 을 호출 |
| **Domain / Model** | `<기능>/model/` | 핵심 비즈니스 로직, Entity, Repository |

- **의존성 방향은 항상 안쪽(Domain)을 향한다.** Domain 은 View 를 모른다.
- ViewModel 은 Android/Compose 타입을 알지 않는다 (`Context`, `Composable`, `View` import 금지).
- 핵심 로직은 UI 없이 테스트 가능해야 한다.

### 3.2 UseCase 사용 기준

- **항상 UseCase 를 만들지 않는다.** 필요할 때만 도입한다.
  - 비즈니스 로직이 ViewModel 에 두기엔 복잡하거나 재사용되는 경우
  - 여러 도메인 객체를 조합해야 하는 경우
- 단순한 흐름이면 ViewModel 이 Domain 객체(Repository)를 직접 호출한다.

### 3.3 패키지 구조

기능(테마)명이 최상위에 오고, 그 아래에 계층을 둔다.

```
com.example.shoppingbasket
├── login/
│   ├── model/       LoginRepository, LoginUser, ... (데이터 소스 · 도메인 모델)
│   ├── view/        LoginActivity, LoginScreen.kt (@Composable), 하위 컴포넌트
│   └── viewmodel/   LoginViewModel, LoginUiState
├── basket/
│   ├── model/
│   ├── view/
│   └── viewmodel/
├── common/          base, util, ext 등 기능 간 공유 코드
└── ui/theme/        Color.kt, Theme.kt, Type.kt (Compose 테마)
```

새 화면을 추가할 때는 이 3단(model/view/viewmodel) 구조를 그대로 따른다. 기존 코드가 이 구조를 벗어나 있으면 리팩터링해서 맞춘다.

### 3.4 데이터 흐름

```
View(Composable) --이벤트--> ViewModel --호출--> Model(Repository/Domain)
View(Composable) <--UiState(StateFlow)-- ViewModel
```

- 상태는 `UiState` data class 하나로 모아 노출한다 (필드별 StateFlow 남발 금지)
- View 는 `collectAsStateWithLifecycle()` 로 구독한다
- Composable 은 상태를 직접 만들지 않고 받는다 (stateless 우선, hoisting)
- 일회성 이벤트(토스트, 화면 이동)는 상태가 아니라 별도 채널/이벤트로 처리한다

---

## 4. 객체지향 생활 체조 원칙

아래 9가지 원칙을 코드 전반에 적용한다.

> **적용 범위**: `model/`(Domain) 계층에는 **9원칙 전부 엄격 적용**한다.
> `viewmodel/` 의 `UiState` data class 와 `view/` 의 Composable 파라미터는 **원칙 7(인스턴스 변수 2개 이하)·9(프로퍼티 노출 금지)의 예외**로 허용한다. 나머지 원칙은 모든 계층에 적용한다.

### 4.1 한 메서드에 오직 한 단계의 들여쓰기만 한다

- 메서드 본문의 **들여쓰기 depth 는 1단계만** 허용한다.
- (전체 기준으로는 depth 2까지 현실적으로 허용하되, 메서드 하나하나는 한 단계를 지향한다. 깊어지면 **메서드를 분리**한다.)
- early return, `when`, 컬렉션 함수를 활용해 중첩을 없앤다.

```kotlin
// 나쁜 예 — depth 3
fun process(users: List<User>) {
    for (user in users) {
        if (user.isActive) {
            if (user.hasPermission) {   // 금지
                grantAccess(user)
            }
        }
    }
}

// 좋은 예 — 메서드 분리로 한 단계 유지
fun process(users: List<User>) {
    users.filter { user -> user.isActive }
        .forEach { activeUser -> grantIfPermitted(activeUser) }
}

private fun grantIfPermitted(user: User) {
    if (user.hasPermission) {
        grantAccess(user)
    }
}
```

### 4.2 else 예약어를 쓰지 않는다

- `else` 를 쓰지 않고 **early return / guard clause** 로 분기를 푼다.
- `when` 에서도 가능하면 `else` 대신 모든 경우를 명시한다. (sealed class 를 쓰면 자연스럽게 해결된다)

```kotlin
// 나쁜 예
fun grade(score: Int): String {
    return if (score >= 60) {
        "PASS"
    } else {
        "FAIL"
    }
}

// 좋은 예
fun grade(score: Int): String {
    if (score >= 60) {
        return "PASS"
    }
    return "FAIL"
}
```

### 4.3 모든 원시 값과 문자열을 포장한다

- `Int`, `String` 등 원시 타입을 그대로 노출하지 않고, **의미를 가진 클래스로 포장**한다.
- 검증 로직은 포장 클래스 안에 둔다.

```kotlin
// 나쁜 예
fun applyQuantity(quantity: Int) { ... }

// 좋은 예
@JvmInline
value class Quantity(private val value: Int) {
    init {
        require(value >= 1) { "수량은 1개 이상이어야 한다" }
    }
}
```

### 4.4 한 줄에 점을 하나만 찍는다

- 메서드 체이닝으로 객체 내부를 파고드는 것을 금지한다. (디미터 법칙)
- 단, **컬렉션 스트림(`filter`, `map` 등) 연산 체이닝은 예외**로 허용한다.
- **Compose 의 `Modifier` 체이닝도 예외**로 허용한다.

```kotlin
// 나쁜 예
val name = order.getCustomer().getProfile().getName()

// 좋은 예 — 객체에게 메시지를 보낸다
val name = order.customerName()
```

### 4.5 줄여 쓰지 않는다 (축약 금지)

- 변수/함수/클래스명에 **줄임말을 사용하지 않는다.** 의미가 전부 드러나는 완전한 단어를 쓴다.
- 단, 업계 통용 약어(`id`, `url`, `dp`, `ui` 등)는 예외로 허용한다.

```kotlin
// 나쁜 예
val usrCnt = 0
val resp = api.fetch()

// 좋은 예
val userCount = 0
val response = api.fetch()
```

### 4.6 모든 엔티티를 작게 유지한다

- 클래스와 메서드를 작게 유지한다.
- **메서드는 10라인을 넘지 않는다.** (중괄호 라인 제외, 본문 기준)
- **메서드는 한 가지 일만** 잘 하도록 구현한다.
- 클래스가 커지면 책임 단위로 분리한다.
- Composable 함수도 동일하다. 화면이 길어지면 하위 Composable 로 쪼갠다.

### 4.7 3개 이상의 인스턴스 변수를 가진 클래스를 쓰지 않는다

- 한 클래스의 인스턴스 변수(프로퍼티)는 **최대 2개**까지만 둔다.
- 늘어나면 관련된 값을 별도 객체로 묶어 응집도를 높인다.
- **예외**: `UiState` data class. 화면 상태를 하나로 모으는 것이 목적이므로 필드 수 제한을 적용하지 않는다.

### 4.8 일급 컬렉션을 쓴다

- 컬렉션을 가진 클래스는 **그 컬렉션 하나만** 인스턴스 변수로 가진다.
- 컬렉션을 다루는 로직(추가/필터/검증/계산)은 이 일급 컬렉션 안에 둔다.

```kotlin
// 좋은 예
class BasketItems(private val items: List<BasketItem>) {

    fun totalPrice(): Money {
        return items.fold(Money.zero()) { sum, item -> sum.plus(item.price()) }
    }

    fun added(item: BasketItem): BasketItems {
        return BasketItems(items + item)
    }
}
```

### 4.9 getter/setter/프로퍼티를 쓰지 않는다

- 단순히 값을 꺼내는 **getter/setter 를 노출하지 않는다.** 데이터를 꺼내 외부에서 처리하지 말고, **객체에게 일을 시킨다.** (Tell, Don't Ask)
- Domain 클래스는 `val`/`var` 프로퍼티를 외부로 공개하지 않고 `private` 으로 감춘 뒤 **행위 메서드**를 제공한다.
- 화면 출력을 위해 값이 꼭 필요한 경우에 한해, 표현용 메서드(예: `toDisplayValue()`)로 최소한만 노출한다.
- **예외**: `UiState` data class 의 프로퍼티. View 가 읽는 것이 목적이다.

```kotlin
// 나쁜 예 — 값을 꺼내 외부에서 판단
if (item.quantity > maxQuantity) { ... }

// 좋은 예 — 객체에게 묻는다
if (item.exceeds(maxQuantity)) { ... }
```

---

## 5. 명시적 표현 (애매함 금지)

### 5.1 `it` 사용 제한

- 람다의 암시적 인자 `it` 은 사용하지 않는다. 항상 **명시적인 파라미터 이름**을 붙인다.

```kotlin
// 나쁜 예
users.filter { it.isActive }

// 좋은 예
users.filter { user -> user.isActive }
```

### 5.2 판단이 애매한 부분 명시

- 동작이 한눈에 명확하지 않거나, 의도/예외 처리가 헷갈릴 수 있는 부분은 **한국어 주석 또는 명시적 코드**로 의도를 드러낸다.
- 매직 넘버, 암시적 null 처리, 분기의 의미 등은 이름이나 주석으로 분명히 한다.

```kotlin
// 나쁜 예 — 0이 무슨 의미인지 불명확
if (status == 0) { ... }

// 좋은 예
private const val STATUS_PENDING = 0
if (status == STATUS_PENDING) { ... }   // 대기 상태일 때만 처리
```

---

## 6. 객체지향 5원칙 (SOLID)

객체 지향 5원칙을 지키면서 프로그래밍한다.

- **SRP** (단일 책임의 원칙): 작성된 클래스는 하나의 기능만 가지며, 클래스가 제공하는 모든 서비스는 그 하나의 책임(변화의 축: axis of change)을 수행하는 데 집중되어 있어야 한다.
- **OCP** (개방 폐쇄의 원칙): 소프트웨어의 구성요소(컴포넌트, 클래스, 모듈, 함수)는 확장에는 열려있고, 변경에는 닫혀있어야 한다.
- **LSP** (리스코프 치환의 원칙): 서브 타입은 언제나 기반 타입으로 교체할 수 있어야 한다. 즉, 서브 타입은 언제나 기반 타입과 호환될 수 있어야 한다.
- **ISP** (인터페이스 분리의 원칙): 한 클래스는 자신이 사용하지 않는 인터페이스는 구현하지 말아야 한다.
- **DIP** (의존성 역전의 원칙): 구조적 디자인에서 발생하던 하위 레벨 모듈의 변경이 상위 레벨 모듈의 변경을 요구하는 위계관계를 끊는 의미의 역전 원칙이다.

---

## 7. UI 작업 규칙

- **Figma 먼저.** MCP `figma` 도구로 해당 프레임을 읽고(`get_design_context` / `get_screenshot` / `get_variable_defs`) 레이아웃·간격·타이포·색을 확인한 뒤 코드를 쓴다. Figma 프레임 URL 이 없으면 사용자에게 요청한다.
- **dp 값**: Figma 의 dp 를 그대로 쓰되 화면 비율에 대응시킨다.
  - 가로 크기가 화면 폭에 비례해야 하는 요소 → `fillMaxWidth(fraction)` / `weight()` / `Modifier.padding` 조합
  - 고정이 맞는 값(아이콘 크기, 코너 반경, 테두리 두께 등) → Figma dp 그대로
  - 반복되는 spacing/size 값은 하드코딩하지 말고 `common` 또는 theme 쪽 상수로 뺀다
- **색상**: Figma 의 hex 를 `res/values/colors.xml` 에 의미 있는 이름으로 추가하고, Compose 에서는 `ui/theme/Color.kt` 가 그 값을 참조하도록 연결한다. Composable 안에 `Color(0xFF...)` 리터럴을 직접 쓰지 않는다.
  - 현재 `colors.xml` 은 템플릿 기본값(purple/teal)만 있음 → Figma 색으로 교체·추가 필요
- 문자열은 `res/values/strings.xml` 에 넣는다 (하드코딩 금지)

---

## 8. 테스트

### 8.1 테스트 프레임워크

JVM 단위 테스트는 **JUnit5(Jupiter) + AssertJ**, 계측 테스트는 **JUnit4**(Android 제약)를 쓴다.

```kotlin
// app/build.gradle.kts
testImplementation(libs.junit.jupiter)
testImplementation(libs.assertj.core)
testImplementation(libs.kotlin.test)
testImplementation(libs.kotlinx.coroutines.test)

androidTestImplementation(libs.androidx.junit)          // JUnit4 — 계측 테스트는 JUnit5 미지원
androidTestImplementation(libs.androidx.ui.test.junit4)

tasks.withType<Test> { useJUnitPlatform() }
```

- 단언(assertion)은 **AssertJ** (`assertThat(...).isEqualTo(...)`) 스타일을 기본으로 사용한다. (계측 테스트 포함)
- 라이프사이클/애너테이션은 JVM 테스트에서 **JUnit5** 를 사용한다. (`@Test`, `@DisplayName`, `@BeforeEach`)
- 의존성은 반드시 `gradle/libs.versions.toml` 에 먼저 추가하고 `libs.*` 로 참조한다.

```kotlin
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class BasketItemsTest {

    @Test
    @DisplayName("장바구니 총액은 담긴 상품 가격의 합이다")
    fun totalPrice() {
        val items = BasketItems(listOf(item(1_000), item(2_000)))
        assertThat(items.totalPrice()).isEqualTo(Money(3_000))
    }
}
```

### 8.2 테스트 커버리지

- **모든 비(non-)private 클래스는 테스트가 있어야 한다.** 클래스 하나가 테스트 0개인 채로 커밋되지 않는다. (필수 사항 7번)
- 단, **순수 UI 로직(화면 출력·입력 자체)은 단위 테스트 대상에서 제외**하고 계측 테스트로 커버한다.
- 테스트 위치는 클래스 성격에 맞게 최소 1곳:

| 대상 | 위치 | 프레임워크 |
|---|---|---|
| ViewModel | `app/src/test/` | JUnit5 + AssertJ, 코루틴은 `runTest` + TestDispatcher |
| Model / Repository / UseCase / util | `app/src/test/` | JUnit5 + AssertJ |
| Activity, Composable Screen, UI 컴포넌트 | `app/src/androidTest/` | JUnit4 + `createAndroidComposeRule` / `createComposeRule` |

- 이름 규칙: `LoginViewModel` → `LoginViewModelTest`, `LoginScreen.kt` → `LoginScreenTest`
- 패키지 경로는 프로덕션 코드와 동일하게 미러링한다 (`com.example.shoppingbasket.login.viewmodel`)
- 양쪽 모두 의미 있는 검증이 가능한 클래스라면 둘 다 작성한다
- private 클래스/함수는 직접 테스트하지 않고 public 진입점을 통해 커버한다

### 8.3 핵심 로직과 UI 로직의 분리

- **핵심 로직(비즈니스 로직)** 과 **UI 로직(화면 표시·입력)** 을 반드시 분리한다.
- 핵심 로직은 Compose/Android 에 의존하지 않아야 하며, UI 없이 테스트 가능해야 한다.

```kotlin
// view/ — 계측 테스트로만 검증
@Composable
fun LoginScreen(uiState: LoginUiState, onSubmit: () -> Unit) { ... }

// model/ — 단위 테스트 필수
class LoginCredential(private val email: Email, private val password: Password) {
    fun isSubmittable(): Boolean { ... }
}
```

---

## 9. Git 전략 (git flow)

브랜치 모델은 **git flow** 를 따른다.

| 브랜치 | 역할 | 분기 원본 | 병합 대상 |
|---|---|---|---|
| `main` | 배포된 릴리스만. 직접 커밋 금지 | — | — |
| `develop` | 개발 통합 브랜치. 기본 작업 기준점 | `main` | — |
| `feature/*` | 기능 단위 개발 | `develop` | `develop` |
| `release/*` | 릴리스 준비(버전업, 마무리 QA) | `develop` | `main` + `develop` |
| `hotfix/*` | 배포본 긴급 수정 | `main` | `main` + `develop` |

### 9.1 브랜치 이름 규칙

`<타입>/<기능명>-<세부작업>` — 전부 **소문자 + 하이픈(kebab-case)**, 한글·공백·언더스코어 금지.

기능명은 패키지명과 일치시킨다 (`login`, `basket`, …). 이슈 번호가 있으면 `feature/12-login-ui` 처럼 앞에 붙인다.

이 프로젝트 기준 추천 브랜치명:

```
feature/login-ui              로그인 화면 Compose UI 구현
feature/login-viewmodel       LoginViewModel + UiState
feature/login-model           LoginRepository / 데이터 소스
feature/basket-list-ui        장바구니 목록 화면
feature/basket-viewmodel      장바구니 상태 관리
feature/design-tokens         Figma 색상·타이포를 colors.xml / Theme 에 반영
feature/common-base           공통 base 클래스·확장 함수
chore/test-dependencies       JUnit5 · AssertJ 등 테스트 라이브러리 추가
release/1.0.0
hotfix/login-crash
```

- 한 브랜치 = 한 기능 단위. UI 와 ViewModel 을 한 브랜치에 섞지 않는 쪽이 리뷰가 쉽다.
- 브랜치가 길어지면 `feature/login-ui` 아래에서 다시 쪼개지 말고 `develop` 에 먼저 병합한다.

### 9.2 커밋 메시지 규칙

**Conventional Commits** 형식, 타입은 영문 소문자 / 설명은 한글.

```
<타입>(<범위>): <요약 50자 이내, 마침표 없음, 명사형 종결>

- 무엇을 왜 바꿨는지 (선택, 72자 줄바꿈)
- 여러 항목이면 불릿으로
```

| 타입 | 용도 |
|---|---|
| `feat` | 새 기능 |
| `fix` | 버그 수정 |
| `ui` | Figma 반영 등 화면/스타일 변경 (기능 변화 없음) |
| `refactor` | 동작 변화 없는 구조 개선 |
| `test` | 테스트 추가·수정 |
| `chore` | 빌드·의존성·설정 |
| `docs` | 문서 |

**범위(scope)** 는 기능 패키지명을 쓴다: `login`, `basket`, `common`, `theme`.

기능 단위 커밋 메시지 예시:

```
feat(login): 로그인 화면 UI 및 입력 검증 구현

- Figma 로그인 프레임 기준으로 LoginScreen 구성
- 이메일/비밀번호 입력 상태를 LoginUiState 로 통합
- LoginViewModelTest, LoginScreenTest 추가
```

```
ui(theme): Figma 색상 팔레트를 colors.xml 에 반영
feat(basket): 장바구니 수량 증감 기능 추가
fix(login): 화면 회전 시 입력값 초기화되는 문제 수정
refactor(basket): 상품 목록을 일급 컬렉션 BasketItems 로 분리
test(basket): BasketItems 총액 계산 테스트 추가
chore: JUnit5, AssertJ 테스트 의존성 추가
```

### 9.3 운영 규칙

- **작업 브랜치를 만들 때마다 `develop` 최신 상태에서 분기한다.**
- 기능 단위 작업이 끝나면 Claude 가 **커밋 메시지 초안을 제안**하고 거기서 멈춘다. 실제 `commit` / `push` 는 사용자가 검토 후 직접 수행한다.
- `main` / `develop` 에 직접 커밋하지 않는다. 반드시 `feature/*` 를 거친다.
- 테스트가 없는 클래스가 포함된 상태로는 커밋 메시지를 제안하지 않는다 (필수 사항 7번).
- 예외적으로 Claude 가 직접 커밋하는 경우, 메시지 마지막에 `Co-Authored-By: Claude Opus 5 <noreply@anthropic.com>` 을 붙인다.

---

## 10. 빌드 / 실행 명령

Windows PowerShell 기준, 프로젝트 루트에서:

```powershell
.\gradlew assembleDebug          # 디버그 빌드
.\gradlew test                   # JVM 단위 테스트 (app/src/test)
.\gradlew connectedAndroidTest   # 계측 테스트 (에뮬레이터/기기 필요)
.\gradlew lint                   # 린트
.\gradlew clean
```

---

## 11. 작업 진행 방식

- 한 번에 하나의 작업만 끝내고 멈춘다. 여러 화면을 한꺼번에 만들지 않는다.
- 작업 완료 시: 변경된 파일 목록 + 무엇을 왜 그렇게 했는지 + 확인이 필요한 지점을 요약해서 보고한다.
- **git commit / push 는 하지 않는다.** 커밋 메시지 초안만 제안하고, 사용자가 검토 후 직접 커밋·push 한다. (→ 9장 참고)
- 다음 중 하나라도 해당하면 진행 전에 사용자에게 질문한다:
  - Figma 에 없는 화면/상태를 만들어야 할 때
  - Figma 스펙이 서로 모순되거나 대응 방식이 여러 개일 때
  - 새 의존성을 추가해야 할 때
  - 기존 패키지 구조를 바꾸는 리팩터링이 필요할 때

---

## 12. 요약 체크리스트

코드를 작성/수정한 뒤 아래를 확인한다.

**아키텍처**

- [ ] View(Composable) ↔ ViewModel ↔ Model(Domain) 구조를 지켰는가?
- [ ] 기능별 패키지 아래 `model` / `view` / `viewmodel` 로 분리했는가?
- [ ] UseCase 는 정말 필요할 때만 도입했는가? Domain 이 View 에 의존하지 않는가?
- [ ] ViewModel 이 `Context` / Compose 타입을 참조하지 않는가?

**객체지향 생활 체조** (Domain 은 엄격, UiState 는 원칙 7·9 예외)

- [ ] 메서드 들여쓰기가 한 단계인가? (`else` 없이 early return 으로 풀었는가?)
- [ ] 원시 값/문자열을 의미 있는 클래스로 포장했는가?
- [ ] 한 줄에 점이 하나인가? (컬렉션 체이닝 · Modifier 체이닝 제외)
- [ ] 줄임말 없이 완전한 단어를 썼는가?
- [ ] 클래스/메서드가 작은가? (메서드 10라인 이하, 한 가지 일)
- [ ] Domain 클래스의 인스턴스 변수가 2개 이하인가?
- [ ] 컬렉션은 일급 컬렉션으로 감쌌는가?
- [ ] Domain 에서 getter/프로퍼티 노출 없이 객체에게 일을 시켰는가?
- [ ] SOLID 를 위반하지 않았는가?

**UI**

- [ ] Figma 를 먼저 확인하고 dp·색상을 그대로 반영했는가?
- [ ] dp 를 고정하지 않고 화면 비율에 대응시켰는가?
- [ ] 색상은 `colors.xml`, 문자열은 `strings.xml` 을 통해서만 참조했는가?

**기타**

- [ ] `it` 대신 명시적 파라미터 이름을 썼는가?
- [ ] 애매한 부분을 한국어 주석/이름으로 명시했는가?
- [ ] 코드·네이밍은 영어인가?
- [ ] 핵심 로직과 UI 로직이 분리되어 있는가?
- [ ] 모든 비-private 클래스에 테스트(AssertJ + JUnit5 / 계측은 JUnit4)가 있는가?
- [ ] 새 의존성을 `libs.versions.toml` 에 추가하고 `libs.*` 로 참조했는가?
