# 1. Spring Security와 JWT 인증 시스템

이 문서는 `java-community` 프로젝트의 인증 및 인가 시스템을 구성하는 `SecurityConfig.java` 파일에 대해 상세히 설명합니다.

## SecurityConfig.java 전체 코드

```java
package dev.flynnpark.community.config;

import dev.flynnpark.community.auth.jwt.JwtAuthenticationFilter;
import dev.flynnpark.community.auth.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/api/v1/auth/**", "/api/v1/users", "/healthcheck").permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
```

## 1. 클래스 선언부: 보안 설정의 시작

```java
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtTokenProvider jwtTokenProvider;
}
```

- **`@Configuration`**: 이 클래스가 Spring의 설정 파일임을 나타냅니다. Spring 컨테이너는 이 클래스를 읽어 여기에 정의된 Bean들을 생성하고 관리합니다.
- **`@EnableWebSecurity`**: Spring Security의 웹 보안 기능을 활성화합니다. 이 어노테이션이 있어야 `SecurityFilterChain`을 설정하는 등 세부적인 보안 커스터마이징이 가능해집니다.
- **`@RequiredArgsConstructor`**: Lombok 어노테이션으로, `final` 키워드가 붙은 필드(`jwtTokenProvider`)를 인자로 받는 생성자를 자동으로 만들어줍니다. 이를 통해 `JwtTokenProvider` 객체를 의존성 주입(DI) 받습니다.

---

## 2. `PasswordEncoder` Bean: 안전한 비밀번호 관리

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

- **`@Bean`**: 이 메서드가 반환하는 객체(`PasswordEncoder`)를 Spring의 Bean으로 등록하라는 의미입니다.
- **역할**: 사용자의 비밀번호를 안전하게 암호화하기 위한 방법을 정의합니다.
- **`BCryptPasswordEncoder`**: Spring Security에서 권장하는 강력한 해시 알고리즘 중 하나입니다. 같은 비밀번호를 해싱하더라도 매번 결과가 달라지며(Salting), 원본 비밀번호를 유추하기 매우 어렵게 만듭니다. 회원가입 시 사용자의 비밀번호를 이 방식으로 암호화하여 데이터베이스에 저장하고, 로그인 시에는 사용자가 입력한 비밀번호를 암호화하여 저장된 값과 비교합니다.

---

## 3. `SecurityFilterChain` Bean: 핵심 보안 규칙 정의

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz
                    .requestMatchers("/api/v1/auth/**", "/api/v1/users", "/healthcheck").permitAll()
                    .anyRequest().authenticated())
            .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

    return http.build();
}
```

- **`@Bean`**: 이 메서드가 반환하는 `SecurityFilterChain`을 Bean으로 등록합니다. 이것이 바로 애플리케이션의 실제 보안 규칙을 정의하는 가장 중요한 부분입니다. `HttpSecurity` 객체를 받아 메서드 체이닝 방식으로 설정을 구성합니다.

### 3.1. CSRF 비활성화

- **코드**: `.csrf(csrf -> csrf.disable())`
- **설명**: CSRF(Cross-Site Request Forgery)는 사용자가 자신의 의지와 무관하게 공격자가 의도한 행위를 특정 웹사이트에 요청하게 하는 공격입니다. 일반적인 세션/쿠키 기반 인증 방식에서는 CSRF 공격에 취약하므로 방어 설정이 필요합니다. 하지만 JWT 토큰 기반의 **Stateless API**에서는 서버에 인증 상태를 저장하지 않으므로 CSRF 공격의 위협이 거의 없습니다. 따라서 이 기능을 비활성화(`disable`)하여 불필요한 검사를 생략합니다.

### 3.2. 세션 관리 정책 설정

- **코드**: `.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))`
- **설명**: 세션 관리 정책을 "상태가 없는(Stateless)" 방식으로 설정합니다. 즉, 서버가 클라이언트의 세션을 생성하거나 관리하지 않습니다. 모든 요청은 독립적으로 처리되며, 각 요청에 포함된 JWT 토큰을 통해서만 사용자를 인증합니다. 이는 RESTful API의 특징에 부합하는 설정입니다.

### 3.3. HTTP 요청 인가 규칙

- **코드**: `.authorizeHttpRequests(authz -> authz ...)`
- **설명**: HTTP 요청에 대한 인가(Authorization) 규칙을 설정합니다.
    - `.requestMatchers("/api/v1/auth/**", "/api/v1/users", "/healthcheck").permitAll()`: `.requestMatchers()`에 명시된 URL 패턴의 요청들은 `.permitAll()`, 즉 **누구나 접근 가능**하도록 허용합니다. (로그인, 회원가입, 헬스체크 API)
    - `.anyRequest().authenticated()`: 위에서 허용한 요청들을 제외한 **그 외의 모든 요청**은 `.authenticated()`, 즉 **인증된 사용자만 접근 가능**하도록 설정합니다.

### 3.4. 커스텀 JWT 필터 추가

- **코드**: `.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)`
- **설명**: 직접 구현한 `JwtAuthenticationFilter`를 Spring Security 필터 체인에 추가합니다.
    - 이 필터는 모든 요청의 헤더에서 JWT 토큰을 추출하고, 토큰이 유효한 경우 해당 사용자의 인증 정보를 SecurityContext에 저장하는 역할을 합니다.
    - `addFilterBefore(..., UsernamePasswordAuthenticationFilter.class)`: 이 커스텀 필터를 Spring Security의 기본 로그인 필터인 `UsernamePasswordAuthenticationFilter` **앞에** 위치시키겠다는 의미입니다. 이렇게 함으로써 모든 요청이 사용자 이름/비밀번호를 처리하는 필터에 도달하기 전에 JWT 토큰을 먼저 검사하게 됩니다.
