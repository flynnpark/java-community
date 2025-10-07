# 프로젝트 문서: Java Community

이 문서는 Gemini CLI 에이전트와 함께 진행하는 `java-community` Spring Boot 사이드 프로젝트의 개발 과정을 기록합니다.

---

## 1. 프로젝트 개요

### 1.1. 목표

Spring Boot와 JWT(JSON Web Token)를 사용하여 기본적인 인증 시스템을 갖춘 커뮤니티 백엔드 API를 구축하는 것을 목표로 하는 학습용 프로젝트입니다.

### 1.2. 기술 스택

- **언어**: Java 24
- **프레임워크**: Spring Boot 3.5.6
- **데이터베이스**: MySQL (운영/개발), H2 (테스트)
- **인증**: Spring Security, JWT (jjwt 라이브러리)
- **빌드 도구**: Gradle

### 1.3. 주요 기능

- 사용자 회원가입
- JWT 토큰 기반 로그인/로그아웃
- 게시글 생성, 목록 조회(페이지네이션), 상세 조회
- API 헬스체크
- 표준화된 API 응답 형식
- 전역 예외 처리
- 핫 리로딩 (Hot Reloading)

---

## 2. 개발 및 디버깅 로그

### 2.1. 초기 환경 설정 및 단위 테스트

- `CommonResponse`, `HealthCheckController`, `GlobalExceptionHandler` 등 핵심 모듈에 대한 단위 테스트를 구현했습니다.
- 테스트 과정에서 `DataSource` 의존성 문제, `GlobalExceptionHandler` 테스트 방식의 어려움 등 초기 문제들을 해결했습니다.

### 2.2. 로그인 기능 구현 (Spring Security & JWT)

- **의존성 추가**: `build.gradle`에 `spring-boot-starter-security`와 `jjwt` 라이브러리를 추가했습니다.
- **엔티티 수정**: `User` 엔티티에 사용자의 권한(Role)을 저장하기 위한 `roles` 필드를 추가했습니다.
- **서비스 구현**: `UserService`에서 회원가입 시 `ROLE_USER` 권한을 기본으로 부여하도록 수정했으며, `CustomUserDetailsService`가 사용자의 권한 정보를 올바르게 로드하도록 구현했습니다.
- **JWT 구현**: `JwtTokenProvider`를 통해 토큰 생성 및 검증 로직을 구현하고, `JwtAuthenticationFilter`를 통해 매 요청마다 토큰을 검사하도록 했습니다.
- **보안 설정**: `SecurityConfig`에서 CSRF 비활성화, 세션 정책을 STATELESS로 설정하고, 회원가입 및 로그인 API를 제외한 모든 요청에 인증을 요구하도록 HTTP 보안 규칙을 설정했습니다.

### 2.3. 테스트 코드 리팩토링 및 디버깅

- Spring Security 도입 후 발생한 테스트 실패 문제를 해결하는 데 많은 노력을 기울였습니다.
- **문제 원인**: `@WebMvcTest`가 `SecurityConfig`를 로드하면서, 테스트에 필요 없는 `JwtTokenProvider`와 같은 Bean을 찾지 못해 발생한 컨텍스트 로딩 실패가 주 원인이었습니다.
- **해결 과정**: `@Import` 어노테이션을 사용해 보거나 `GlobalExceptionHandler`를 직접 주입하는 등 여러 시도를 거쳤습니다. 최종적으로 `@WebMvcTest`에서 `SecurityAutoConfiguration`을 제외하는 것이 가장 효과적임을 발견하고 수정하여 모든 테스트를 통과시켰습니다.

### 2.4. Swagger (OpenAPI) 문서화 도입

- **의존성 추가**: `build.gradle`에 `springdoc-openapi-starter-webmvc-ui` 라이브러리를 추가하여 Swagger UI를 연동했습니다.
- **보안 설정 업데이트**: `SecurityConfig`에 Swagger UI 관련 엔드포인트(`v3/api-docs/**`, `/swagger-ui/**`, `/swagger-ui.html`)를 `permitAll()` 목록에 추가하여 인증 없이 접근 가능하도록 설정했습니다.
- **접근 방법**: 애플리케이션 실행 후 `http://localhost:8080/swagger-ui.html`을 통해 API 문서를 확인할 수 있도록 했습니다.

### 2.5. 게시글 API 구현 (목록/상세 조회)

- 페이지네이션을 지원하는 게시글 목록 조회 API (`GET /api/v1/posts`)를 구현했습니다.
- Spring Data JPA의 `Pageable` 객체를 사용하여 페이지 번호, 페이지 크기, 정렬 순서를 쿼리 파라미터로 받을 수 있도록 했습니다. (예: `?page=0&size=10&sort=createdAt,desc`)
- 게시글 ID를 받아 특정 게시글의 상세 정보를 조회하는 API (`GET /api/v1/posts/{id}`)를 구현했습니다.
- `@PathVariable`을 사용하여 URL 경로에서 게시글 ID를 추출했습니다.
- `SecurityConfig`를 수정하여 게시글 조회 관련 API(`GET /api/v1/posts/**`)는 인증 없이 호출할 수 있도록 `permitAll()` 설정을 추가했습니다.

### 2.6. 로컬 개발 환경 개선

- **데이터베이스 변경 (H2 -> MySQL)**: 로컬 개발 환경의 데이터베이스를 인메모리 H2에서 Docker를 이용한 MySQL로 변경하여 운영 환경과 유사한 환경에서 개발을 진행하도록 했습니다.
- `docker-compose.yml`에 정의된 MySQL 서비스를 사용하도록 `application.properties`의 데이터소스 설정을 변경했습니다.
- **환경 변수 도입**: 데이터베이스 연결 정보(URL, username, password)를 하드코딩하는 대신, `${DB_URL:기본값}`과 같은 형태로 환경 변수에서 값을 읽어오도록 `application.properties`를 리팩토링했습니다. 이를 통해 소스 코드 변경 없이 외부에서 설정을 주입할 수 있게 되었습니다.
- **핫 리로딩 적용**: `build.gradle`에 `spring-boot-devtools` 의존성을 추가하여 핫 리로딩(Hot Reloading) 기능을 활성화했습니다. 이를 통해 코드 변경 시 애플리케이션이 자동으로 재시작되어 개발 생산성을 크게 향상시켰습니다.

---

## 3. 주요 학습 및 교훈

이번 프로젝트를 진행하며 다음과 같은 중요한 기술적 교훈을 얻었습니다.

- **`@WebMvcTest`와 Spring Security의 상호작용**
  - 공개된(public) API 엔드포인트를 테스트할 때, `@WebMvcTest`는 전체 보안 설정을 로드하려고 시도하여 테스트 실패를 유발할 수 있습니다.
  - **해결책**: `@WebMvcTest(excludeAutoConfiguration = SecurityAutoConfiguration.class)`와 같이 `SecurityAutoConfiguration`을 명시적으로 제외하여, 테스트 시에는 Spring Security의 필터 체인을 비활성화하는 것이 가장 효과적이고 깔끔한 방법입니다.

- **`@WebMvcTest`와 `@ControllerAdvice`의 동작 방식**
  - `@WebMvcTest`는 테스트 대상 컨트롤러와 동일한 패키지 또는 그 상위 패키지에 위치한 `@ControllerAdvice` Bean(`GlobalExceptionHandler` 등)을 자동으로 스캔하여 로드합니다.
  - 초기 테스트 실패의 근본 원인은 `@ControllerAdvice`를 찾지 못해서가 아니라, `SecurityConfig`와의 충돌로 인해 테스트 애플리케이션 컨텍스트가 제대로 구성되지 못했기 때문이었습니다.

- **라이브러리 버전 호환성의 중요성**
  - `jjwt` 라이브러리가 0.12.x 버전으로 업데이트되면서 빌더 API가 변경되었습니다. `setExpiration()`과 같은 기존 `set` 접두사 메서드들이 deprecated 되고, `expiration()`과 같이 필드명과 동일한 메서드 이름으로 대체되었습니다.
  - Spring Boot `3.5.6` 버전에서 `springdoc-openapi:2.5.0` 라이브러리 사용 시, API 문서 페이지 접근 시 `NoSuchMethodError`가 발생하는 호환성 문제를 발견했습니다. `springdoc-openapi` 버전을 `2.8.13`으로 업데이트하여 문제를 해결했습니다.
  - **교훈**: 라이브러리 버전을 올릴 때는 항상 공식 문서나 릴리즈 노트를 통해 변경된 API(Breaking Changes)가 있는지 확인하는 습관이 중요합니다. 특히 Spring Boot와 같이 여러 라이브러리들의 의존성을 관리하는 프레임워크의 버전을 변경할 때는, 다른 라이브러리들과의 호환성을 반드시 확인해야 합니다.

- **환경 변수를 활용한 설정 관리**
  - 데이터베이스 접속 정보와 같은 민감하거나 환경에 따라 달라지는 설정은 `application.properties`에 하드코딩하기보다, 환경 변수를 통해 주입받는 것이 좋습니다.
  - Spring Boot에서는 `application.properties` 파일 내에서 `${ENV_VAR:defaultValue}` 구문을 사용하여 환경 변수가 존재하지 않을 경우 사용할 기본값을 지정할 수 있어 유연한 설정이 가능합니다.

- **코드 청결성의 중요성**
  - 사용되지 않는 `import` 구문은 코드 가독성을 해치고 잠재적인 혼란을 야기할 수 있습니다. IDE의 `Optimize Imports` 기능을 활용하거나 주기적인 코드 리뷰를 통해 코드를 항상 최적의 상태로 깔끔하게 유지하는 것이 좋습니다.

---

## 4. 프로젝트 컨벤션

### 4.1. Git 커밋 컨벤션

- **형식**: `<이모지> <제목>` (예: `✨ 로그인 기능 구현`)
- **본문**: 변경 사항의 이유를 중심으로 글머리 기호 목록으로 작성합니다.
- **언어**: 한국어
- **이모지 가이드**:
    - `✨`: 새로운 기능 추가
    - `🐛`: 버그 수정
    - `♻️`: 코드 리팩토링
    - `📝`: 문서 변경
    - `🚀`: 성능 개선
    - `✅`: 테스트 코드 관련
    - `📦`: 빌드 및 의존성

### 4.2. 언어 컨벤션

- 이 프로젝트의 모든 코드 주석, 커밋 메시지, 문서, 그리고 Gemini 에이전트와의 모든 소통은 한국어로 작성하는 것을 원칙으로 합니다.

---

*이 문서는 Gemini 에이전트와 함께 작업하며 지속적으로 업데이트됩니다.*