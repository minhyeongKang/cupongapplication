# CupongApplication

쿠팡 스타일의 쇼핑몰 웹 애플리케이션.

## 기술 스택

- **Backend**: Spring Boot 3.5, Java 17
- **View**: Thymeleaf + thymeleaf-extras-springsecurity6
- **Security**: Spring Security 6
- **Database**: MySQL (mysql-connector-j)
- **Build**: Gradle
- **Utilities**: Lombok, Spring Validation
- **Gateway**: Spring Cloud Gateway (WebFlux)

## 프로젝트 구조

```
src/
├── main/
│   ├── java/com/hellomeen/cupongapplication/
│   │   ├── CupongapplicationApplication.java   # 진입점
│   │   ├── config/        # Security, Web 설정
│   │   ├── controller/    # MVC 컨트롤러
│   │   ├── service/       # 비즈니스 로직
│   │   ├── repository/    # JPA 리포지토리
│   │   ├── entity/        # JPA 엔티티
│   │   └── dto/           # 요청/응답 DTO
│   └── resources/
│       ├── application.yaml
│       ├── static/        # CSS, JS, 이미지
│       └── templates/     # Thymeleaf 템플릿
└── test/
```

## 주요 도메인

- **회원**: 회원가입, 로그인, 마이페이지 (Spring Security 기반 인증/인가)
- **상품**: 상품 목록, 상품 상세, 카테고리
- **주문**: 장바구니, 주문, 결제
- **리뷰**: 상품 리뷰 작성/조회

## 개발 규칙

### 공통
- 패키지: `com.hellomeen.cupongapplication`
- Lombok 적극 사용 (`@Getter`, `@Builder`, `@RequiredArgsConstructor` 등)
- DTO와 Entity 분리 — 컨트롤러/서비스 경계에서 변환
- 유효성 검증은 `@Valid` + Bean Validation (`spring-boot-starter-validation`) 사용

### Spring Security
- `thymeleaf-extras-springsecurity6`로 템플릿에서 인증 정보 활용 (`sec:authorize`)
- CSRF는 기본 활성화 유지 (폼 기반 앱)
- 비밀번호는 반드시 `BCryptPasswordEncoder` 사용

### Thymeleaf
- 레이아웃은 `th:replace` / `th:insert`로 공통 헤더/푸터 분리
- Spring Security 연동: `sec:authorize="isAuthenticated()"` 등 사용

### Database
- MySQL 사용, `application.yaml`에 datasource 설정
- JPA / Spring Data JPA로 데이터 접근
- 엔티티에 `@CreatedDate`, `@LastModifiedDate` (Auditing) 활용 권장

## 빌드 & 실행

```bash
# 빌드
./gradlew build

# 실행
./gradlew bootRun

# 테스트
./gradlew test
```

## 설정 파일

`src/main/resources/application.yaml` — DB 접속 정보, 서버 포트 등 환경별 설정 관리.
민감 정보(비밀번호, API 키)는 환경변수 또는 별도 프로파일로 분리할 것.
