# 프로젝트 문서: Java Community

이 문서는 Gemini CLI 에이전트와 함께 진행하는 `java-community` Spring Boot 사이드 프로젝트의 개발 과정을 기록합니다.

---

## 1. 프로젝트 개요

### 1.1. 목표

Spring Boot와 JWT(JSON Web Token)를 사용하여 기본적인 인증 시스템을 갖춘 커뮤니티 백엔드 API를 구축하는 것을 목표로 하는 학습용 프로젝트입니다.

### 1.2. 기술 스택

- **언어**: Java 24
- **프레임워크**: Spring Boot 3.5.4
- **데이터베이스**: MySQL (운영), H2 (테스트)
- **인증**: Spring Security, JWT (jjwt 라이브러리)
- **빌드 도구**: Gradle

### 1.3. 주요 기능

- 사용자 회원가입
- JWT 토큰 기반 로그인/로그아웃
- API 헬스체크
- 표준화된 API 응답 형식
- 전역 예외 처리

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

---

## 3. 주요 학습 및 교훈

이번 프로젝트를 진행하며 다음과 같은 중요한 기술적 교훈을 얻었습니다.

- **`@WebMvcTest`와 Spring Security의 상호작용**
  - 공개된(public) API 엔드포인트를 테스트할 때, `@WebMvcTest`는 전체 보안 설정을 로드하려고 시도하여 테스트 실패를 유발할 수 있습니다.
  - **해결책**: `@WebMvcTest(excludeAutoConfiguration = SecurityAutoConfiguration.class)`와 같이 `SecurityAutoConfiguration`을 명시적으로 제외하여, 테스트 시에는 Spring Security의 필터 체인을 비활성화하는 것이 가장 효과적이고 깔끔한 방법입니다.

- **`@WebMvcTest`와 `@ControllerAdvice`의 동작 방식**
  - `@WebMvcTest`는 테스트 대상 컨트롤러와 동일한 패키지 또는 그 상위 패키지에 위치한 `@ControllerAdvice` Bean(`GlobalExceptionHandler` 등)을 자동으로 스캔하여 로드합니다.
  - 초기 테스트 실패의 근본 원인은 `@ControllerAdvice`를 찾지 못해서가 아니라, `SecurityConfig`와의 충돌로 인해 테스트 애플리케이션 컨텍스트가 제대로 구성되지 못했기 때문이었습니다.

- **라이브러리 API의 변경사항 준수**
  - `jjwt` 라이브러리가 0.12.x 버전으로 업데이트되면서 빌더 API가 변경되었습니다. `setExpiration()`과 같은 기존 `set` 접두사 메서드들이 deprecated 되고, `expiration()`과 같이 필드명과 동일한 메서드 이름으로 대체되었습니다.
  - 라이브러리 버전을 올릴 때는 항상 공식 문서나 릴리즈 노트를 통해 변경된 API(Breaking Changes)가 있는지 확인하는 습관이 중요합니다.

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
