# SDET Take-Home – UI + API Automation (Mono Repo)

UI automation for **SauceDemo** (https://www.saucedemo.com) and API automation for **Swagger Petstore**
(https://petstore.swagger.io/v2) in a single Gradle multi-module project.

| Layer | Tooling |
|-------|---------|
| UI | Selenium 4, Page Object Model + components, Factory + ThreadLocal driver |
| API | REST Assured 5, request/response spec builders, Lombok POJOs, JSON Schema validation |
| Runner | TestNG – groups, data providers, parallel execution, retry |
| Build | Gradle multi-module (wrapper included), Java 17 |
| Reports / Logs | ExtentReports 5 (Spark HTML), Log4j2 |

---

## 1. Prerequisites
## 1. Prerequisites

- **JDK 17:** Required for compiling and running the test automation framework.
- **Git:** Required to clone the repository.
- **Browser:** Chrome is the default. Firefox and Edge are also supported where configured. Safari is available on macOS only.
- **IDE (optional):** IntelliJ IDEA or another Java IDE. Install the Lombok plugin and enable annotation processing for IDE support.

### Java configuration

Verify that Java 17 is installed:

```bash
java -version
javac -version
```

Configure `JAVA_HOME` to point to your local JDK installation if it is not already configured.

**Windows (PowerShell):**

```powershell
$env:JAVA_HOME = "C:\path\to\your\jdk-17"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
```

**macOS / Linux:**

```bash
export JAVA_HOME="/path/to/your/jdk-17"
export PATH="$JAVA_HOME/bin:$PATH"
```

Replace the example path with the actual JDK installation directory on your machine.

The project uses the Gradle Wrapper and a Java 17 toolchain. No separate Gradle installation is required.

The repository does not require a machine-specific `org.gradle.java.home` setting. Do not add local Java installation paths to committed project configuration.

### Browser drivers

Selenium Manager resolves compatible browser drivers automatically for supported browsers. Ensure the selected browser is installed and available on your machine.

## 2. How to run
```bash
# Everything (UI + API regression)
./gradlew test

# UI
./gradlew :ui-tests:test                                  # regression, headed Chrome
./gradlew :ui-tests:test -Dheadless=true
./gradlew :ui-tests:test -Psuite=smoke
./gradlew :ui-tests:test -Dbrowser=firefox -Denv=staging
./gradlew :ui-tests:test -Dui.username=performance_glitch_user

# API
./gradlew :api-tests:test                                 # regression (27 tests)
./gradlew :api-tests:test -Psuite=smoke
./gradlew :api-tests:test -Psuite=known-defects           # spec-compliance findings (fail by design)
./gradlew :api-tests:test -Denv=staging

# By TestNG group (either module)
./gradlew :api-tests:test -Pgroups=negative
./gradlew :ui-tests:test  -Pgroups=checkout
```
| Option | Values | Default |
|---|---|---|
| `-Psuite=` | `regression`, `smoke`, `known-defects` (API) | `regression` |
| `-Pgroups=` | `smoke`, `regression`, `negative`, `e2e`, `login`, `cart`, `checkout`, `chaining`, `data-driven` | – |
| `-Denv=` | `qa`, `staging` → `config/{env}.properties` | `qa` |
| `-Dbrowser=` | `chrome`, `firefox`, `edge`, `safari` | `chrome` |
| `-Dheadless=` | `true` / `false` | `false` |
| `-Dui.*` / `-Dapi.*` | override any config key | – |

Individual tests/classes can be run from the IDE (TestNG plugin) – every test is self-contained.

## 3. Outputs
| Artifact | Location |
|---|---|
| Extent HTML report | `<module>/build/reports/extent/*.html` |
| Failure screenshots (UI) | embedded in Extent + `ui-tests/build/screenshots/*.png` |
| Request/response logs (API) | embedded in Extent per test |
| Log4j2 log file | `<module>/build/logs/automation.log` |
| TestNG/Gradle report | `<module>/build/reports/tests/test/index.html` |

## 4. Project structure
```
build.gradle / settings.gradle     shared deps, Java 17 toolchain, TestNG suite/group wiring
common/                            used by both test modules
  config/ConfigManager             env properties + -D overrides
  reporting/ExtentManager          thread-safe Extent report
  listeners/TestListener           report lifecycle, screenshots on failure, Log4j
  listeners/RetryAnalyzer+Transformer  global retry (retry.count)
  utils/JsonDataReader             JSON test data -> POJOs / TestNG data providers
  resources/log4j2.xml
ui-tests/
  driver/    BrowserType, DriverFactory (factory), DriverManager (ThreadLocal)
  pages/     BasePage, LoginPage, ProductsPage, ProductDetailsPage, CartPage,
             CheckoutInformationPage, CheckoutOverviewPage, CheckoutCompletePage
  components/HeaderComponent       cart icon/badge shared by all logged-in pages
  models/    LoginCredentials, Customer, NegativeLoginCase, CheckoutValidationCase (Lombok)
  utils/     WaitUtils, ScreenshotUtils
  test: base/BaseTest, tests/{LoginTests, CartTests, CheckoutTests}
  resources: config/{qa,staging}.properties, testdata/ui/*.json, suites/*.xml
api-tests/
  specs/     RequestBuilder, ResponseSpecs, LoggingFilter
  clients/   BaseClient, PetClient, UserClient, StoreClient
  constants/ Endpoints
  models/    pet/{Pet,Category,Tag}, user/User, store/Order, common/ApiResponse (Lombok)
  data/      TestDataFactory, PetCase, NegativeCase
  test: base/BaseApiTest, tests/{PetCrudTest, PetDataDrivenTest, PetNegativeTest,
        UserStoreNegativeTest, UserFlowTest, PurchaseFlowTest, PetSpecComplianceTest}
  resources: config/{qa,staging}.properties, testdata/api/pets.json, schemas/, suites/*.xml
```

## 5. UI framework design
- **Page Object Model** – one class per page; actions return the next page, so flows read like the user journey:
  `loginAsDefaultUser().selectProduct(p).addToCart().header().openCart().proceedToCheckout()...`
  Pages contain only locators + business methods; waiting/logging lives in `BasePage`.
- **Component** – `HeaderComponent` (cart link + badge) is modelled once and composed into pages.
- **Locator strategy** – `By.id` first; otherwise XPath on `data-test` attributes (added for automation,
  immune to styling changes). Products are located by visible name, scoped to their card. No absolute or
  index-based XPaths, no CSS classes.
- **Driver lifecycle** – `DriverFactory` (Factory pattern) is the only place that builds browsers;
  `DriverManager` keeps one driver per thread (`ThreadLocal`); `BaseTest` creates it in `@BeforeMethod`
  and quits + `remove()`s it in `@AfterMethod` (`alwaysRun = true`).
- **Parallel execution** – owned by TestNG suite XML (`parallel="methods" thread-count="4"`,
  `data-provider-thread-count="3"`); code is thread-safe but makes no threading decisions.
- **Synchronisation** – explicit waits only (`WaitUtils`: visible / clickable / URL), stale elements ignored
  while polling. No implicit waits, no `Thread.sleep`.
- **Assertions** – meaningful messages; `SoftAssert` for cart details so all mismatches are reported together;
  money compared as `BigDecimal` (total = item total + tax).
- **Failure evidence** – `TestListener.onTestFailure` runs before `@AfterMethod`, so the browser is still open
  to capture the screenshot.

## 6. API framework design
- **RequestBuilder (Builder pattern)** – every request starts from one base spec (base URI, JSON, logging filter)
  and adds only what it needs: `.bearerToken()`, `.apiKey()`, `.pathParam()`, `.queryParam()`, `.body()`.
  A new builder per call keeps parallel tests isolated.
- **ResponseSpecs** – composable expectations `expect().status(200).json().withinSla().schema(...)` plus presets
  `ok()`, `okWithSchema()`, `notFound()`. Response-time SLA comes from config (`api.maxResponseMs`).
- **Clients** – `PetClient`, `UserClient`, `StoreClient` extend `BaseClient`; they only send requests and return
  `Response`, tests own the assertions (so the same `create()` serves positive and malformed-payload tests).
- **Lombok models** – `@Data @Builder(toBuilder = true)` + Jackson `NON_NULL` / `ignoreUnknown`.
  `equals()` enables whole-object request-vs-response assertions; `toBuilder()` derives the next request
  from a previous response.
- **Chaining** – `UserClient.loginAndGetToken()` extracts the session token; `withToken(token)` passes it to
  other clients. IDs flow between calls (pet id → order → order id).
- **Test data** – `TestDataFactory` builds valid defaults with unique IDs (no collisions on the shared public
  server); data-driven cases come from `testdata/api/pets.json`.
- **Logging** – `LoggingFilter` writes every request/response to Log4j2 and into the Extent test node.

## 7. Coverage
### UI (19 tests)
| Assignment step | Test |
|---|---|
| Launch, login, verify login | `standardUserCanLoginAndSeeProductsPage`, `acceptedUserCanLogin` (5 users) |
| Select product, verify details | `selectedProductDetailsMatchProductList` |
| Add to cart, verify cart (name, price, qty, badge) | `addedProductAppearsInCartWithCorrectDetails`, `emptyCartShowsNoProducts` |
| Checkout → shipping → overview → finish → confirmation | `customerCanPurchaseProductEndToEnd` |
| Negative: login | `loginIsBlockedWithCorrectErrorMessage` – wrong password, unknown user, locked user, empty username/password, case sensitivity |
| Negative: checkout | `checkoutIsBlockedWhenShippingDetailsAreMissing` – first name / last name / postal code |

### API (27 regression tests + 6 known-defect)
| Assignment item | Test |
|---|---|
| GET / POST / PUT / DELETE | `PetCrudTest`, `UserFlowTest`, `PurchaseFlowTest` |
| Status, body, field, schema validation | `ResponseSpecs`, whole-object `equals()`, `schemas/pet-schema.json` |
| Data-driven / parameterized | `PetDataDrivenTest` (5 pets from JSON), `findByStatus` ×3, `rejectedPetPayloads` |
| Invalid resource ID | unknown / negative / zero / non-numeric pet, user and order ids |
| Invalid / unsupported input | malformed JSON (400), wrong id type (500), empty body (405) |
| Missing mandatory fields, invalid auth | `PetSpecComplianceTest` (see §8) |
| Chaining / token reuse | `UserFlowTest`, `PurchaseFlowTest` |

### Sample result (headless, clean build)
```
UI Regression:  19 passed, 0 failed
API Regression: 27 passed, 0 failed
API Known Defects: 6 failed by design (spec findings)
```

## 8. Known findings / assumptions
Petstore validates far less than its own OpenAPI spec. These are captured as **failing contract tests** in
`PetSpecComplianceTest` (group `known-defect`), excluded from regression:

| Spec expectation | Actual |
|---|---|
| POST /pet without `name` / `photoUrls` → 405 | 200, pet created |
| POST /pet with `status` outside enum → 405 | 200, stored as-is |
| DELETE /pet without / with wrong `api_key` → rejected | 200, pet deleted |
| GET /user/login with invalid credentials → 400 | 200, session issued |

Other assumptions:
- Petstore has no PATCH endpoint – PUT is used for updates.
- The login session token is not enforced by Petstore; it is sent as `Authorization: Bearer` to demonstrate
  the token-chaining pattern used against real JWT/OAuth services.
- Petstore is a shared public server: tests create their own data with unique IDs and never rely on
  pre-existing records (e.g. `findByStatus` seeds its own pet).
- `qa` and `staging` point to the same public URLs (only one environment exists); the files demonstrate switching.
- SauceDemo checkout tests use `standard_user`; other users (`problem_user`, `error_user`) are intentionally buggy
  and only covered by the login test.
- Retry (`retry.count`) exists to absorb public-service flakiness, not to hide product defects.
