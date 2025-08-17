# Spring Boot & JWT 학습 프로젝트: Java Community

이 프로젝트는 Spring Boot, Spring Security, JWT(JSON Web Token) 등 백엔드 기술 스택을 학습하고 적용하기 위해 Gemini CLI 에이전트와 페어 프로그래밍 방식으로 개발한 토이 프로젝트입니다.

단순히 기능을 구현하는 것을 넘어, 테스트 코드를 작성하고, 발생한 문제를 디버깅하며, 그 과정에서 배운 점들을 상세히 기록하는 것에 중점을 두었습니다.

---

## ✨ 주요 구현 기능

- **인증 시스템**
  - Spring Security 기반의 인증 및 인가 아키텍처 구축
  - JWT (jjwt 라이브러리)를 이용한 토큰 기반 로그인 API
  - BCrypt를 사용한 안전한 비밀번호 암호화
- **API**
  - 사용자 회원가입 API
  - 표준화된 API 응답 형식 (`CommonResponse`)
  - 전역 예외 처리 (`GlobalExceptionHandler`)
- **API 문서**
  - `springdoc-openapi`를 이용한 Swagger UI 자동 생성
- **테스트**
  - JUnit5를 사용한 서비스 및 컨트롤러 단위 테스트
  - Spring Security 환경에서의 테스트 코드 작성 및 디버깅

## 📖 API 문서

애플리케이션 실행 후, 웹 브라우저에서 다음 URL로 접속하여 API 명세를 확인할 수 있습니다.

- **Swagger UI**: `http://localhost:8080/swagger-ui.html`

## 🛠️ 기술 스택

- **언어**: Java 24
- **프레임워크**: Spring Boot 3.5.4
- **인증/인가**: Spring Security, JWT (jjwt 라이브러리)
- **데이터베이스**: MySQL (운영), H2 (테스트)
- **빌드 도구**: Gradle

## 📝 개발 및 학습 로그

이 프로젝트의 진짜 핵심은 결과물 코드뿐만 아니라, 코드를 만들어가는 과정 그 자체에 있습니다. 초기 설정부터 기능 구현, 수많은 테스트 실패와 디버깅, 그리고 그 과정에서 무엇을 배웠는지에 대한 상세한 로그를 아래 문서에 모두 기록했습니다.

- **[➡️ 전체 개발 및 학습 로그 보러가기 (GEMINI.md)](./GEMINI.md)**